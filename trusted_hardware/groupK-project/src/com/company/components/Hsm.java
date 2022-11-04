package com.company.components;

import com.company.crypto.Asymmetric;
import com.company.crypto.Symmetric;
import com.company.keys_management.Domain;
import com.company.keys_management.DomainKeys;
import com.company.keys_management.QuorumAttestation;
import com.company.keys_management.Trust;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Hsm {
    private PrivateKey priv_key;        // Private key asymmetric crypto
    private PublicKey pub_key;          // Public key asymmetric crypto
    private static SecretKey master_key;       // Secret key
    private String hsmname;
    private Socket socket;
    private ObjectInputStream objectReader;
    private ObjectOutputStream objectWriter;

    // ---------------------------------------------------------------------

    public Hsm(Socket socket, String hsmname) {
        try {
            this.socket = socket;
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
            this.hsmname = hsmname;

            // Generate asymmetric key pair, master key and fresh key
            KeyPair key_pair = Asymmetric.generateRSAKeyPair();
            this.priv_key = key_pair.getPrivate();
            this.pub_key = key_pair.getPublic();
            this.master_key = Symmetric.createAESKey();
        } catch ( IOException e ) {
            e.printStackTrace();
            //closeEverything(socket, objectReader, objectWriter);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------

    public void sendWhoIAm(){
        try {
            if ( socket.isConnected() ) {
                HelloMessage hello = new HelloMessage(hsmname, pub_key);
                objectWriter.writeObject(hello);
                objectWriter.flush();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            //closeEverything(socket, objectReader, objectWriter);
        }
    }

    public void sendMessage(KSMMessage message) {
        try {
            if ( socket.isConnected() ) {
                objectWriter.writeObject(message);
                objectWriter.flush();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
            //closeEverything(socket, objectReader, objectWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                KSMMessage msgFromKMS;

                while ( socket.isConnected() ) {
                    try {
                        System.out.println();
                        System.out.println("----------------------------------------------------------------------------------------------------");
                        System.out.println("Waiting for tasks to do....");
                        msgFromKMS = (KSMMessage) objectReader.readObject();
                        Integer operation = msgFromKMS.getOperation();

                        System.out.print("  TASK == ");
                        if ( operation == 1 ) {
                            System.out.println("Encrypt data to " + msgFromKMS.getHostname() + " (" + msgFromKMS.getDomain().getTrust().getId() + ").");
                            System.out.println("----------------------------------------------------------------------------------------------------");

                            // encrypt data
                            byte[] encryptedBytes = encryptOperation(msgFromKMS);

                            // send encrypted data through socket
                            msgFromKMS.setResponseMessage(encode(encryptedBytes));
                            sendMessage(msgFromKMS);
                        } else if ( operation == 2 ) {
                            System.out.println("Decrypt data to " + msgFromKMS.getHostname() + " (" + msgFromKMS.getDomain().getTrust().getId() + ").");
                            System.out.println("----------------------------------------------------------------------------------------------------");

                            // decrypt data
                            String dec = decryptOperation(msgFromKMS);

                            // send decrypted data through socket
                            msgFromKMS.setResponseMessage(dec);
                            sendMessage(msgFromKMS);
                        } else if ( operation == 3 ) {
                            System.out.println("Create domain keys, sign initial trust and sign domain of " + msgFromKMS.getHostname());
                            System.out.println("----------------------------------------------------------------------------------------------------");

                            // generate domain keys
                            Domain new_domain = msgFromKMS.getDomain();
                            DomainKeys domain_keys = generateDomainKeys(new_domain.getTrust().getDomainPublicKeys().getHsmsPublicKeys());
                            new_domain.setDomainKeys(domain_keys);

                            // sign initial trust and domain
                            Trust initial_trust = new_domain.getTrust();
                            KSMMessage responseMessage;
                            Trust signed_trust = signTrust(initial_trust);
                            signed_trust.setPreviousHash("");
                            new_domain.setTrust(signed_trust);
                            Domain signed_domain = signDomain(new_domain);
                            responseMessage = new KSMMessage(4, signed_domain, msgFromKMS.getHostname() );
                            sendMessage(responseMessage);
                        } else if ( operation == 5 ) {
                            System.out.println("Verify domain, trust and attestation entities signatures of " + msgFromKMS.getOldTrust().getId());

                            // sign trust
                            Trust new_trust = msgFromKMS.getNewTrust();
                            Trust old_trust = msgFromKMS.getOldTrust();
                            KSMMessage responseMessage;
                            if ( updateTrustVerification(new_trust, old_trust) && newEntitiesVerification(new_trust, old_trust)) {
                                for( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
                                    if ( attestation.getEntityName().contains("operator-") )
                                        new_trust.getDomainPublicKeys().getOperatorsPublicKeys().add(attestation.getEntity());
                                    if ( attestation.getEntityName().contains("hsm-") )
                                        new_trust.getDomainPublicKeys().getHsmsPublicKeys().add(attestation.getEntity());
                                }
                                DomainKeys new_domain_keys = generateDomainKeys(new_trust.getDomainPublicKeys().getHsmsPublicKeys());
                                Trust trust_to_sign = new Trust(new_trust.getId(), new_trust.getDomainPublicKeys(), new_trust.getQuorumSignsRequired());
                                Trust signed_trust = signTrust(trust_to_sign);

                                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                                byte[] encodedHash = digest.digest(convertToBytes(signed_trust));
                                String hash = bytesToHex(encodedHash);
                                signed_trust.setPreviousHash(hash);
                                responseMessage = new KSMMessage(6, signed_trust, new_domain_keys, old_trust);
                            } else {
                                // error on trust verification
                                System.out.println("Can not trust on Domain Trust. Update domain failed.");
                                responseMessage = new KSMMessage(6, (Trust) null, (Trust) null);
                            }
                            System.out.println("----------------------------------------------------------------------------------------------------");
                            sendMessage(responseMessage);
                        } else if ( operation == 7 ) {
                            System.out.println("Rotate domain keys of " + msgFromKMS.getHostname() + " domain.");

                            Domain domain = msgFromKMS.getDomain();

                            // trust verifications
                            if ( rotateTrustVerification(domain.getTrust()) ) {
                                DomainKeys keys = generateDomainKeys(domain.getTrust().getDomainPublicKeys().getHsmsPublicKeys());
                                domain.setDomainKeys(keys);
                                KSMMessage responseMessage = new KSMMessage(8, domain, msgFromKMS.getHostname());
                                sendMessage(responseMessage);
                            } else {
                                System.out.println("Can not trust on Domain Trust. Rotate domain keys failed.");
                                KSMMessage responseMessage = new KSMMessage(8, null, msgFromKMS.getHostname());
                                sendMessage(responseMessage);
                            }
                            System.out.println("----------------------------------------------------------------------------------------------------");

                        }
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        //closeEverything(socket, objectReader, objectWriter);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream bufferedReader, ObjectOutputStream bufferedWriter) {
        try {
            if ( bufferedReader != null )
                bufferedReader.close();
            if ( bufferedWriter != null )
                bufferedWriter.close();
            if ( socket != null )
                socket.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------------

    public PublicKey getPubKey() {
        return pub_key;
    }

    private SecretKey createFreshKey() {
        SecretKey fresh_key = null;
        try {
            fresh_key = Symmetric.createAESKey();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        return fresh_key;
    }

    private DomainKeys generateDomainKeys(LinkedList<PublicKey> domain_public_keys)  {
        // Generate new fresh domain key
        SecretKey fresh_key = createFreshKey();

        // Wrap key with domain public keys
        return wrapKey(fresh_key,domain_public_keys);
    }

    private DomainKeys wrapKey(SecretKey fresh_key, LinkedList<PublicKey> domain_public_keys) {
        // Encrypt master key using fresh_key
        byte[] byte_cipher_master_key = Symmetric.aesEncrypt(master_key.getEncoded(), fresh_key);
        String string_cipher_master_key = encode(byte_cipher_master_key);

        // Wrap fresh key with domain PKs foreach public key on domain
        HashMap<PublicKey, String> domain_keys = new HashMap<>();
        for ( PublicKey pub_key : domain_public_keys ) {

            // Encrypt fresh key using public key
            byte[] byte_cipher_domain_key = Asymmetric.rsaEncrypt(fresh_key.getEncoded(), pub_key);
            String string_cipher_domain_key = encode(byte_cipher_domain_key);

            // Associate public key with encrypted fresh key
            domain_keys.put(pub_key, string_cipher_domain_key);
        }

        // Build DomainKeys object
        return new DomainKeys(string_cipher_master_key, domain_keys);
    }

    private SecretKey unWrapKey(DomainKeys domain_key) throws UnsupportedEncodingException {

        // Decrypt symmetric fresh key associated to this public key, using this priv key
        String domain_key_token = domain_key.getDomainKeys().get(pub_key);
        byte[] decoded_domain_key_token = decode(domain_key_token);
        byte[] bytes_fresh_key = Asymmetric.rsaDecrypt(decoded_domain_key_token, priv_key);
        //SecretKey fresh_key = new SecretKeySpec(bytes_fresh_key, 0, bytes_fresh_key.length, "AES/GCM/NoPadding");
        SecretKey fresh_key = new SecretKeySpec(bytes_fresh_key, "AES");

        // Decrypt domain master key, using symmetric fresh key
        String master_key_token = domain_key.getMasterToken();
        byte[] decoded_master_key_token = decode(master_key_token);
        byte[] bytes_domain_master_key = Symmetric.aesDecrypt(decoded_master_key_token, fresh_key);
        //SecretKey domain_master_key = new SecretKeySpec(bytes_domain_master_key, 0, bytes_symmetric_key.length, "AES/GCM/NoPadding");
        SecretKey domain_master_key = new SecretKeySpec(bytes_domain_master_key, "AES");

        return domain_master_key;
    }

    private byte[] encryptOperation(KSMMessage message) throws UnsupportedEncodingException {

        Domain domain = message.getDomain();

        // verify if this hsm belongs to domain
        for ( PublicKey key : domain.getTrust().getDomainPublicKeys().getHsmsPublicKeys() ) {
            if ( key.equals(pub_key) ) {
                // Unwrap domain key
                SecretKey domain_master_key = unWrapKey(domain.getDomainKeys());

                // Encrypt data
                byte[] byte_string = message.getRequestMessage().getBytes();
                byte[] enc = Symmetric.aesEncrypt(byte_string,domain_master_key);
                return enc;
            }
        }
        return null;
    }

    public String decryptOperation(KSMMessage message) throws UnsupportedEncodingException {

        byte[] encrypted = decode(message.getRequestMessage());

        if ( encrypted == null ) {
            return new String("* * * ERROR! Decrypt message failed");
        } else {
            // Unwrap domain key
            SecretKey domain_master_key = unWrapKey(message.getDomain().getDomainKeys());

            byte[] dec = Symmetric.aesDecrypt(encrypted,domain_master_key);

            return new String(dec);
        }
    }

    private Domain signDomain(Domain new_domain) throws IOException {
        // Sign domain keys object only because trust can change on some time, and then the verification will fail if we sign entire domain object.
        byte[] signature = signOperation(new_domain.getDomainKeys());
        new_domain.setSignature(signature);
        return new_domain;
    }

    private Trust signTrust(Trust trust) throws IOException {
        Trust trust_to_sign = new Trust(trust.getId(), trust.getDomainPublicKeys(), trust.getQuorumSignsRequired());
        byte[] signature = signOperation(trust_to_sign);
        trust.setSignature(signature);
        return trust;
    }

    private byte[] signOperation(Object object) throws IOException {
        byte[] bytes_object = convertToBytes(object);
        byte[] signed_bytes_object = Asymmetric.rsaSignMessage(bytes_object, priv_key);
        return signed_bytes_object;
    }

    private boolean verifyTrustSignature(PublicKey key, Trust trust) throws IOException {
        byte[] signature = trust.getSignature();
        Trust trust_to_verify_signature = new Trust(trust.getId(), trust.getDomainPublicKeys(), trust.getQuorumSignsRequired());
        byte[] bytes_object = convertToBytes(trust_to_verify_signature);

        return Asymmetric.rsaVerifyMessage(signature, bytes_object, key);
    }

    private boolean newEntitiesVerification(Trust new_trust, Trust old_trust) {
        // New hsms verification
        LinkedList<PublicKey> new_hsms_entities = new LinkedList<>();
        new_hsms_entities.addAll(new_trust.getDomainPublicKeys().getHsmsPublicKeys());
        for( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
            if ( attestation.getEntityName().contains("hsm-") )
                new_hsms_entities.add(attestation.getEntity());
        }
        for ( PublicKey new_hsm : new_hsms_entities ) {
            // verify if every entity of the new trust is either in the old trust,
            boolean hsm_in_old_trust = false;
            for ( PublicKey old_hsm : old_trust.getDomainPublicKeys().getHsmsPublicKeys() ) {
                if ( new_hsm.equals(old_hsm) ) {
                    hsm_in_old_trust = true;
                    break;
                }
            }

            // or is attested by a quorum of operators of the existing trust
            if ( !hsm_in_old_trust ) {
                for ( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
                    if ( attestation.getEntity().equals(new_hsm) ) {
                        // verify attested signatures with operators pubkeys in the old domain trust
                        Integer attested_signatures_verified = 0;
                        for ( byte[] attested_signature : attestation.getAttestedOperatorsSigns() ) {
                            for ( PublicKey operator : old_trust.getDomainPublicKeys().getOperatorsPublicKeys() ) {
                                if ( Asymmetric.rsaVerifyMessage(attested_signature, attestation.getEntity().getEncoded(), operator) ) {
                                    System.out.println("Attested signature was signed by an Operator that belongs to the domain.");
                                    attested_signatures_verified++;
                                    break;
                                }
                            }
                        }

                        // if attested signatures verified number is greater or equal to quorum number, this entity were attested.
                        // if not, trust verification it will fail.
                        if ( attested_signatures_verified < new_trust.getQuorumSignsRequired() ) {
                            // hsm entity don't have necessary attestations yet
                            System.out.println(attestation.getEntityName() + " entity don't have the necessary attestations signatures yet.");
                            return false;
                        }
                        break;
                    }
                }
            }
        }

        // New operators attestations verification
        LinkedList<PublicKey> new_operators_entities = new LinkedList<>();
        new_operators_entities.addAll(new_trust.getDomainPublicKeys().getOperatorsPublicKeys());
        for( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
            if ( attestation.getEntityName().contains("operator-") )
                new_operators_entities.add(attestation.getEntity());
        }
        for ( PublicKey new_operator : new_operators_entities ) {
            // verify if every entity of the new trust is either in the old trust,
            boolean operator_in_old_trust = false;
            for ( PublicKey old_operator : old_trust.getDomainPublicKeys().getOperatorsPublicKeys() ) {
                if ( new_operator.equals(old_operator) ) {
                    operator_in_old_trust = true;
                    break;
                }
            }

            // or is attested by a quorum of operators of the existing trust
            if ( !operator_in_old_trust ) {
                for ( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
                    if ( attestation.getEntity().equals(new_operator) ) {
                        // verify attested signatures with operators pubkeys in the old domain trust
                        Integer attested_signatures_verified = 0;
                        for ( byte[] attested_signature : attestation.getAttestedOperatorsSigns() ) {
                            for ( PublicKey operator : old_trust.getDomainPublicKeys().getOperatorsPublicKeys() ) {
                                if ( Asymmetric.rsaVerifyMessage(attested_signature, attestation.getEntity().getEncoded(), operator) ) {
                                    System.out.println("Attested signature was verified.");
                                    attested_signatures_verified++;
                                    break;
                                }
                            }
                        }

                        // if attested signatures verified number is greater or equal to quorum number, this entity were attested.
                        // if not, trust verification it will fail.
                        if ( attested_signatures_verified < new_trust.getQuorumSignsRequired() ) {
                            // hsm entity don't have necessary attestations yet
                            System.out.println(attestation.getEntityName() + " entity don't have the necessary attestations signatures yet.");
                            return false;
                        }
                        break;
                    }
                }
            }
        }
        return true;
    }

    private boolean updateTrustVerification(Trust new_trust, Trust old_trust) throws IOException, NoSuchAlgorithmException {
        boolean hsm_in_domain = false;
        boolean trust_consistence = false;

        // checking hash of old_trust object
        if ( old_trust.getPreviousHash().equals("") )
            System.out.println("Hash of previous trust is correct. It is first trust");
        else {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            Trust trust_to_calculate_hash = new Trust(old_trust.getId(),old_trust.getDomainPublicKeys(), old_trust.getQuorumSignsRequired(), old_trust.getSignature());
            byte[] encodedHash = digest.digest(convertToBytes(trust_to_calculate_hash));
            String hash = bytesToHex(encodedHash);
            if ( !hash.equals(old_trust.getPreviousHash()) ) {
                System.out.println("Hash of previous trust doesn't match.");
                System.out.println("HASH = " + hash);
                return false;
            } else {
                System.out.println("Hash of previous trust is correct.");
                System.out.println("HASH = " + hash);
            }
        }


        for( PublicKey key : new_trust.getDomainPublicKeys().getHsmsPublicKeys() ) {
            // verify if this hsm belongs existing trust of domain
            if ( !hsm_in_domain && key.equals(pub_key)  ) {
                hsm_in_domain = true;
            }

            // verify if trust( trust object without signature) is signed by HSM in existing trust
            if ( !trust_consistence && verifyTrustSignature(key, old_trust) ) {
                trust_consistence = true;
            }
        }

        if ( hsm_in_domain )
            System.out.println("This hsm belongs to the domain");
        else
            System.out.println("This hsm not belongs to the domain");

        if ( trust_consistence )
            System.out.println("Domain Trust is signed by an Hsm that belongs to the domain");
        else
            System.out.println("Domain Trust is not signed by an Hsm that belongs to the domain");

        // final verification: Entities on new trust
        if ( hsm_in_domain && trust_consistence ) {
            return true;
        } else
            return false;
    }

    private boolean rotateTrustVerification(Trust trust) throws NoSuchAlgorithmException, IOException {
        boolean hsm_in_domain = false;
        boolean trust_consistence = false;

        if ( trust.getPreviousHash().equals("") ) {
            System.out.println("Hash of trust is correct. It is the first trust");
        } else {
            // checking hash of trust object
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            Trust trust_to_calculate_hash = new Trust(trust.getId(),trust.getDomainPublicKeys(), trust.getQuorumSignsRequired(), trust.getSignature());
            byte[] encodedHash = digest.digest(convertToBytes(trust_to_calculate_hash));
            String hash = bytesToHex(encodedHash);
            if ( hash.equals(trust.getPreviousHash()) ) {
                System.out.println("Hash of trust is correct.");
                System.out.println("HASH = " + hash);
            } else {
                System.out.println("Hash of trust doesn't match.");
                System.out.println("HASH = " + hash);
                return false;
            }
        }


        for( PublicKey key : trust.getDomainPublicKeys().getHsmsPublicKeys() ) {
            // verify if this hsm belongs existing trust of domain
            if ( !hsm_in_domain && key.equals(pub_key)  ) {
                hsm_in_domain = true;
                System.out.println("This hsm belongs to the domain");
            } else {
                System.out.println("This hsm not belongs to the domain");
            }

            // verify if trust( trust object without signature) is signed by HSM in existing trust
            if ( !trust_consistence && verifyTrustSignature(key, trust) ) {
                trust_consistence = true;
                System.out.println("Domain Trust is signed by an Hsm that belongs to the domain");
            } else {
                System.out.println("Domain Trust is not signed by an Hsm that belongs to the domain");

            }
        }

        // final verification: Entities on new trust
        if ( hsm_in_domain && trust_consistence ) {
            return true;
        } else
            return false;
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bis)) {
            return in.readObject();
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        try {
            return Base64.getDecoder().decode(data);
        } catch ( Exception e) {
            System.out.println("* * * * ERROR! Decoded message failed.");
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter this hsm name for the key management system: ");
        String hsmname = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Hsm hsm = new Hsm(socket, hsmname);
        hsm.listenForMessage();
        hsm.sendWhoIAm();
    }
}
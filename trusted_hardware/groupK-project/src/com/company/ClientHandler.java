package com.company;

import com.company.components.HelloMessage;
import com.company.components.KSMMessage;
import com.company.components.OperatorMessage;
import com.company.keys_management.Domain;
import com.company.keys_management.EntitiesPublicKeys;
import com.company.keys_management.QuorumAttestation;
import com.company.keys_management.Trust;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.*;

public class ClientHandler implements Runnable {
    private static StoredDomains hosts_mapped_domains = new StoredDomains();
    public static ArrayList<ClientHandler> hsmHandlers = new ArrayList<>();
    public static ArrayList<ClientHandler> hostHandlers = new ArrayList<>();
    public static ArrayList<ClientHandler> operatorHandlers = new ArrayList<>();
    public static ArrayList<Domain> new_domains = new ArrayList<>();
    public static ArrayList<Trust> update_trusts = new ArrayList<>();
    private Socket socket;
    private ObjectInputStream objectReader;
    private ObjectOutputStream objectWriter;
    private String clientUsername;
    private PublicKey clientPubkey;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
            HelloMessage hello = (HelloMessage) objectReader.readObject();
            this.clientUsername = hello.getClientUsername();
            this.clientPubkey = hello.getPubKey();

            if ( this.clientUsername.contains("hsm-") ) {
                System.out.println("A new hsm has connected");
                hsmHandlers.add(this);
            } else if ( this.clientUsername.contains("host-") ) {
                System.out.println("A new host has connected");
                hostHandlers.add(this);
            } else if ( this.clientUsername.contains("operator-") ) {
                System.out.println("A new operator has connected");
                operatorHandlers.add(this);
            } else {
                System.out.println(" **** ERRRRROR Unknown entity ****");
            }
        } catch (IOException | ClassNotFoundException e ) {
            closeEverything(socket, objectReader, objectWriter);
        }
    }

    @Override
    public void run() {
        while ( socket.isConnected() ) {
            try {
                if ( clientUsername.contains("hsm-") ){
                    KSMMessage messageFromHsm = (KSMMessage) objectReader.readObject();
                    deserializeHsmMessage(messageFromHsm);
                } else if ( clientUsername.contains("host-") ) {
                    KSMMessage messageFromHost = (KSMMessage) objectReader.readObject();
                    Integer host_handler = Integer.valueOf(clientUsername.substring(clientUsername.indexOf("host-")+5));
                    if ( hosts_mapped_domains.containsHandler(host_handler) ) {
                        // Encrypt and decrypt operations
                        Domain host_domain = hosts_mapped_domains.getDomain(host_handler);
                        messageFromHost.setHostname(clientUsername);
                        messageFromHost.setDomain(host_domain);
                        sendMessageToRandomDomainHsm(messageFromHost);
                    } else
                        System.out.println(" ERRORRRRR! Host " + clientUsername + " doesn't have an referer domain");
                } else if ( clientUsername.contains("operator-") ) {
                    OperatorMessage messageFromOperator = (OperatorMessage) objectReader.readObject();
                    deserializeOperatorMessage(messageFromOperator);
                }
            } catch ( IOException e ) {
                e.printStackTrace();
                closeEverything(socket, objectReader, objectWriter);
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void deserializeHsmMessage(KSMMessage messageFromHsm) {
        if ( messageFromHsm.getOperation() == 1 || messageFromHsm.getOperation() == 2) {
            // encrypt and decrypt operation
            for ( ClientHandler clientHandler : hostHandlers ) {
                if (clientHandler.clientUsername.equals(messageFromHsm.getHostname())) {
                    sendMessageToHost(clientHandler, messageFromHsm);
                    break;
                }
            }
        } else if ( messageFromHsm.getOperation() == 4 ) {
            // create domain operation
            Domain new_domain = messageFromHsm.getDomain();
            for ( Domain domain : new_domains ) {
                if( domain.getTrust().getId().equals(new_domain.getTrust().getId()) ) {
                    Integer host_handler = Integer.valueOf(messageFromHsm.getHostname().substring(messageFromHsm.getHostname().indexOf("host-")+5));
                    hosts_mapped_domains.insertDomain(host_handler, new_domain);
                    new_domains.remove(domain);
                    break;
                }
            }

            System.out.println(messageFromHsm.getHostname() + " !");
            System.out.println("Hsm entities: ");
            for ( PublicKey hsm_domain : new_domain.getTrust().getDomainPublicKeys().getHsmsPublicKeys() ) {
                for ( ClientHandler hsm : hsmHandlers ) {
                    if ( hsm.clientPubkey.equals(hsm_domain) ) {
                        System.out.println(" " + hsm.clientUsername);
                        break;
                    }
                }
            }
            System.out.println("Operator entities: ");
            for ( PublicKey operator_domain : new_domain.getTrust().getDomainPublicKeys().getOperatorsPublicKeys() ) {
                for ( ClientHandler operator : operatorHandlers ) {
                    if ( operator.clientPubkey.equals(operator_domain) ) {
                        System.out.println(" " + operator.clientUsername);
                        break;
                    }
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println();
        } else if ( messageFromHsm.getOperation() == 6 ) {
            // update domain operation
            Trust new_trust = messageFromHsm.getNewTrust();
            Trust old_trust = messageFromHsm.getOldTrust();
            if ( new_trust != null && old_trust != null ) {

                // removing trust from update_trusts list
                for (Trust loading_trust : update_trusts) {
                    if (loading_trust.getId().equals(new_trust.getId())) {
                        update_trusts.remove(loading_trust);
                        break;
                    }
                }

                // getting domain that belongs the new trust to update
                for (Integer host_handler : hosts_mapped_domains.getHandlers()) {
                    if (hosts_mapped_domains.getDomain(host_handler).getTrust().getId().equals(old_trust.getId())) {
                        Domain domain_to_update = hosts_mapped_domains.getDomain(host_handler);
                        domain_to_update.setTrust(new_trust);
                        domain_to_update.setDomainKeys(messageFromHsm.getNewDomainKeys());
                        hosts_mapped_domains.replaceDomain(host_handler, domain_to_update);
                        System.out.println("Domain of host-" + host_handler + " was updated successfully !");

                        System.out.println("Hsm entities: ");
                        for (PublicKey hsm_domain : new_trust.getDomainPublicKeys().getHsmsPublicKeys()) {
                            for (ClientHandler hsm : hsmHandlers) {
                                if (hsm.clientPubkey.equals(hsm_domain)) {
                                    System.out.println(" " + hsm.clientUsername);
                                    break;
                                }
                            }
                        }
                        System.out.println("Operator entities: ");
                        for (PublicKey operator_domain : new_trust.getDomainPublicKeys().getOperatorsPublicKeys()) {
                            for (ClientHandler operator : operatorHandlers) {
                                if (operator.clientPubkey.equals(operator_domain)) {
                                    System.out.println(" " + operator.clientUsername);
                                    break;
                                }
                            }
                        }
                        System.out.println("----------------------------------------------------------------------------------------------------");
                        System.out.println();
                        break;
                    }
                }

            } else {
                System.out.println(" Nothing happened. The trust verification failed.");
                System.out.println("----------------------------------------------------------------------------------------------------");
                System.out.println();
            }
        } else if ( messageFromHsm.getOperation() == 8 ) {
            // rotate domain keys operation
            Domain updated_domain_keys_domain = messageFromHsm.getDomain();
            if ( updated_domain_keys_domain == null ) {
                System.out.println(" * * * * ERROR! Rotate Domain Keys on " + messageFromHsm.getHostname() + " domain failed.");
            } else {
                System.out.println(" Rotate Domain Keys on " + messageFromHsm.getHostname() + " domain were successfully refreshed.");
                // getting domain that belongs to new domain keys
                for ( Integer handler : hosts_mapped_domains.getHandlers() ) {
                    if ( hosts_mapped_domains.getDomain(handler).getTrust().getId().equals(updated_domain_keys_domain.getTrust().getId()) )  {
                        hosts_mapped_domains.replaceDomain(handler,updated_domain_keys_domain);
                        break;
                    }
                }
            }
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println();
        }
    }

    private void deserializeOperatorMessage(OperatorMessage message) throws NoSuchAlgorithmException, IOException {
        Integer operation = message.getOperation();
        if ( operation == 1 ) {
            LinkedList<String> hostnames = new LinkedList<>();
            for ( ClientHandler host : hostHandlers )
                hostnames.add(host.clientUsername);

            LinkedList<String> hsm_names = new LinkedList<>();
            for ( ClientHandler hsm : hsmHandlers )
                hsm_names.add(hsm.clientUsername);

            LinkedList<String> operator_names = new LinkedList<>();
            for ( ClientHandler operator : operatorHandlers )
                operator_names.add(operator.clientUsername);

            OperatorMessage responseMessage = new OperatorMessage(2, hostnames, hsm_names, operator_names);
            sendResponseMessageToOperator(responseMessage);
        } else if ( operation == 11 ) {
            // create domain request
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println(" New domain received from "+ clientUsername + " to " + message.getHostname());

            LinkedList<PublicKey> operatorsPublicKeys = new LinkedList<>();
            for ( ClientHandler operator : operatorHandlers ) {
                for ( String domain_operators : message.getOperatorNames() ) {
                    if ( operator.clientUsername.equals(domain_operators) ) {
                        operatorsPublicKeys.add(operator.clientPubkey);
                        break;
                    }
                }
            }

            LinkedList<PublicKey> hsmsPublicKeys = new LinkedList<>();
            for ( ClientHandler hsm : hsmHandlers ) {
                for ( String domain_hsm : message.getHsmNames() ) {
                    if ( hsm.clientUsername.equals(domain_hsm) ) {
                        hsmsPublicKeys.add(hsm.clientPubkey);
                        break;
                    }
                }
            }

            Integer domain_id = hosts_mapped_domains.getStoredDomainsNumber() + new_domains.size();
            String identifier = "domain-" + domain_id + "trust-0";
            EntitiesPublicKeys publicKeys = new EntitiesPublicKeys(operatorsPublicKeys, hsmsPublicKeys);
            Trust trust = new Trust(identifier, publicKeys, message.getQuorumSize());
            Domain domain = new Domain(trust);
            new_domains.add(domain);

            // create domain keys, sign initial trust and sign referer domain
            System.out.print("Create domain keys, sign initial trust and sign referer domain on ");
            KSMMessage hsm_message = new KSMMessage(3, domain, message.getHostname());
            sendMessageToRandomDomainHsm(hsm_message);
        } else if ( operation == 3 ) {
            // update domain request
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println(" Update domain received from "+ clientUsername);

            LinkedList<String> hsm_names = new LinkedList<>();
            for ( ClientHandler hsm : hsmHandlers )
                hsm_names.add(hsm.clientUsername);

            LinkedList<String> operator_names = new LinkedList<>();
            for ( ClientHandler operator : operatorHandlers )
                operator_names.add(operator.clientUsername);

            OperatorMessage response_message = new OperatorMessage(4,hosts_mapped_domains.getStoredDomainsState(), hsm_names, operator_names);
            sendResponseMessageToOperator(response_message);
        } else if ( operation == 5 ) {
            Domain old_domain = message.getDomain();
            Trust old_trust = old_domain.getTrust();

            // new hsms to domain
            LinkedList<QuorumAttestation> new_hsms_attestation = new LinkedList<>();
            for ( ClientHandler hsm : hsmHandlers ) {
                for ( String new_hsm : message.getHsmNames() ) {
                    if ( hsm.clientUsername.equals(new_hsm) && !old_trust.getDomainPublicKeys().getHsmsPublicKeys().contains(hsm.clientPubkey) ) {
                        new_hsms_attestation.add(new QuorumAttestation(hsm.clientPubkey, hsm.clientUsername));
                        break;
                    }
                }
            }

            // new operators to domain
            LinkedList<QuorumAttestation> new_operators_attestation = new LinkedList<>();
            for ( ClientHandler operator : operatorHandlers ) {
                for ( String new_operator : message.getOperatorNames() ) {
                    if ( operator.clientUsername.equals(new_operator) && !old_trust.getDomainPublicKeys().getOperatorsPublicKeys().contains(operator.clientPubkey) ) {
                        new_operators_attestation.add(new QuorumAttestation(operator.clientPubkey, operator.clientUsername));
                        break;
                    }
                }
            }

            // Concatenate attestation lists
            LinkedList<QuorumAttestation> new_entities_attestation = new LinkedList<>();
            new_entities_attestation.addAll(new_hsms_attestation);
            new_entities_attestation.addAll(new_operators_attestation);

            // setting new trust identifier
            String old_trust_identifier = old_trust.getId();
            String prefix_new_id = old_trust_identifier.substring(0,old_trust_identifier.indexOf("trust-")+6);
            Integer suffix_new_id = Integer.valueOf(old_trust_identifier.substring(old_trust_identifier.indexOf("trust-")+6));
            suffix_new_id++;
            String new_id = prefix_new_id + suffix_new_id;

            Trust new_trust = new Trust(new_id, old_trust.getDomainPublicKeys(), old_trust.getQuorumSignsRequired(), new_entities_attestation);
            update_trusts.add(new_trust);

            // getting referer host to this domain
            String hostname = "";
            for ( Integer host_handler : hosts_mapped_domains.getHandlers() ) {
                if ( hosts_mapped_domains.getDomain(host_handler).getTrust().getId().equals(old_domain.getTrust().getId()) ) {
                    hostname = "host-" + host_handler;
                    break;
                }
            }

            // send attestations requests
            OperatorMessage broadcastMessage = new OperatorMessage(6, new_trust, old_trust, hostname);
            broadcastAttestationMessageToDomainOperators(broadcastMessage);
        } else if ( operation == 7 ) {
            Trust updated_trust = message.getNewTrust();

            String identifier = updated_trust.getId();
            Trust old_trust = message.getOldTrust() ;

            for ( Trust loading_trust : update_trusts ) {
                if ( loading_trust.getId().equals(identifier) ) {
                    for ( QuorumAttestation attestation_loading : loading_trust.getNewEntitiesAttestation() ) {
                        for ( QuorumAttestation new_attestation : updated_trust.getNewEntitiesAttestation() ) {
                            if ( attestation_loading.getEntity().equals(new_attestation.getEntity()) ) {
                                 attestation_loading.getAttestedOperatorsSigns().addAll(new_attestation.getAttestedOperatorsSigns());
                                 break;
                            }
                        }
                    }

                    // check if attested signatures number of each new entity is greater or equal to Quorum signs required
                    boolean new_entities_attested = true;
                    for ( QuorumAttestation attestation_loading : loading_trust.getNewEntitiesAttestation() ) {
                        if ( attestation_loading.getAttestedOperatorsSigns().size() < loading_trust.getQuorumSignsRequired() ) {
                            new_entities_attested = false;
                            break;
                        }
                    }

                    // verify attested signatures to validate this trust
                    if ( new_entities_attested ) {
                        System.out.println("old trust HASH = "+ old_trust.getPreviousHash() );
                        KSMMessage hsm_message = new KSMMessage(5, loading_trust, old_trust);
                        sendMessageToRandomDomainHsm(hsm_message);
                    } else {
                        System.out.println("Nothing happened. The trust still need entities attestations.");
                        System.out.println();
                    }
                    break;
                }
            }
        } else if ( operation == 8 ) {
            // remove domain request
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println(" Remove domain received from "+ clientUsername);
            OperatorMessage response_message = new OperatorMessage(9,hosts_mapped_domains.getStoredDomainsState());
            sendResponseMessageToOperator(response_message);
        } else if ( operation == 10 ) {
            // remove selected domain
            Domain selected_domain = message.getDomain();

            for ( Integer host_handler : hosts_mapped_domains.getHandlers() ) {
                if ( hosts_mapped_domains.getDomain(host_handler).getTrust().getId().equals(selected_domain.getTrust().getId()) ) {
                    hosts_mapped_domains.removeDomain(host_handler);
                    System.out.println("Domain of host-" + host_handler + " was removed successfully !");
                    System.out.println("----------------------------------------------------------------------------------------------------");
                    break;
                }
            }
        } else if ( operation == 12 ) {
            System.out.println("----------------------------------------------------------------------------------------------------");
            System.out.println(" Rotate domain keys received from "+ clientUsername);
            OperatorMessage response_message = new OperatorMessage(13,hosts_mapped_domains.getStoredDomainsState());
            sendResponseMessageToOperator(response_message);
        } else if ( operation == 14 ) {
            // rotate keys to selected domain
            Domain selected_domain = message.getDomain();
            System.out.println("receive");
            for ( Integer handler : hosts_mapped_domains.getHandlers() ) {
                if ( handler.equals(message.getHostHandler()) ) {
                    String hostname = "host-" + handler;
                    System.out.println("HASH = " + hosts_mapped_domains.getDomain(handler).getTrust().getPreviousHash());
                    
                    KSMMessage hsm_message = new KSMMessage(7, hosts_mapped_domains.getDomain(handler), hostname);
                    sendMessageToRandomDomainHsm(hsm_message);
                    break;
                }
            }
        }
    }

    private void broadcastAttestationMessageToDomainOperators(OperatorMessage message) {
        for ( ClientHandler clientHandler : operatorHandlers ) {
            for (PublicKey domain_operator_key : message.getOldTrust().getDomainPublicKeys().getOperatorsPublicKeys()) {
                if (clientHandler.clientPubkey.equals(domain_operator_key)) {
                    try {
                        clientHandler.objectWriter.writeObject(message);
                        clientHandler.objectWriter.flush();
                    } catch (IOException e) {
                        closeEverything(socket, objectReader, objectWriter);
                    }
                }
            }
        }
    }
    private void sendResponseMessageToOperator(OperatorMessage response_message) {
        try {
            objectWriter.writeObject(response_message);
            objectWriter.flush();
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        }
    }
    private void sendMessageToHost(ClientHandler host, KSMMessage message) {
        try {
            host.objectWriter.writeObject(message);
            host.objectWriter.flush();
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        }
    }
    public void sendMessageToRandomDomainHsm(KSMMessage message) {
        Integer hsms_domain_size = 0;
        Trust trust = null;
        if ( message.getOperation() == 3 || message.getOperation() == 1 || message.getOperation() == 2 || message.getOperation() == 7 ) {
            // create domain message case
            hsms_domain_size = message.getDomain().getTrust().getDomainPublicKeys().getHsmsPublicKeys().size();
            trust = message.getDomain().getTrust();
        } else if ( message.getOperation() == 5 ) {
            // update trust message case
            hsms_domain_size = message.getNewTrust().getDomainPublicKeys().getHsmsPublicKeys().size();
            trust = message.getNewTrust();
        }

        PublicKey choosen_domain_hsm;
        if ( hsms_domain_size > 0 ) {
            Random rand = new Random();
            int int_random = rand.nextInt(hsms_domain_size);
            choosen_domain_hsm = trust.getDomainPublicKeys().getHsmsPublicKeys().get(int_random);

            for ( ClientHandler hsm : hsmHandlers ) {
                if ( hsm.clientPubkey.equals(choosen_domain_hsm) ) {
                    try {
                        hsm.objectWriter.writeObject(message);
                        hsm.objectWriter.flush();
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        closeEverything(socket, objectReader, objectWriter);
                    }
                    break;
                }
            }
        } else {
            System.out.println(" *** ERROR! The domain " + message.getDomain().getTrust().getId() + " doesn't have hsms associated.");
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

    public void closeEverything(Socket socket, ObjectInputStream objectReader, ObjectOutputStream objectWriter) {
        if ( this.clientUsername.contains("hsm-") )
            hsmHandlers.remove(this);
        else if ( this.clientUsername.contains("host-") )
            hostHandlers.remove(this);
        else if ( this.clientUsername.contains("operator-") )
            operatorHandlers.remove(this);

        try {
            if ( objectReader != null )
                objectReader.close();
            if ( objectWriter != null )
                objectWriter.close();
            if ( socket != null )
                socket.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}

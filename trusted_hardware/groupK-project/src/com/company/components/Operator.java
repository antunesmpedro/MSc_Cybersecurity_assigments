package com.company.components;
import com.company.crypto.Asymmetric;
import com.company.keys_management.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Operator {
    private PrivateKey priv_key;        // Private key asymmetric crypto

    private PublicKey pub_key;          // Public key asymmetric crypto
    private String operatorname;
    private Socket socket;
    private ObjectInputStream objectReader;
    private ObjectOutputStream objectWriter;

    private static ReentrantLock reentrantLock = new ReentrantLock();
    public Operator(Socket socket, String operatorname) {
        try {
            this.socket = socket;
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
            this.operatorname = operatorname;

            // Generate asymmetric key pair, master key and fresh key
            KeyPair key_pair = Asymmetric.generateRSAKeyPair();
            this.priv_key = key_pair.getPrivate();
            this.pub_key = key_pair.getPublic();
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void sendWhoIAm(){
        try {
            if ( socket.isConnected() ) {
                HelloMessage hello = new HelloMessage(operatorname, pub_key);
                objectWriter.writeObject(hello);
                objectWriter.flush();
            }
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        }
    }

    public void sendMessage(OperatorMessage message) {
        try {
            if ( socket.isConnected() ) {
                objectWriter.writeObject(message);
                objectWriter.flush();
            }
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OperatorMessage msgFromKMS;

                while ( socket.isConnected() ) {
                    /*
                    System.out.println("This program will be on Attested mode.");
                    System.out.println("Do you want to produce some actions on Key Management System? (Y/n)");
                    Scanner scanner = new Scanner(System.in);
                    String option = scanner.nextLine();

                    if ( option.equals("y") || option.equals("Y") ) {
                        menu();
                    } else {

                     */
                    try {
                        Scanner scanner = new Scanner(System.in);
                        msgFromKMS = (OperatorMessage) objectReader.readObject();
                        Integer operation = msgFromKMS.getOperation();
                        if ( operation == 2 ) {
                            reentrantLock.lock();
                            createDomain(msgFromKMS);
                        } else if ( operation == 4 ) {
                            reentrantLock.lock();
                            // update one domain from domains list
                            HashMap<Integer, Domain> domains = msgFromKMS.getDomains();
                            System.out.println("\n  Hosts that has domain belongs to this operator on KMS:");
                            LinkedList<Integer> handler_domains_authorized_to_operator = new LinkedList<>();
                            for ( Integer handler : domains.keySet() ) {
                                Domain domain = domains.get(handler);
                                LinkedList<PublicKey> operators_belongs_to_domain = domain.getTrust().getDomainPublicKeys().getOperatorsPublicKeys();
                                if ( operators_belongs_to_domain.contains(pub_key) ) {
                                    System.out.println("    host-" + handler);
                                    handler_domains_authorized_to_operator.add(handler);
                                }
                            }

                            if ( handler_domains_authorized_to_operator.size() == 0 ) {
                                System.out.println("  * * * * ERROR! This operator does not belongs to any host domain.");
                                System.out.println(" Press enter to continue to the menu...");
                                String hostname = scanner.nextLine();
                                reentrantLock.unlock();
                                menu();
                            } else {
                                System.out.println("\n  Which host domain do you want to update: ");
                                String hostname = scanner.nextLine();
                                Integer host_handler = Integer.valueOf(hostname.substring(hostname.indexOf("host-")+5));
                                if ( !handler_domains_authorized_to_operator.contains(host_handler) ) {
                                    System.out.println("\n  * * * * ERROR! This operator has no authorized access to chosen host domain, or the domain does not exist.");
                                    System.out.println(" Press enter to continue to the menu...");
                                    hostname = scanner.nextLine();
                                    reentrantLock.unlock();
                                    menu();
                                } else {
                                    Domain selected_domain = domains.get(host_handler);
                                    updateDomain(selected_domain, msgFromKMS.getHsmNames(), msgFromKMS.getOperatorNames());
                                }
                            }
                        } else if ( operation == 6 ) {

                            reentrantLock.lock();

                            Trust new_trust = msgFromKMS.getNewTrust();
                            String hostname = msgFromKMS.getHostname();


                            System.out.println("New entities are configured on " + hostname + " domain.");

                            // attest hsm entity on domain
                            for ( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
                                if ( attestation.getEntityName().contains("hsm-") ) {
                                    System.out.println("   " + attestation.getEntityName() + " entity was set on this domain.");
                                    System.out.println("   Do you want to attest this entity ? (Y/n)");
                                    String attest_response = scanner.nextLine();
                                    if ( attest_response.equals("y") || attest_response.equals("Y") ) {
                                        byte[] bytes_pk_entity_attestation = attestation.getEntity().getEncoded();
                                        byte[] signature = Asymmetric.rsaSignMessage(bytes_pk_entity_attestation,priv_key);
                                        attestation.attestEntity(signature);
                                    }
                                }
                            }

                            // attest new operator entity on domain
                            for ( QuorumAttestation attestation : new_trust.getNewEntitiesAttestation() ) {
                                if ( attestation.getEntityName().contains("operator-") ) {
                                    System.out.println("   " + attestation.getEntityName() + " entity was set on this domain.");
                                    System.out.println("   Do you want to attest this entity ? (Y/n)");
                                    String attest_response = scanner.nextLine();
                                    if ( attest_response.equals("y") || attest_response.equals("Y") ) {
                                        byte[] bytes_pk_entity_attestation = attestation.getEntity().getEncoded();
                                        byte[] signature = Asymmetric.rsaSignMessage(bytes_pk_entity_attestation,priv_key);
                                        attestation.attestEntity(signature);
                                    }
                                }
                            }

                            // send response message with attested signatures
                            OperatorMessage message = new OperatorMessage(7, new_trust, msgFromKMS.getOldTrust());
                            sendMessage(message);
                            reentrantLock.unlock();
                            menu();
                        } else if ( operation == 9 ) {
                            reentrantLock.lock();
                            // remove one domain from domains list
                            HashMap<Integer, Domain> domains = msgFromKMS.getDomains();
                            System.out.println("\n  Hosts that has domain belongs to this operator on KMS:");
                            LinkedList<Integer> handler_domains_authorized_to_operator = new LinkedList<>();
                            for ( Integer handler : domains.keySet() ) {
                                Domain domain = domains.get(handler);
                                LinkedList<PublicKey> operators_belongs_to_domain = domain.getTrust().getDomainPublicKeys().getOperatorsPublicKeys();
                                if ( operators_belongs_to_domain.contains(pub_key) ) {
                                    System.out.println("    host-" + handler);
                                    handler_domains_authorized_to_operator.add(handler);
                                }
                            }
                            System.out.println("\n  Which host domain do you want to remove: ");
                            String hostname = scanner.nextLine();
                            Integer host_handler = Integer.valueOf(hostname.substring(hostname.indexOf("host-")+5));
                            if ( !handler_domains_authorized_to_operator.contains(host_handler) ) {
                                System.out.println("\n  * * * * ERROR! This operator has no authorized access to chosen host domain, or the domain does not exist.");
                                System.out.println(" Press enter to continue to the menu...");
                                hostname = scanner.nextLine();
                            } else {
                                Domain selected_domain = domains.get(host_handler);
                                // send remove selected domain request
                                OperatorMessage message = new OperatorMessage(10, selected_domain);
                                sendMessage(message);
                            }
                            reentrantLock.unlock();
                            menu();
                        } else if ( operation == 13 ) {
                            reentrantLock.lock();
                            // rotate keys of one domain
                            HashMap<Integer, Domain> domains = msgFromKMS.getDomains();
                            System.out.println("\n  Hosts that has domain belongs to this operator on KMS:");
                            LinkedList<Integer> handler_domains_authorized_to_operator = new LinkedList<>();
                            for ( Integer handler : domains.keySet() ) {
                                Domain domain = domains.get(handler);
                                LinkedList<PublicKey> operators_belongs_to_domain = domain.getTrust().getDomainPublicKeys().getOperatorsPublicKeys();
                                if ( operators_belongs_to_domain.contains(pub_key) ) {
                                    System.out.println("    host-" + handler);
                                    handler_domains_authorized_to_operator.add(handler);
                                }
                            }

                            if ( handler_domains_authorized_to_operator.size() == 0 ) {
                                System.out.println("  * * * * ERROR! This operator does not belongs to any host domain.");
                                System.out.println(" Press enter to continue to the menu...");
                                String hostname = scanner.nextLine();
                                reentrantLock.unlock();
                                menu();
                            } else {
                                System.out.println("\n  Which host domain do you want to rotate keys: ");
                                String hostname = scanner.nextLine();
                                Integer host_handler = Integer.valueOf(hostname.substring(hostname.indexOf("host-")+5));
                                if ( !handler_domains_authorized_to_operator.contains(host_handler) ) {
                                    System.out.println("\n  * * * * ERROR! This operator has no authorized access to chosen host domain, or the domain does not exist.");
                                    System.out.println(" Press enter to continue to the menu...");
                                    hostname = scanner.nextLine();
                                } else {
                                    Domain selected_domain = domains.get(host_handler);
                                    // send rotate keys selected domain request
                                    System.out.println("send");
                                    OperatorMessage message = new OperatorMessage(14, selected_domain, host_handler);
                                    sendMessage(message);
                                }
                                reentrantLock.unlock();
                                menu();
                            }
                        }
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        closeEverything(socket, objectReader, objectWriter);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
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

    public PublicKey getPubKey() {
        return pub_key;
    }

    public void createDomain(OperatorMessage message) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n  Hosts on KMS:");
        for ( String hostname : message.getHostnames() )
            System.out.println("    " + hostname);
        System.out.println("\n  Enter which host belongs to the domain: ");
        String domain_hostname = scanner.nextLine();

        System.out.println("\n  HSMs available: ");
        for ( String hsmname : message.getHsmNames() )
            System.out.println("    " + hsmname);
        LinkedList<String> hsms_domain = new LinkedList<>();
        System.out.println("\n  Enter which hsms belongs to the domain: (0 to break)");
        while ( scanner.hasNext() ) {
            String domain_hsm = scanner.nextLine();
            if ( domain_hsm.equals("0"))
                break;
            else
                hsms_domain.add(domain_hsm);
        }

        System.out.println("\n  Operators available: ");
        for ( String operatorname : message.getOperatorNames() )
            System.out.println("    " + operatorname);
        LinkedList<String> operators_domain = new LinkedList<>();
        System.out.println("\n  Enter which operators belongs to the domain: (0 to break)");
        while ( scanner.hasNext() ) {
            String domain_operator = scanner.nextLine();
            if ( domain_operator.equals("0"))
                break;
            else
                operators_domain.add(domain_operator);
        }

        System.out.println("  Enter which qualified operators set number needed to validate some changes on this domain: ");
        String domain_quorum_number = scanner.nextLine();

        OperatorMessage response_message = new OperatorMessage(11, domain_hostname, hsms_domain, operators_domain, Integer.valueOf(domain_quorum_number));
        sendMessage(response_message);
        reentrantLock.unlock();
        menu();
    }

    public void updateDomain(Domain old_domain, LinkedList<String> hsms_available, LinkedList<String> operators_available) {
        System.out.println("\n  HSMs available: ( !please choose not belonging to the domain ones)");
        for ( String hsm : hsms_available )
            System.out.println(hsm);
        System.out.println();
        System.out.println("  Which hsms do you want to add (0 to break): ");

        Scanner scanner = new Scanner(System.in);
        LinkedList<String> new_hsms = new LinkedList<>();
        while ( scanner.hasNext() ) {
            String hsm_name = scanner.nextLine();
            if ( hsm_name.equals("0") )
                break;
            new_hsms.add(hsm_name);
        }

        System.out.println("\n  Operators available: ( !please choose not belonging to the domain ones)");
        for ( String operator : operators_available )
            System.out.println(operator);
        System.out.println();
        System.out.println("  Which operators do you want to add (0 to break): ");

        LinkedList<String> new_operators = new LinkedList<>();
        while ( scanner.hasNext() ) {
            String operator_name = scanner.nextLine();
            if ( operator_name.equals("0") )
                break;
            new_operators.add(operator_name);
        }

        OperatorMessage message = new OperatorMessage(5, old_domain, new_hsms, new_operators);
        sendMessage(message);
        reentrantLock.unlock();
        menu();
    }

    public void menu() {
        reentrantLock.lock();
        System.out.println(" ------------------------------------------------ ");
        System.out.println(" -----------           MENU           ----------- ");
        System.out.println("     1 - Create Domain");
        System.out.println("     2 - Update Domain");
        System.out.println("     3 - Remove Domain");
        System.out.println("     4 - Attest Entities");
        System.out.println("     5 - Rotate Domain Keys");
        System.out.println(" ------------------------------------------------ ");
        System.out.println("");
        System.out.println(" Option: ");
        Scanner scanner = new Scanner(System.in);
        Integer option = Integer.valueOf(scanner.nextLine());

        if ( option == 1 ) {
            reentrantLock.unlock();
            OperatorMessage message = new OperatorMessage(1);
            sendMessage(message);
        } else if ( option == 2 ) {
            reentrantLock.unlock();
            OperatorMessage message = new OperatorMessage(3);
            sendMessage(message);
        } else if ( option == 3 ) {
            reentrantLock.unlock();
            OperatorMessage  message = new OperatorMessage(8);
            sendMessage(message);
            //menu();
        } else if ( option == 4 ) {
            reentrantLock.unlock();
            // just listen new message from KMS
        } else if ( option == 5 ) {
            reentrantLock.unlock();
            OperatorMessage message = new OperatorMessage(12);
            sendMessage(message);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter this operator name for the key management system: ");
        String operatorname = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Operator operator = new Operator(socket, operatorname);
        operator.listenForMessage();
        operator.sendWhoIAm();
        operator.menu();
    }
}
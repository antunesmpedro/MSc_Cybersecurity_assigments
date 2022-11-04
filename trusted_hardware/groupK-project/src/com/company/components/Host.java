package com.company.components;

import com.company.crypto.Asymmetric;
import com.company.keys_management.Domain;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.Scanner;

public class Host {
    private PrivateKey priv_key;        // Private key asymmetric crypto
    private PublicKey pub_key;          // Public key asymmetric crypto
    private Integer handler;            // Domain Handler
    private String hostname;
    private Socket socket;
    private ObjectInputStream objectReader;
    private ObjectOutputStream objectWriter;


    public Host(Socket socket, String hostname) {
        try {
            this.socket = socket;
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
            this.hostname = hostname;

            // Generate asymmetric key pair
            KeyPair key_pair = Asymmetric.generateRSAKeyPair();
            this.priv_key = key_pair.getPrivate();
            this.pub_key = key_pair.getPublic();
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWhoIAm(){
        try {
            if ( socket.isConnected() ) {
                HelloMessage hello = new HelloMessage(hostname, pub_key);
                objectWriter.writeObject(hello);
                objectWriter.flush();
            }
        } catch ( IOException e ) {
            closeEverything(socket, objectReader, objectWriter);
        }
    }

    public void sendMessage(KSMMessage message) {
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
                KSMMessage msgFromKMS;

                while ( socket.isConnected() ) {
                    try {
                        msgFromKMS = (KSMMessage) objectReader.readObject();
                        Integer operation = msgFromKMS.getOperation();

                        if ( operation == 1 ) {
                            // encrypted data
                            showEncryptedData(msgFromKMS.getResponseMessage(), msgFromKMS.getRequestMessage());
                        } else if ( operation == 2 ) {
                            // decrypted data
                            showDecryptedData(msgFromKMS.getResponseMessage(), msgFromKMS.getRequestMessage());
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

    private void showDecryptedData(String dec, String encrypted_b64) {
        System.out.println("Decrypted Operation successful.\nEncrypted message: " + encrypted_b64 + "\nDecrypted message: " + dec);
        System.out.println("-------------------------------------------------");
    }

    private void showEncryptedData(String enc_b64, String original) {
        System.out.println("Encrypted Operation successful.\nOriginal message: " + original + "\nEncrypted message: " + enc_b64);
        System.out.println("-------------------------------------------------");
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

    public void setHandler(Integer handler) {
        this.handler = handler;
    }

    public void menu() {
        System.out.println(" ------------------------------------------------ ");
        System.out.println(" -----------           MENU           ----------- ");
        System.out.println("     1 - Encrypt Message");
        System.out.println("     2 - Decrypt Message");
        System.out.println(" ------------------------------------------------ ");
        System.out.println("");
        System.out.println(" Option: ");
        Scanner scanner = new Scanner(System.in);
        Integer option =  Integer.valueOf(scanner.nextLine());

        if (option == 1) {
            System.out.println("Enter Message: ");
            String input_message = scanner.nextLine();

            KSMMessage message = new KSMMessage(option, input_message);
            sendMessage(message);

            // block window screen
            input_message = scanner.nextLine();
            menu();
        } else if (option == 2) {
            System.out.println("Enter Cipher Message: ");
            String encrypted = scanner.nextLine();

            KSMMessage message = new KSMMessage(option, encrypted);
            sendMessage(message);

            // block window screen
            encrypted = scanner.nextLine();
            menu();
        }
    }

    public static void main (String[]args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter this hostname for the key management system: ");
        String hostname = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Host host = new Host(socket, hostname);
        host.listenForMessage();
        host.sendWhoIAm();
        host.menu();
    }
}
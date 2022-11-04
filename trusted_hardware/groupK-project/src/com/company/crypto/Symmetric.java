package com.company.crypto;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

// Class to create a
// symmetric key
public class Symmetric {

    public static final String AES
            = "AES";

    //private static final Integer GCM_IV_LENGTH = 16;
    private static final Integer GCM_IV_LENGTH = 12;
    private static final Integer GCM_TAG_LENGTH = 128;

    // Function to create a secret key
    public static SecretKey createAESKey() throws Exception {

        Security.setProperty("crypto.policy", "unlimited");
        //int maxKeySize = javax.crypto.Cipher.getMaxAllowedKeyLength("AES");
        //System.out.println("Max Key Size for AES : " + maxKeySize);

        // Creating a new instance of
        // SecureRandom class.
        SecureRandom secure_random = new SecureRandom();

        // Passing the string to
        // KeyGenerator
        KeyGenerator keygenerator = KeyGenerator.getInstance(AES);

        // Initializing the KeyGenerator
        // with 256 bits.
        keygenerator.init(256, secure_random);

        return keygenerator.generateKey();
    }

    /**
     * Function that Generate initial vector to be used on AES encryption
     * */
    private static byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];

        // Creating a new instance of
        // SecureRandom class.
        SecureRandom secure_random = new SecureRandom();

        secure_random.nextBytes(iv);

        return iv;
    }

    public static byte[] aesEncrypt(byte[] original, SecretKey key) {
        try {
            // Generating GCM parameters with random IV
            byte[] iv = generateIv();
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH,iv);

            // Creating a Cipher object
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Initializing a Cipher object
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            // Encrypt the data
            byte[] cipher_bytes = cipher.doFinal(original);

            // Sending IV on beginning of encrypted array
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipher_bytes.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipher_bytes);
            return byteBuffer.array();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] aesDecrypt(byte[] encrypted, SecretKey key) {
        try {
            // Extracting and use first 12 bytes for IV from encrypted array
            AlgorithmParameterSpec gcmIv = new GCMParameterSpec(GCM_TAG_LENGTH, encrypted, 0, GCM_IV_LENGTH);

            // Creating a Cipher object
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Initializing a Cipher object
            cipher.init(Cipher.DECRYPT_MODE, key, gcmIv);

            /* ASSOCIATED DATA OPTIONAL
            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }
             */

            // use everything from 12 bytes on as ciphertext and decrypt the data
            return cipher.doFinal(encrypted, GCM_IV_LENGTH, encrypted.length - GCM_IV_LENGTH);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

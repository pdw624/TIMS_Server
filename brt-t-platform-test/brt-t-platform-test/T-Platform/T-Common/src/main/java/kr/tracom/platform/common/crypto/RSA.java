package kr.tracom.platform.common.crypto;

import javax.crypto.Cipher;
import java.security.*;

public class RSA {

    public static void test2() throws NoSuchProviderException, NoSuchAlgorithmException {

    }

    public static void test() throws Exception {
        // generate public and private keys
        KeyPair keyPair = buildKeyPair();
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.println("----------------------------------------------------");
        // encrypt the message
        byte [] encrypted = encrypt(privateKey, "TP-2017-01,BIS_CHANNEL,8083,0,0,1,A,EUC-KR,little,65,65535,256,6,3,3,0,1,0,0TP-2017-01,BIS_CHANNEL,8083,0,0,1,A,EUC-KR,little,65,65535,256,6,3,3,0,1,0,0");
        System.out.println(byteArrayToHex(encrypted));  // <<encrypted message>>

        System.out.println("----------------------------------------------------");

        // decrypt the message
        byte[] secret = decrypt(pubKey, encrypted);
        System.out.println(new String(secret));     // This is a secret message

        System.out.println("----------------------------------------------------");
    }

    public static String byteArrayToHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        SecureRandom random = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize, random);

        KeyPair keyPair = keyPairGenerator.genKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] publicKeyBytes = publicKey.getEncoded();

        System.out.println("Private Key : " + byteArrayToHex(privateKeyBytes));
        System.out.println("Public Key : " + byteArrayToHex(publicKeyBytes));


        return keyPairGenerator.genKeyPair();
    }

    public static byte[] encrypt(PrivateKey privateKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(message.getBytes());
    }

    public static byte[] decrypt(PublicKey publicKey, byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(encrypted);
    }
}

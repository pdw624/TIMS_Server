package kr.tracom.platform.common.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256 {
    public static String encode(String plainText) {
        StringBuilder builder = new StringBuilder();

        MessageDigest mDigest;
        try {
            mDigest = MessageDigest.getInstance("SHA-256");
            mDigest.update(plainText.getBytes());

            byte[] msgStr = mDigest.digest() ;

            for(int i=0; i < msgStr.length; i++){
                byte tmpStrByte = msgStr[i];
                String tmpEncTxt = Integer.toString((tmpStrByte & 0xff) + 0x100, 16).substring(1);

                builder.append(tmpEncTxt) ;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }
}

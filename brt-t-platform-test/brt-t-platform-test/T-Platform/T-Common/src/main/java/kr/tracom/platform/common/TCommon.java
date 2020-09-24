package kr.tracom.platform.common;


import kr.tracom.platform.common.crypto.RSA;

public class TCommon {
    public static void main(String[] args) {
        /*
        String licenseDate = "TP-2017-01,BIS_CHANNEL,8083,0,0,1,A,EUC-KR,little,65,65535,256,6,3,3,0,1,0,0";
        String strHash;
        try {
            strHash = BCrypt.hashpw(licenseDate, BCrypt.gensalt(5));

            System.out.println(strHash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        strHash = Sha256.encode(licenseDate);

        System.out.println(strHash);
        */

        try {
            RSA.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

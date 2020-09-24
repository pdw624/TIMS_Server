package kr.tracom.platform.launcher.manager;

import kr.tracom.platform.common.crypto.Sha256;

public class CryptoManager {
    private static final String ALGORITHM = "SHA";

    public static String encode(String plainText) {
        if("SHA".equalsIgnoreCase(ALGORITHM)) {
            return Sha256.encode(plainText);
        }
        return "-";
    }
}

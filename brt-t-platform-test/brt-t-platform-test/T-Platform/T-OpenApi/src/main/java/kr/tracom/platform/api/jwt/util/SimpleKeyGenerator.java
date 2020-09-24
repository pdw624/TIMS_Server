package kr.tracom.platform.api.jwt.util;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class SimpleKeyGenerator implements KeyGenerator {

    // ======================================
    // =          Business methods          =
    // ======================================

    @Override
    public Key generateKey() {
        String keyString = "GJ-SONG";
        Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
        return key;
    }
}
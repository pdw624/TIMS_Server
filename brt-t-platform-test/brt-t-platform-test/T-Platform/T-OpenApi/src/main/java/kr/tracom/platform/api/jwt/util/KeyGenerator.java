package kr.tracom.platform.api.jwt.util;

import java.security.Key;

public interface KeyGenerator {
    Key generateKey();
}

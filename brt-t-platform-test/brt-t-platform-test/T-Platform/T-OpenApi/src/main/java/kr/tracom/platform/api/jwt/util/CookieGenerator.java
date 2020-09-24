package kr.tracom.platform.api.jwt.util;

import javax.ws.rs.core.NewCookie;

public class CookieGenerator {
    public static final String COOKIE_NAME = "access_token";

    public static NewCookie generateCookie(String accessToken) {
        String path = "/";
        String domain = null;
        String comment = null;
        int maxAge = 15 * 60;
        boolean secure = false;

        NewCookie cookie = new NewCookie(COOKIE_NAME, accessToken, path, domain, comment, maxAge, secure);

        return cookie;
    }
}

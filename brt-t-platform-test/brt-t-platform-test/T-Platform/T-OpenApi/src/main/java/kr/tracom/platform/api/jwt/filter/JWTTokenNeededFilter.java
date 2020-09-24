package kr.tracom.platform.api.jwt.filter;

import io.jsonwebtoken.Jwts;
import kr.tracom.platform.api.jwt.util.CookieGenerator;
import kr.tracom.platform.api.jwt.util.KeyGenerator;
import kr.tracom.platform.api.jwt.util.SimpleKeyGenerator;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;


@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTTokenNeededFilter implements ContainerRequestFilter {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        filterCookie(requestContext);

    }

    private void filterCookie(ContainerRequestContext requestContext) {
        Cookie cookie = requestContext.getCookies().get(CookieGenerator.COOKIE_NAME);
        if (null != cookie) {
            String token = cookie.getValue();

            try {
                // Validate the token
                Key key = keyGenerator.generateKey();
                Jwts.parser().setSigningKey(key).parseClaimsJws(token);
                logger.info("#### valid token : " + token);

            } catch (Exception e) {
                logger.info("#### invalid token : " + token);
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            }
        }
    }

    private void filterHeader(ContainerRequestContext requestContext) {
        // Get the HTTP Authorization header from the request
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        logger.info("#### authorizationHeader : " + authorizationHeader);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("#### invalid authorizationHeader : " + authorizationHeader);
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {

            // Validate the token
            Key key = keyGenerator.generateKey();
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            logger.info("#### valid token : " + token);

        } catch (Exception e) {
            logger.info("#### invalid token : " + token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
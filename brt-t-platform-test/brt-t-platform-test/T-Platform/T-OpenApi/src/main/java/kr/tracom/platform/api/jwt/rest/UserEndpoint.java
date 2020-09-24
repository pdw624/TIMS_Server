package kr.tracom.platform.api.jwt.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.net.URI;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import kr.tracom.platform.api.domain.User;
import kr.tracom.platform.api.jwt.util.CookieGenerator;
import kr.tracom.platform.api.jwt.util.KeyGenerator;
import kr.tracom.platform.api.jwt.util.SimpleKeyGenerator;

/**
 * @author Antonio Goncalves
 *         http://www.antoniogoncalves.org
 *         --
 */
@Path("/users")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class UserEndpoint {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    @POST
    @Path("/login")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Response authenticateUser(@Context UriInfo uriInfo,
                                     @FormParam("login") String login,
                                     @FormParam("password") String password) {
        try {
            logger.info("#### login/password : " + login + "/" + password);

            // Authenticate the user using the credentials provided
            authenticate(login, password);

            // Issue a token for the user
            String token = issueToken(login, uriInfo);

            logger.info("#### token : " + token);

            // Return the token on the response
            //return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();

            return Response.seeOther(URI.create("/static/index.html"))
                    .cookie(CookieGenerator.generateCookie(token)).build();

        } catch (Exception e) {
            return Response.status(UNAUTHORIZED).build();
        }
    }

    private void authenticate(String login, String password) throws Exception {
        /*
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("login", login);
        query.setParameter("password", PasswordUtils.digestPassword(password));
        User user = query.getSingleResult();
        */
        User user = new User();
        user.setId(login);
        user.setPassword(password);

        //if (user == null)
        //    throw new SecurityException("Invalid user/password");
    }

    private String issueToken(String login, UriInfo uriInfo) {
        Key key = keyGenerator.generateKey();
        String jwtToken = Jwts.builder()
                .setSubject(login)
                .setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        logger.info("#### generating token for a key : " + jwtToken + " - " + key);

        return jwtToken;

    }

    @POST
    public Response create(User user) {
        //em.persist(user);
        //return Response.created(uriInfo.getAbsolutePathBuilder().path(user.getId()).build()).build();
        return Response.ok(user).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        //User user = em.find(User.class, id);
        // User user = null;

        // if (user == null)
            return Response.status(NOT_FOUND).build();
        
        // return Response.ok(user).build();
    }

    @GET
    public Response findAllUsers() {
        /*
        TypedQuery<User> query = em.createNamedQuery(User.FIND_ALL, User.class);
        List<User> allUsers = query.getResultList();
        */
        // List<User> allUsers = null;

        // if (allUsers == null)
            return Response.status(NOT_FOUND).build();

       //  return Response.ok(allUsers).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remove(@PathParam("id") String id) {
        //em.remove(em.getReference(User.class, id));

        return Response.noContent().build();
    }

    // ======================================
    // =          Private methods           =
    // ======================================

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}

package kr.tracom.platform.launcher.http.security;

import com.sun.jersey.core.util.Priority;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import kr.tracom.platform.launcher.http.model.Session;

import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Priority(Priorities.AUTHORIZATION)
public class AuthFilter implements ContainerRequestFilter {

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        String method = containerRequest.getMethod();
        String path = containerRequest.getPath(true);

        if(method.equals("GET") && (path.equals("application.wadl") || path.equals("application.wadl/xsd0.xsd"))) {
            return containerRequest;
        }

        if(path.equals("auth/login")) {
            return containerRequest;
        }

        String auth = containerRequest.getHeaderValue("authorization");

        if(auth == null){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        String[] loginToken = BasicAuth.decode(auth);

        //If login or password fail
        if(loginToken == null || loginToken.length != 2){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        Session session = LoginManager.authentification(loginToken[0], loginToken[1]);

        //Our system refuse login and password
        if(session == null){
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        containerRequest.setSecurityContext(new AppSecurityContext(session));

        return containerRequest;
    }
}
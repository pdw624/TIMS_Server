package kr.tracom.platform.launcher.http.controller;

import kr.tracom.platform.common.log.Debug;
import kr.tracom.platform.launcher.http.model.LoginRequest;
import kr.tracom.platform.launcher.http.model.Session;
import kr.tracom.platform.launcher.http.model.User;
import kr.tracom.platform.launcher.http.security.LoginManager;

import javax.annotation.security.PermitAll;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/auth")
@PermitAll
public class LoginController {
    @POST
    @Path("/login")
    public Session login(LoginRequest loginRequest) {

        Debug.log(this.getClass().getName(), loginRequest.toString());

        LoginManager loginManager = LoginManager.getInstance();

        // Clumsy, I know, remember, this is just a quick and dirty example
        for (User user : loginManager.getUsers()) {
            if (user.getUsername().equals(loginRequest.getUsername()) &&
                    user.getPassword().equals(loginRequest.getPassword())) {

                for (Session session : loginManager.getSessions()) {
                    if (session.getName().equals(loginRequest.getUsername())) {
                        return session;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Here just a dummy method, would handle revoking the token in a real world scenario.
     * @param session
     */
    @POST
    @Path("/logout")
    public void logut(Session session) {

    }
}

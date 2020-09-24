package kr.tracom.platform.launcher.http.security;

import kr.tracom.platform.launcher.http.model.Session;
import kr.tracom.platform.launcher.http.model.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class AppSecurityContext implements SecurityContext {
    private Session loginSession;

    public AppSecurityContext(Session loginSession) {
        this.loginSession = loginSession;
    }

    // Returns the session if the user is logged on
    @Override
    public Principal getUserPrincipal() {
        if (loginSession == null)
            return null;

        return loginSession;
    }

    // Checks if the role and the users group is the same
    @Override
    public boolean isUserInRole(String role) {
        if (loginSession == null) return false;

        for (User user : LoginManager.getInstance().getUsers()) {
            if (user.getUsername().equals(loginSession.getName())) {
                // In our simplification each user only has one group
                return user.getGroup().name().equalsIgnoreCase(role);
            }
        }

        return false;
    }

    // Simple check for if the request is done via a secure channel such as https
    @Override
    public boolean isSecure() {
        return false;
    }

    // Just basic auth here, you can look into the others, if interested
    @Override
    public String getAuthenticationScheme(){
        return this.BASIC_AUTH;
    }
}

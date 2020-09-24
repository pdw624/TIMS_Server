package kr.tracom.platform.launcher.http.security;

import kr.tracom.platform.launcher.http.model.Group;
import kr.tracom.platform.launcher.http.model.Session;
import kr.tracom.platform.launcher.http.model.User;

import java.util.ArrayList;
import java.util.List;

public class LoginManager {
    private static LoginManager instance = new LoginManager();
    private List<User> users = new ArrayList<>();
    private List<Session> sessions = new ArrayList<>();

    private LoginManager() {
        users.add(new User("tracom", "tracom3452", Group.ADMIN));
        users.add(new User("song", "song1234", Group.USER));
        // BRT 계정
        users.add(new User("brt", "tracom3452", Group.ADMIN));

        sessions.add(new Session("tracom", "tracom_dev"));
        sessions.add(new Session("song", "song_dev"));
        sessions.add(new Session("brt", "brt_release"));
    }

    public static LoginManager getInstance() {
        return instance;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Session> getSessions() {
        return sessions;
    }


    public static Session authentification(String username, String password) {
        Session session = null;
        for (Session activeSession : LoginManager.getInstance().getSessions()) {
            if (activeSession.getName().equals(username)
                    && activeSession.getToken().equals(password)) {
                session = activeSession;
            }
        }
        return session;
    }
}

package kr.tracom.platform.launcher.http.model;

import lombok.Data;

import java.security.Principal;

@Data
public class Session implements Principal {
    private String name;
    private String token;

    public Session() {

    }

    public Session(String username, String token) {
        this.name = username;
        this.token = token;
    }
}

package kr.tracom.platform.launcher.http.model;

import lombok.Data;

@Data
public class User {
    private String username;
    private String password;
    private Group group;

    public User() {
    }


    public User(String username, String password, Group group) {
        this.username = username;
        this.password = password;
        this.group = group;
    }
}
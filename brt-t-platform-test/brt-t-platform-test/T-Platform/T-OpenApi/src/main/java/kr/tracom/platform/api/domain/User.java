package kr.tracom.platform.api.domain;

import lombok.Data;

@Data
public class User {
    private String id;
    private String lastName;
    private String firstName;
    private String login;
    private String password;
    private String twitter;
    private String avatarUrl;
    private String company;
}

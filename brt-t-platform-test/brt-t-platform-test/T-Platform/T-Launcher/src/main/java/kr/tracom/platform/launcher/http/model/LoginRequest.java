package kr.tracom.platform.launcher.http.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

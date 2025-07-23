package org.example.danggeun.user.dto;

import lombok.Data;

@Data
public class UserRegisterRequestDto {
    private String email;
    private String username;
    private String password;
    private String phone;
    private String confirmPassword;
}

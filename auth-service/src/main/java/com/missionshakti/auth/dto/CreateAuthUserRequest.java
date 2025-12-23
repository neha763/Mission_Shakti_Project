package com.missionshakti.auth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAuthUserRequest {
    private String username;
    private String email;
    private String role;
}
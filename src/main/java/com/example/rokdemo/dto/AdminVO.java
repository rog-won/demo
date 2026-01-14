package com.example.rokdemo.dto;

import lombok.Data;

@Data
public class AdminVO {
    String adminId;
    String password;
    String name;
    String email;
    String phone;
    String role;
}

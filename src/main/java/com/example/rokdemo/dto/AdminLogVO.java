package com.example.rokdemo.dto;

import lombok.Data;

@Data
public class AdminLogVO {
    String AdminId;
    String url;
    String ipAddress;
    String name;
    boolean success;
    String content;
    String errorMessage;
}

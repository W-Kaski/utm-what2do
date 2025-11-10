package com.utm.what2do.model.dto;

import lombok.Data;

@Data
public class UserQueryDTO {
    private int page = 1;
    private int size = 10;
    private String keyword;
}

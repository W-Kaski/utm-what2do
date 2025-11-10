package com.utm.what2do.model.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String utorid;
    private String email;
    private String displayName;
    private String faculty;
    private String yearOfStudy;
}

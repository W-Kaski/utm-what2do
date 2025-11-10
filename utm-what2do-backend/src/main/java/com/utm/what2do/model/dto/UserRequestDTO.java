package com.utm.what2do.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank
    private String utorid;

    @Email
    private String email;

    private String displayName;

    private String faculty;

    private String yearOfStudy;
}

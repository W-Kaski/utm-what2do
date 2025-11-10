package com.utm.what2do.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrganizationRequestDTO {

    @NotBlank
    private String name;

    private String description;

    @Email
    private String contactEmail;

    private String contactPhone;

    private String website;
}

package com.utm.what2do.model.vo;

import lombok.Data;

@Data
public class OrganizationVO {
    private Long id;
    private String name;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String website;
}

package com.utm.what2do.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User level payload exchanged with the client.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 64)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String displayName;

    /**
     * Plain password collected from the client.
     * Hashing happens in the service layer.
     */
    @Size(min = 8, max = 128)
    private String password;

    /**
     * USER / CLUB_MANAGER / ADMIN
     */
    private String role;

    private String avatarUrl;

    private String coverUrl;

    @Size(max = 512)
    private String bio;
}

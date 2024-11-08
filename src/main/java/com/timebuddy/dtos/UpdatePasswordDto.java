package com.timebuddy.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating user passwords.
 * This class is used to encapsulate the information required for a password update request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {

    /**
     * The username of the user.
     * Cannot be blank and must be provided in the request.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The current password of the user.
     * Cannot be blank and must be provided to verify the user's identity.
     */
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    /**
     * The new password for the user.
     * Must be between 8 and 100 characters.
     */
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    private String newPassword;
}



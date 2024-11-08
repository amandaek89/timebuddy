package com.timebuddy.dtos;

import com.timebuddy.models.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for incoming user data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

    /** The username of the user. */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    /** The password of the user. */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    /** The roles assigned to the user. */
    private Set<Role> authorities = new HashSet<>();

}

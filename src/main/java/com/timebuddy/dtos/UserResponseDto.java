package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for outgoing user data.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserResponseDto {

    /** The unique identifier for the user. */
    private Long id;

    /** The username of the user. */
    private String username;

    /** The roles assigned to the user. */
    private List<String> roles;
}

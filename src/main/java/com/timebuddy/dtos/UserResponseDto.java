package com.timebuddy.dtos;

import lombok.Data;

/**
 * Data Transfer Object for outgoing user data.
 */
@Data
public class UserResponseDto {

    /** The unique identifier for the user. */
    private Long id;

    /** The username of the user. */
    private String username;

    public UserResponseDto(long id, String username) {
    }

    public UserResponseDto() {

    }
}

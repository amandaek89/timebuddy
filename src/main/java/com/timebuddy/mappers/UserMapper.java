package com.timebuddy.mappers;

import com.timebuddy.dtos.UserRequestDto;
import com.timebuddy.dtos.UserResponseDto;
import com.timebuddy.models.User;

/**
 * Mapper for converting between User entity and DTOs.
 */
public class UserMapper {

    /**
     * Converts a UserRequestDto to a User entity.
     *
     * @param dto The UserRequestDto object.
     * @return The User entity.
     */
    public static User toEntity(UserRequestDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    /**
     * Converts a User entity to a UserResponseDto.
     *
     * @param user The User entity.
     * @return The UserResponseDto object.
     */
    public static UserResponseDto toResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUsername(user.getUsername());
        return responseDto;
    }
}


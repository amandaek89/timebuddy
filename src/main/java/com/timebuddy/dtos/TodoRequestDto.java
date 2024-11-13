package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Data Transfer Object (DTO) for transferring information about a to-do item
 * between client and server.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoRequestDto {

    /**
     * The title of the to-do item.
     */
    private String title;

    /**
     * A brief description of the to-do item.
     */
    private String description;

}


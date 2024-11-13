package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for transferring information about a to-do item
 * from the server to the client as part of the response.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoResponseDto {

    /**
     * The title of the to-do item.
     */
    private String title;

    /**
     * A brief description of the to-do item.
     */
    private String description;

    /**
     * Indicates whether the to-do item is marked as done or completed.
     */
    private boolean done;

    /**
    * The date of the to-do item.
    */
    private LocalDate date;
}

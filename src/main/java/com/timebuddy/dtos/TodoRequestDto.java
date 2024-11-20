package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import java.time.LocalTime;

/**
 * DTO for creating or updating a Todo task.
 * Includes fields for the title, description, time (optional), and all-day status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequestDto {

    private String title;
    private String description;
    private String time; // Optional time for the Todo task
}



package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.time.LocalTime;

/**
 * Data Transfer Object (DTO) for returning a simplified representation of a Todo task.
 * This DTO includes the title, description, completion status, date, time, and all-day status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {

    private long id;
    private String title;
    private String description;
    private boolean done;
    private LocalDate date; // Date of the Todo task
    private String time; // Optional time of the Todo task
    private boolean allDay; // Indicates whether the Todo is an all-day task

}

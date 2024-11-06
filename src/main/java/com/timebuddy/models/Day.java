package com.timebuddy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a single day in the calendar,
 * which includes the date and a list of Todo tasks for that day.
 * Example usage:
 *   - Date: 2024-11-06
 *   - Todos: A list of Todo tasks to be completed on this day.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Day {
    /**
     * The date for this day.
     */
    private LocalDate date;

    /**
     * A list of Todo tasks associated with this day.
     */
    private List<Todo> todos;
}

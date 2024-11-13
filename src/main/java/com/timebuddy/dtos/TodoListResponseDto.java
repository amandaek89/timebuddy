package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * A Data Transfer Object (DTO) representing the necessary details for a TodoList.
 * It includes the date of the TodoList and the titles of the associated Todos.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TodoListResponseDto {

    /**
     * The date associated with the TodoList.
     */
    private LocalDate date;

    /**
     * A list of titles of the Todo items in the TodoList.
     */
    private List<String> todos;
}


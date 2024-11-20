package com.timebuddy.mappers;

import com.timebuddy.dtos.TodoRequestDto;
import com.timebuddy.dtos.TodoResponseDto;
import com.timebuddy.models.Todo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TodoMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Converts a Todo entity to a TodoResponseDto.
     *
     * @param todo The Todo entity to convert.
     * @return The corresponding TodoResponseDto.
     */
    public static TodoResponseDto toResponseDto(Todo todo) {
        return new TodoResponseDto(
                todo.getTitle(),
                todo.getDescription(),
                todo.isDone(),
                todo.getTodoList() != null ? todo.getTodoList().getDate() : null,  // Lägg till datum från TodoList om det finns
                todo.getTime() != null ? todo.getTime().format(DateTimeFormatter.ofPattern("HH:mm")) : null,  // Omvandla LocalTime till String
                todo.isAllDay()  // Lägg till flagga för all-day
        );
    }

    /**
     * Converts a TodoRequestDto to a Todo entity.
     *
     * @param dto The TodoRequestDto to convert.
     * @return The corresponding Todo entity.
     */
    public static Todo toEntity(TodoRequestDto dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());

        // Parse the time string into LocalTime
        if (dto.getTime() != null && !dto.getTime().isEmpty()) {
            todo.setTime(LocalTime.parse(dto.getTime()));  // Parse the string time into LocalTime
        }
        return todo;
    }
}


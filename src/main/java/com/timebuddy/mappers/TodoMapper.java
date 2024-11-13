package com.timebuddy.mappers;

import com.timebuddy.dtos.TodoRequestDto;
import com.timebuddy.dtos.TodoResponseDto;
import com.timebuddy.models.Todo;

public class TodoMapper {

    public static TodoResponseDto toResponseDto(Todo todo) {
        TodoResponseDto dto = new TodoResponseDto();
        dto.setTitle(todo.getTitle());
        dto.setDescription(todo.getDescription());
        dto.setDone(todo.isDone());
        return dto;
    }

    public static Todo toEntity(TodoRequestDto dto) {
        Todo todo = new Todo();
        todo.setTitle(dto.getTitle());
        todo.setDescription(dto.getDescription());
        return todo;
    }
}


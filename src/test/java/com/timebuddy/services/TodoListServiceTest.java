package com.timebuddy.services;

import com.timebuddy.models.Todo;
import com.timebuddy.models.User;
import com.timebuddy.dtos.TodoListResponseDto;
import com.timebuddy.models.TodoList;
import com.timebuddy.repositories.TodoListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoListServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @InjectMocks
    private TodoListService todoListService;

    @Test
    void getTodoListsForUser_shouldReturnTodoListResponseDtos() {
        // Arrange: Mock User
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Mock TodoList and Todo items
        Todo todo1 = new Todo();
        todo1.setTitle("Task 1");

        Todo todo2 = new Todo();
        todo2.setTitle("Task 2");

        TodoList todoList1 = new TodoList();
        todoList1.setDate(LocalDate.of(2024, 11, 12));
        todoList1.setTodos(List.of(todo1));

        TodoList todoList2 = new TodoList();
        todoList2.setDate(LocalDate.of(2024, 11, 13));
        todoList2.setTodos(List.of(todo1, todo2));

        // Mock repository behavior
        when(todoListRepository.findByUser(user)).thenReturn(List.of(todoList1, todoList2));

        // Act: Call the method
        List<TodoListResponseDto> result = todoListService.getTodoListsForUser(user);

        // Assert: Verify results
        assertNotNull(result);
        assertEquals(2, result.size());

        TodoListResponseDto responseDto1 = result.get(0);
        assertEquals(LocalDate.of(2024, 11, 12), responseDto1.getDate());
        assertEquals(List.of("Task 1"), responseDto1.getTodos());

        TodoListResponseDto responseDto2 = result.get(1);
        assertEquals(LocalDate.of(2024, 11, 13), responseDto2.getDate());
        assertEquals(List.of("Task 1", "Task 2"), responseDto2.getTodos());
    }
}



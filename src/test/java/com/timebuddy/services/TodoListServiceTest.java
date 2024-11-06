package com.timebuddy.services;

import com.timebuddy.models.TodoList;
import com.timebuddy.repositories.TodoListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TodoListServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @InjectMocks
    private TodoListService todoListService;

    private TodoList todoList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        todoList = new TodoList(1L, "My TodoList", null, null); // Exempeldata
    }

    @Test
    void testCreateTodoList() {
        when(todoListRepository.save(todoList)).thenReturn(todoList);

        TodoList createdTodoList = todoListService.createTodoList(todoList);

        assertNotNull(createdTodoList);
        assertEquals("My TodoList", createdTodoList.getTitle());
        verify(todoListRepository, times(1)).save(todoList);
    }

    @Test
    void testGetTodoListById() {
        when(todoListRepository.findById(1L)).thenReturn(Optional.of(todoList));

        Optional<TodoList> foundTodoList = todoListService.getTodoListById(1L);

        assertTrue(foundTodoList.isPresent());
        assertEquals("My TodoList", foundTodoList.get().getTitle());
    }

    @Test
    void testUpdateTodoList() {
        TodoList updatedTodoList = new TodoList(1L, "Updated TodoList", null, null);
        when(todoListRepository.findById(1L)).thenReturn(Optional.of(todoList));
        when(todoListRepository.save(todoList)).thenReturn(updatedTodoList);

        Optional<TodoList> updated = todoListService.updateTodoList(1L, updatedTodoList);

        assertTrue(updated.isPresent());
        assertEquals("Updated TodoList", updated.get().getTitle());
    }

    @Test
    void testDeleteTodoList() {
        doNothing().when(todoListRepository).deleteById(1L);

        todoListService.deleteTodoList(1L);

        verify(todoListRepository, times(1)).deleteById(1L);
    }
}


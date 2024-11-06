package com.timebuddy.services;

import com.timebuddy.models.Todo;
import com.timebuddy.repositories.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Todo todo;

    @BeforeEach
    public void setUp() {
        // Initialize the mocks
        MockitoAnnotations.openMocks(this);

        // Create a new Todo for testing
        todo = new Todo(1L, "Test Todo", "Test Description", false, null);
    }

    @Test
    public void testAddTodo() {
        // Arrange
        when(todoRepository.save(todo)).thenReturn(todo);

        // Act
        Todo result = todoService.addTodo(todo);

        // Assert
        assertNotNull(result);
        assertEquals(todo.getTitle(), result.getTitle());
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    public void testGetTodoById_Success() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        // Act
        Optional<Todo> result = todoService.getTodoById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(todo.getId(), result.get().getId());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetTodoById_NotFound() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Todo> result = todoService.getTodoById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateTodoById_Success() {
        // Arrange
        Todo updatedTodo = new Todo(1L, "Updated Todo", "Updated Description", true, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(updatedTodo)).thenReturn(updatedTodo);

        // Act
        Optional<Todo> result = todoService.updateTodoById(1L, updatedTodo);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedTodo.getTitle(), result.get().getTitle());
        assertTrue(result.get().isDone());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(updatedTodo);
    }

    @Test
    public void testUpdateTodoById_NotFound() {
        // Arrange
        Todo updatedTodo = new Todo(1L, "Updated Todo", "Updated Description", true, null);
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Todo> result = todoService.updateTodoById(1L, updatedTodo);

        // Assert
        assertFalse(result.isPresent());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteTodo_Success() {
        // Act
        todoService.deleteTodo(1L);

        // Assert
        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testMarkAsDone_Success() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        // Act
        Optional<Todo> result = todoService.markAsDone(1L);

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get().isDone());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    public void testMarkAsNotDone_Success() {
        // Arrange
        todo.setDone(true);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);

        // Act
        Optional<Todo> result = todoService.markAsNotDone(1L);

        // Assert
        assertTrue(result.isPresent());
        assertFalse(result.get().isDone());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(todo);
    }
}


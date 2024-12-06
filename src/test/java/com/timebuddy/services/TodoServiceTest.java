package com.timebuddy.services;

import com.timebuddy.dtos.TodoRequestDto;
import com.timebuddy.dtos.TodoResponseDto;
import com.timebuddy.models.Todo;
import com.timebuddy.models.TodoList;
import com.timebuddy.models.User;
import com.timebuddy.repositories.TodoRepository;
import com.timebuddy.repositories.TodoListRepository;
import com.timebuddy.repositories.UserRepository;
import com.timebuddy.services.TodoListService;
import com.timebuddy.services.TodoService;
import com.timebuddy.exceptions.TodoNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoListService todoListService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TodoListRepository todoListRepository;

    @InjectMocks
    private TodoService todoService;

    private User testUser;
    private Todo testTodo;
    private TodoRequestDto todoRequestDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up a mock user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        // Set up a test Todo
        testTodo = new Todo();
        testTodo.setId(1L); // Ge testTodo ett ID
        testTodo.setTitle("Test Todo");
        testTodo.setDescription("Description");
        testTodo.setDone(false);
        testTodo.setTime(LocalTime.of(9, 0));

        // Set up a TodoRequestDto
        todoRequestDto = new TodoRequestDto();
        todoRequestDto.setTitle("New Todo");
        todoRequestDto.setDescription("New Todo Description");
        todoRequestDto.setTime("10:00");
    }

    @Test
    public void testAddTodoToSpecificDay_Success() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 12, 5);
        TodoList todoList = new TodoList();
        todoList.setUser(testUser);
        todoList.setDate(date);
        when(todoListService.getOrCreateTodoList(testUser, date)).thenReturn(todoList);
        when(todoRepository.save(any(Todo.class))).thenReturn(testTodo);

        // Act
        Todo result = todoService.addTodoToSpecificDay(testUser, date, todoRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(testTodo.getTitle(), result.getTitle());
        assertEquals(testTodo.getDescription(), result.getDescription());
        assertEquals(testTodo.getTime(), result.getTime());
        verify(todoListService, times(1)).getOrCreateTodoList(testUser, date);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    public void testGetTodoById_Success() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));

        // Act
        Optional<Todo> result = todoService.getTodoById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTodo.getId(), result.get().getId());
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
    public void testGetTodosForDate_Success() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 12, 05);
        testTodo.setId(1L);
        TodoList todoList = new TodoList();
        todoList.setUser(testUser);
        todoList.setDate(date);
        todoList.addTodo(testTodo);
        when(todoListRepository.findByUserAndDate(testUser, date)).thenReturn(Optional.of(todoList));

        // Act
        List<TodoResponseDto> result = todoService.getTodosForDate(testUser, date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTodo.getTitle(), result.get(0).getTitle());
        verify(todoListRepository, times(1)).findByUserAndDate(testUser, date);
    }

    @Test
    public void testGetTodosForDate_NotFound() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 12, 5);
        when(todoListRepository.findByUserAndDate(testUser, date)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> todoService.getTodosForDate(testUser, date));
        verify(todoListRepository, times(1)).findByUserAndDate(testUser, date);
    }

    @Test
    public void testUpdateTodoById_Success() {
        // Arrange
        testTodo.setId(1L);
        testTodo.setTitle("Original Todo");
        testTodo.setDescription("Original Description");
        testTodo.setTime(LocalTime.of(9, 0));

        Todo updatedTodo = new Todo();
        updatedTodo.setTitle("Updated Todo");
        updatedTodo.setDescription("Updated Description");
        updatedTodo.setTime(LocalTime.of(10, 30));

        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Todo> result = todoService.updateTodoById(1L, updatedTodo);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Todo", result.get().getTitle());
        assertEquals("Updated Description", result.get().getDescription());
        assertEquals(LocalTime.of(10, 30), result.get().getTime());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(testTodo);
    }


    @Test
    public void testUpdateTodoById_NotFound() {
        // Arrange
        Todo updatedTodo = new Todo(1L, "Updated Todo", "Updated Description", false, LocalTime.of(10, 30));
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Todo> result = todoService.updateTodoById(1L, updatedTodo);

        // Assert
        assertFalse(result.isPresent());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteTodo_Success() throws Exception {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));

        // Act
        todoService.deleteTodo(1L);

        // Assert
        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteTodo_NotFound() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodo(1L));
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateTodoStatus_Success() {
        // Arrange
        boolean done = true;
        testTodo.setId(1L); // Ge testTodo ett ID
        testTodo.setDone(done); // Uppdatera "done" attributet
        when(todoRepository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoRepository.save(testTodo)).thenReturn(testTodo);

        // Act
        Optional<Todo> result = todoService.updateTodoStatus(1L, done);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(done, result.get().isDone());
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(testTodo);
    }


    @Test
    public void testUpdateTodoStatus_NotFound() {
        // Arrange
        boolean done = true;
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Todo> result = todoService.updateTodoStatus(1L, done);

        // Assert
        assertFalse(result.isPresent());
        verify(todoRepository, times(1)).findById(1L);
    }
}

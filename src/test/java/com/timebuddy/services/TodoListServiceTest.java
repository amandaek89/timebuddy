package com.timebuddy.services;

import com.timebuddy.models.Todo;
import com.timebuddy.models.User;
import com.timebuddy.dtos.TodoListResponseDto;
import com.timebuddy.models.TodoList;
import com.timebuddy.repositories.TodoListRepository;
import com.timebuddy.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoListServiceTest {

    @Mock
    private TodoListRepository todoListRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoListService todoListService;

    @Mock
    private UserDetails userDetails;

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

    @Test
    void getOrCreateTodoList_shouldReturnExistingTodoList_whenItExists() {
        // Arrange: Mock User and date
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        LocalDate date = LocalDate.of(2024, 11, 12);

        // Mock existing TodoList
        TodoList existingTodoList = new TodoList();
        existingTodoList.setUser(user);
        existingTodoList.setDate(date);
        existingTodoList.setTodos(new ArrayList<>());  // Empty todo list

        // Mock repository behavior to return existing TodoList
        when(todoListRepository.findByUserAndDate(user, date)).thenReturn(Optional.of(existingTodoList));

        // Act: Call the method
        TodoList result = todoListService.getOrCreateTodoList(user, date);

        // Assert: Verify that the returned TodoList is the same as the existing one
        assertNotNull(result);
        assertEquals(existingTodoList, result);
        verify(todoListRepository, times(1)).findByUserAndDate(user, date);  // Verify repository interaction
        verify(todoListRepository, times(0)).save(any());  // Ensure save is not called
    }

    @Test
    void getOrCreateTodoList_shouldCreateNewTodoList_whenItDoesNotExist() {
        // Arrange: Mock User and date
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        LocalDate date = LocalDate.of(2024, 11, 12);

        // Mock empty result when no TodoList exists for the user and date
        when(todoListRepository.findByUserAndDate(user, date)).thenReturn(Optional.empty());

        // Mock save method to return a new TodoList object when called
        TodoList newTodoList = new TodoList();
        newTodoList.setUser(user);
        newTodoList.setDate(date);
        newTodoList.setTodos(new ArrayList<>());
        when(todoListRepository.save(any(TodoList.class))).thenReturn(newTodoList);

        // Act: Call the method
        TodoList result = todoListService.getOrCreateTodoList(user, date);

        // Assert: Verify that a new TodoList is created and saved
        assertNotNull(result);  // Verify result is not null
        assertEquals(user, result.getUser());  // Ensure the user is correctly set
        assertEquals(date, result.getDate());  // Ensure the date is correctly set
        assertTrue(result.getTodos().isEmpty());  // Newly created list should have no todos

        verify(todoListRepository, times(1)).findByUserAndDate(user, date);  // Verify repository interaction for find
        verify(todoListRepository, times(1)).save(result);  // Ensure save is called once
    }

    @Test
    void getTodoListsForMonth_shouldReturnTodoListsForMonth() {
        // Arrange
        String loggedInUsername = "testuser";
        int month = 11;
        int year = 2024;

        // Mock UserDetails
        when(userDetails.getUsername()).thenReturn(loggedInUsername);

        // Mock UserRepository
        User user = new User("testuser", "password", new ArrayList<>());

        when(userRepository.findByUsername(loggedInUsername)).thenReturn(Optional.of(user));

        // Mock TodoListRepository
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

        when(todoListRepository.findByUserAndDateBetween(user, LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30)))
                .thenReturn(List.of(todoList1, todoList2));

        // Act: Call the method
        List<TodoListResponseDto> result = todoListService.getTodoListsForMonth(month, year, userDetails);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify the first TodoList
        TodoListResponseDto responseDto1 = result.get(0);
        assertEquals(LocalDate.of(2024, 11, 12), responseDto1.getDate());
        assertIterableEquals(List.of("Task 1"), responseDto1.getTodos());

        // Verify the second TodoList
        TodoListResponseDto responseDto2 = result.get(1);
        assertEquals(LocalDate.of(2024, 11, 13), responseDto2.getDate());
        assertIterableEquals(List.of("Task 1", "Task 2"), responseDto2.getTodos());

        // Verify repository interactions
        verify(userRepository, times(1)).findByUsername(loggedInUsername);
        verify(todoListRepository, times(1)).findByUserAndDateBetween(user, LocalDate.of(2024, 11, 1), LocalDate.of(2024, 11, 30));
    }
}



package com.timebuddy.controllers;

import com.timebuddy.models.Todo;
import com.timebuddy.dtos.TodoListResponseDto;
import com.timebuddy.models.TodoList;
import com.timebuddy.models.User;
import com.timebuddy.services.TodoListService;
import com.timebuddy.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class to handle TodoList related HTTP requests.
 */
@RestController
@RequestMapping("/api/todolist")
public class TodoListController {

    private final TodoListService todoListService;
    private final UserService userService;

    /**
     * Constructor for TodoListController.
     *
     * @param todoListService The service used for managing TodoLists.
     * @param userService The service used for managing Users.
     */
    public TodoListController(TodoListService todoListService, UserService userService) {
        this.todoListService = todoListService;
        this.userService = userService;
    }

    /**
     * Retrieves all TodoLists for the currently authenticated user.
     * Returns a list of TodoListResponseDto containing the date and titles of todos.
     *
     * @param user The currently authenticated user.
     * @return A list of TodoLists associated with the user.
     */
    @GetMapping("/user")
    public ResponseEntity<List<TodoListResponseDto>> getTodoListsForUser(@AuthenticationPrincipal User user) {
        List<TodoListResponseDto> todoLists = todoListService.getTodoListsForUser(user);
        return ResponseEntity.ok(todoLists);
    }

    /**
     * Retrieves or creates a TodoList for a specific date for the currently authenticated user.
     * Returns a TodoListResponseDto with the date and titles of todos.
     *
     * @param date The date for which the TodoList is to be retrieved or created (format: yyyy-MM-dd).
     * @param user The currently authenticated user.
     * @return A ResponseEntity containing a TodoListResponseDto with the date and todo titles.
     */
    @GetMapping("/user/{date}")
    public ResponseEntity<TodoListResponseDto> getOrCreateTodoListForUser(
            @PathVariable("date") String date,
            @AuthenticationPrincipal User user) {

        // Parse the date string to LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Retrieve or create the TodoList for the specified date
        TodoList todoList = todoListService.getOrCreateTodoList(user, localDate);

        // Map the TodoList to TodoListResponseDto
        TodoListResponseDto responseDto = new TodoListResponseDto(
                todoList.getDate(),
                todoList.getTodos().stream()
                        .map(Todo::getTitle) // Extract titles of todos
                        .collect(Collectors.toList())
        );

        // Return the DTO wrapped in a ResponseEntity
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/month")
    public ResponseEntity<List<TodoListResponseDto>> getTodoListsForMonth(
            @RequestParam int month,
            @RequestParam int year,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Hämta TodoLists för den inloggade användaren för den specifika månaden
        List<TodoListResponseDto> todoLists = todoListService.getTodoListsForMonth(month, year, userDetails);

        return ResponseEntity.ok(todoLists);
    }


}

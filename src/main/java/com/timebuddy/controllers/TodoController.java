package com.timebuddy.controllers;

import com.timebuddy.dtos.*;
import com.timebuddy.mappers.TodoMapper;
import com.timebuddy.models.Todo;
import com.timebuddy.models.User;
import com.timebuddy.repositories.UserRepository;
import com.timebuddy.services.TodoService;
import com.timebuddy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Constructor for TodoController.
     *
     * @param todoService The service handling Todo-related business logic.
     * @param userService The service handling User-related business logic.
     * @param userRepository The repository for User entities.
     */
    public TodoController(TodoService todoService, UserService userService, UserRepository userRepository) {
        this.todoService = todoService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Adds a new Todo task for the authenticated user on a specific date.
     *
     * @param date        The date for which the Todo is being added (format: yyyy-MM-dd).
     * @param todoRequestDto The details of the Todo task (title and description).
     * @param userDetails The authenticated user's details, automatically injected.
     * @return A ResponseEntity containing the newly created Todo task in a TodoResponseDto.
     */
    @Operation(summary = "Add a todo to a specific day", description = "Adds a new todo to the authenticated user's TodoList for a specific day, with an optional time.")
    @PostMapping("/add/{date}")
    public ResponseEntity<TodoResponseDto> addTodoToSpecificDay(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody TodoRequestDto todoRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Extract the username of the logged-in user
        String username = userDetails.getUsername();

        // Fetch the User entity from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create the Todo based on the provided data (including time and all-day flag)
        Todo todo = todoService.addTodoToSpecificDay(user, date, todoRequestDto);

        // Format the time to string if needed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = (todo.getTime() != null) ? todo.getTime().format(formatter) : null;

        // Create the response DTO
        TodoResponseDto responseDto = new TodoResponseDto(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isDone(),
                todo.getTodoList().getDate(),
                formattedTime, // Formatted time as a string
                todo.isAllDay()
        );

        return ResponseEntity.ok(responseDto);
    }


    /**
     * Retrieves a specific Todo by its ID.
     *
     * @param id The ID of the Todo to retrieve.
     * @return A ResponseEntity containing an ApiResponse with the Todo details or an error message.
     */
    @Operation(summary = "Get a todo by ID", description = "Retrieves a specific todo by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponseDto>> getTodoById(@PathVariable Long id) {
        Optional<Todo> todo = todoService.getTodoById(id);

        return todo.map(value -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Todo fetched successfully", TodoMapper.toResponseDto(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null)));
    }

    /**
     * Retrieves all Todos for the currently authenticated user on a specific date.
     *
     * @param date        The date for which to fetch Todos (format: yyyy-MM-dd).
     * @param userDetails The details of the currently authenticated user, injected automatically.
     * @return A ResponseEntity containing the list of TodoResponseDto for the specified date.
     */
    @Operation(summary = "Get todos for a specific date", description = "Retrieves all todos for the authenticated user on a specific date")
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TodoResponseDto>> getTodosForDate(
            @PathVariable String date,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Extract the username of the logged-in user
        String username = userDetails.getUsername();

        // Fetch the User entity from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Parse the date from String to LocalDate
        LocalDate localDate = LocalDate.parse(date);

        // Fetch Todos for the user and date
        List<TodoResponseDto> todoResponseDtos = todoService.getTodosForDate(user, localDate);

        return ResponseEntity.ok(todoResponseDtos);
    }

    /**
     * Endpoint to retrieve all todos for the logged-in user.
     *
     * @param userDetails The details of the logged-in user, automatically injected.
     * @return ResponseEntity containing the list of all TodoResponseDto objects for the logged-in user.
     * @throws RuntimeException if the user is not found in the database.
     */
    /**
     * Endpoint to retrieve all todos for the logged-in user.
     *
     * @param userDetails The details of the logged-in user.
     * @return ResponseEntity containing the list of all TodoResponseDto objects for the logged-in user.
     */
    @Operation(summary = "Get all todos for the authenticated user", description = "Retrieves all todos for the authenticated user")
    @GetMapping("/all")
    public ResponseEntity<List<TodoResponseDto>> getAllTodosForUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Extract the username of the logged-in user
        String username = userDetails.getUsername();

        // Fetch the User entity from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch all Todos for the user (from all TodoLists)
        List<TodoResponseDto> todoResponseDtos = todoService.getAllTodosForUser(user);

        return ResponseEntity.ok(todoResponseDtos);  // Return the list of todos
    }



    /**
     * Updates an existing Todo by its ID.
     *
     * @param id         The ID of the Todo to update.
     * @param requestDto The data transfer object containing the updated Todo details.
     * @return A ResponseEntity containing an ApiResponse with the updated Todo details or an error message.
     */
    @Operation(summary = "Update a todo", description = "Updates an existing todo")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponseDto>> updateTodo(
            @PathVariable Long id,
            @RequestBody @Valid TodoRequestDto requestDto) {
        Todo updatedTodoData = TodoMapper.toEntity(requestDto);

        Optional<Todo> updatedTodo = todoService.updateTodoById(id, updatedTodoData);

        return updatedTodo.map(value -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Todo updated successfully", TodoMapper.toResponseDto(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null)));
    }

    /**
     * Deletes a Todo by its ID.
     *
     * @param id The ID of the Todo to delete.
     * @return A ResponseEntity containing an ApiResponse indicating the deletion status.
     * throws exception If the Todo task with the given ID does not exist.
     */
    @Operation(summary = "Delete a todo", description = "Deletes a todo by its ID if it exists.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodoById(@PathVariable Long id) {
        Optional<Todo> todo = todoService.getTodoById(id);

        if (todo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null));
        }

        todoService.deleteTodo(id);

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Todo deleted successfully", null));
    }

    /**
     * Marks a Todo as completed or not completed based on the provided status.
     *
     * @param id The ID of the Todo to update.
     * @param done The status to set the Todo to. If true, the task will be marked as completed. If false, it will be marked as not completed.
     * @return A ResponseEntity containing an ApiResponse with the updated Todo details or an error message.
     */
    @Operation(summary = "Update a todo status", description = "Marks a todo as completed or not completed based on the provided status")
    @PatchMapping("/{id}/{done}")
    public ResponseEntity<ApiResponse<TodoResponseDto>> updateTodoStatus(@PathVariable Long id, @PathVariable boolean done) {
        Optional<Todo> updatedTodo = todoService.updateTodoStatus(id, done);

        return updatedTodo.map(value -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Todo status updated", TodoMapper.toResponseDto(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null)));
    }
}

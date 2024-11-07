package com.timebuddy.controllers;

import com.timebuddy.dtos.ApiResponse;
import com.timebuddy.dtos.TodoRequestDto;
import com.timebuddy.dtos.TodoResponseDto;
import com.timebuddy.mappers.TodoMapper;
import com.timebuddy.models.Todo;
import com.timebuddy.services.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService todoService;

    /**
     * Constructor for TodoController.
     *
     * @param todoService The service layer responsible for managing Todos.
     */
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * Creates a new Todo task.
     *
     * @param requestDto The data transfer object containing information about the new Todo.
     * @return A ResponseEntity containing an ApiResponse with the created Todo details.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponseDto>> createTodo(@RequestBody @Valid TodoRequestDto requestDto) {
        Todo todo = TodoMapper.toEntity(requestDto);
        Todo savedTodo = todoService.addTodo(todo);
        TodoResponseDto responseDto = TodoMapper.toResponseDto(savedTodo);

        return new ResponseEntity<>(
                new ApiResponse<>(HttpStatus.CREATED.value(), "Todo created successfully", responseDto),
                HttpStatus.CREATED
        );
    }

    /**
     * Retrieves a specific Todo by its ID.
     *
     * @param id The ID of the Todo to retrieve.
     * @return A ResponseEntity containing an ApiResponse with the Todo details or an error message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TodoResponseDto>> getTodoById(@PathVariable Long id) {
        Optional<Todo> todo = todoService.getTodoById(id);

        return todo.map(value -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Todo fetched successfully", TodoMapper.toResponseDto(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null)));
    }

    /**
     * Updates an existing Todo by its ID.
     *
     * @param id         The ID of the Todo to update.
     * @param requestDto The data transfer object containing the updated Todo details.
     * @return A ResponseEntity containing an ApiResponse with the updated Todo details or an error message.
     */
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
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTodoById(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.NO_CONTENT.value(), "Todo deleted successfully", null));
    }

    /**
     * Marks a Todo as completed.
     *
     * @param id The ID of the Todo to mark as done.
     * @return A ResponseEntity containing an ApiResponse with the updated Todo details or an error message.
     */
    @PatchMapping("/{id}/done")
    public ResponseEntity<ApiResponse<TodoResponseDto>> markTodoAsDone(@PathVariable Long id) {
        Optional<Todo> updatedTodo = todoService.markAsDone(id);

        return updatedTodo.map(value -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Todo marked as done", TodoMapper.toResponseDto(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null)));
    }

    /**
     * Marks a Todo as not completed.
     *
     * @param id The ID of the Todo to mark as not done.
     * @return A ResponseEntity containing an ApiResponse with the updated Todo details or an error message.
     */
    @PatchMapping("/{id}/not-done")
    public ResponseEntity<ApiResponse<TodoResponseDto>> markTodoAsNotDone(@PathVariable Long id) {
        Optional<Todo> updatedTodo = todoService.markAsNotDone(id);

        return updatedTodo.map(value -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "Todo marked as not done", TodoMapper.toResponseDto(value))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Todo not found", null)));
    }
}

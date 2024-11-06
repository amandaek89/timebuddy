package com.timebuddy.controllers;

import com.timebuddy.models.TodoList;
import com.timebuddy.services.TodoListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller class to handle CRUD operations for TodoLists.
 * This class exposes API endpoints to manage TodoLists.
 */
@RestController
@RequestMapping("/api/todolists")
public class TodoListController {

    private final TodoListService todoListService;

    /**
     * Constructor for TodoListController.
     *
     * @param todoListService The service to interact with TodoLists.
     */
    public TodoListController(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    /**
     * Creates a new TodoList.
     *
     * @param todoList The TodoList object to be created.
     * @return A response with the created TodoList.
     */
    @PostMapping
    public ResponseEntity<TodoList> createTodoList(@RequestBody TodoList todoList) {
        TodoList createdTodoList = todoListService.createTodoList(todoList);
        return new ResponseEntity<>(createdTodoList, HttpStatus.CREATED);
    }

    /**
     * Retrieves all TodoLists for a specific user.
     *
     * @param userId The ID of the user whose TodoLists should be retrieved.
     * @return A response containing the list of TodoLists for the user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TodoList>> getTodoListsForUser(@PathVariable Long userId) {
        List<TodoList> todoLists = todoListService.getTodoListsForUser(userId);
        if (todoLists.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No content if the list is empty
        }
        return new ResponseEntity<>(todoLists, HttpStatus.OK);
    }

    /**
     * Retrieves a specific TodoList by its ID.
     *
     * @param id The ID of the TodoList to be retrieved.
     * @return A response containing the TodoList if found, or a 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoList> getTodoListById(@PathVariable Long id) {
        Optional<TodoList> todoList = todoListService.getTodoListById(id);
        return todoList.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Retrieves a specific TodoList by its title.
     *
     * @param title The title of the TodoList to be retrieved.
     * @return A response containing the TodoList if found, or a 404 if not found.
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<TodoList> getTodoListByTitle(@PathVariable String title) {
        Optional<TodoList> todoList = todoListService.getTodoListByTitle(title);
        return todoList.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Updates an existing TodoList.
     *
     * @param id The ID of the TodoList to be updated.
     * @param updatedTodoList The updated TodoList object.
     * @return A response with the updated TodoList, or a 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoList> updateTodoList(@PathVariable Long id, @RequestBody TodoList updatedTodoList) {
        Optional<TodoList> updated = todoListService.updateTodoList(id, updatedTodoList);
        return updated.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes a specific TodoList by its ID.
     *
     * @param id The ID of the TodoList to be deleted.
     * @return A response indicating the outcome of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoList(@PathVariable Long id) {
        Optional<TodoList> todoList = todoListService.getTodoListById(id);
        if (todoList.isPresent()) {
            todoListService.deleteTodoList(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Successfully deleted
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Not found if no TodoList with the given ID exists
    }
}


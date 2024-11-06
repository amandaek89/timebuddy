package com.timebuddy.services;

import com.timebuddy.models.Todo;
import com.timebuddy.repositories.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Todo tasks.
 * Provides methods to create, update, delete, and retrieve Todo tasks.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Constructor for TodoService.
     *
     * @param todoRepository The repository used to interact with the Todo database table.
     */
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /**
     * Adds a new Todo task to the database.
     *
     * @param todo The Todo task to be added.
     * @return The saved Todo task with its generated ID.
     */
    public Todo addTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    /**
     * Retrieves a Todo task by its ID.
     *
     * @param id The ID of the Todo task to be retrieved.
     * @return An Optional containing the Todo task, or an empty Optional if not found.
     */
    public Optional<Todo> getTodoById(Long id) {
        return todoRepository.findById(id);
    }

    /**
     * Retrieves all Todo tasks associated with a specific TodoList.
     *
     * @param todoListId The ID of the TodoList whose Todos are to be retrieved.
     * @return A list of Todo tasks belonging to the specified TodoList.
     */
    public List<Todo> getTodosByTodoListId(Long todoListId) {
        return todoRepository.findByTodoListId(todoListId);
    }

    /**
     * Updates an existing Todo task in the database.
     *
     * @param id The ID of the Todo task to be updated.
     * @param updatedTodo The updated Todo object with new values.
     * @return The updated Todo task, or an empty Optional if the Todo task was not found.
     */
    public Optional<Todo> updateTodoById(Long id, Todo updatedTodo) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setTitle(updatedTodo.getTitle());
                    todo.setDescription(updatedTodo.getDescription());
                    todo.setDone(updatedTodo.isDone());
                    return todoRepository.save(todo);
                });
    }

    /**
     * Deletes a Todo task by its ID.
     *
     * @param id The ID of the Todo task to be deleted.
     */
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }

    /**
     * Marks a Todo task as completed.
     *
     * @param id The ID of the Todo task to be marked as completed.
     * @return The updated Todo task with its status set to "done", or an empty Optional if not found.
     */
    public Optional<Todo> markAsDone(Long id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setDone(true);
                    return todoRepository.save(todo);
                });
    }

    /**
     * Marks a Todo task as not completed.
     *
     * @param id The ID of the Todo task to be marked as not completed.
     * @return The updated Todo task with its status set to "not done", or an empty Optional if not found.
     */
    public Optional<Todo> markAsNotDone(Long id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setDone(false);
                    return todoRepository.save(todo);
                });
    }

}
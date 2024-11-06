package com.timebuddy.services;

import com.timebuddy.models.TodoList;
import com.timebuddy.repositories.TodoListRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing TodoList operations.
 * Provides methods to create, update, delete, and retrieve TodoLists.
 */
@Service
public class TodoListService {

    private final TodoListRepository todoListRepository;

    /**
     * Constructor for TodoListService.
     *
     * @param todoListRepository The repository used to interact with the TodoList database table.
     */
    public TodoListService(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
    }

    /**
     * Creates a new TodoList.
     *
     * @param todoList The TodoList to be created.
     * @return The created TodoList, with its generated ID.
     */
    public TodoList createTodoList(TodoList todoList) {
        return todoListRepository.save(todoList);
    }

    /**
     * Retrieves a list of TodoLists for a specific user.
     *
     * @param userId The ID of the user whose TodoLists are to be retrieved.
     * @return A list of TodoLists belonging to the user.
     */
    public List<TodoList> getTodoListsForUser(Long userId) {
        return todoListRepository.findByUserId(userId);
    }

    /**
     * Retrieves a TodoList by its ID.
     *
     * @param id The ID of the TodoList to be retrieved.
     * @return An Optional containing the TodoList if found, or empty if not found.
     */
    public Optional<TodoList> getTodoListById(Long id) {
        return todoListRepository.findById(id);
    }

    /**
     * Retrieves a TodoList by its title.
     *
     * @param title The title of the TodoList.
     * @return An Optional containing the TodoList if found, or an empty Optional if not found.
     */
    public Optional<TodoList> getTodoListByTitle(String title) {
        return todoListRepository.findByTitle(title);
    }

    /**
     * Updates an existing TodoList.
     *
     * @param id The ID of the TodoList to be updated.
     * @param updatedTodoList The updated TodoList object containing new values.
     * @return The updated TodoList, or an empty Optional if the TodoList was not found.
     */
    public Optional<TodoList> updateTodoList(Long id, TodoList updatedTodoList) {
        return todoListRepository.findById(id)
                .map(todoList -> {
                    todoList.setTitle(updatedTodoList.getTitle());
                    // You could also update other fields if necessary
                    return todoListRepository.save(todoList);
                });
    }

    /**
     * Deletes a TodoList by its ID.
     *
     * @param id The ID of the TodoList to be deleted.
     */
    public void deleteTodoList(Long id) {
        todoListRepository.deleteById(id);
    }
}


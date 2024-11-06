package com.timebuddy.repositories;

import com.timebuddy.models.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for the TodoList entity.
 * Provides CRUD operations for TodoList objects and custom query methods.
 * Extends JpaRepository to benefit from built-in functionality for database operations.
 */
@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    /**
     * Finds a TodoList by its title.
     * This method will automatically generate a query based on the method name.
     *
     * @param title The title of the TodoList to search for.
     * @return The TodoList object that matches the provided title, or null if no match is found.
     */
    Optional<TodoList> findByTitle(String title);

    /**
     * Retrieves a list of TodoLists for a specific user.
     *
     * @param userId The ID of the user whose TodoLists are to be retrieved.
     * @return A list of TodoLists belonging to the user.
     */
    List<TodoList> findByUserId(Long userId);

}


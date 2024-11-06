package com.timebuddy.repositories;

import com.timebuddy.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the Todo entity.
 * Provides CRUD operations for Todo objects and custom query methods.
 * Extends JpaRepository to benefit from built-in functionality for database operations.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * Finds a Todo by its title.
     * This method will automatically generate a query based on the method name.
     *
     * @param title The title of the Todo to search for.
     * @return The Todo object that matches the provided title, or null if no match is found.
     */
    Todo findByTitle(String title);
}


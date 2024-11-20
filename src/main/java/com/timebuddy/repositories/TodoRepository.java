package com.timebuddy.repositories;

import com.timebuddy.models.Todo;
import com.timebuddy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for the Todo entity.
 * Provides CRUD operations for Todo objects and custom query methods.
 * Extends JpaRepository to benefit from built-in functionality for database operations.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * Finds Todo tasks by their title.
     *
     * @param title The title of the Todo tasks to search for.
     * @return A list of Todo tasks with the given title.
     */
    List<Todo> findByTitle(String title);

    /**
     * Finds all todos associated with a specific user.
     *
     * @param user The user whose todos are to be retrieved.
     * @return A list of Todo entities belonging to the specified user.
     */
    List<Todo> findByUser(User user);
}


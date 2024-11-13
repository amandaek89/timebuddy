package com.timebuddy.repositories;

import com.timebuddy.dtos.UserDto;
import com.timebuddy.models.TodoList;
import com.timebuddy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


/**
 * Repository interface for managing TodoList entities in the database.
 * Provides methods to find, save, and delete TodoLists for a user based on the date.
 */
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    /**
     * Retrieves a TodoList for a specific user and date.
     *
     * @param user The user whose TodoList is being retrieved.
     * @param date The date of the TodoList.
     * @return An Optional containing the TodoList if it exists, otherwise empty.
     */
    Optional<TodoList> findByUserAndDate(User user, LocalDate date);

    /**
     * Deletes all TodoLists before a specified date for a given user.
     *
     * @param user The user whose TodoLists are to be deleted.
     * @param date The date before which the TodoLists will be deleted.
     */
    void deleteByUserAndDateBefore(User user, LocalDate date);

    /**
     * Retrieves all TodoLists for a specific user.
     *
     * @param user The user whose TodoLists are to be retrieved.
     * @return A list of TodoLists associated with the user.
     */
    List<TodoList> findByUser(User user);
}


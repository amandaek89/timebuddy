package com.timebuddy.services;

import com.timebuddy.models.Todo;
import com.timebuddy.dtos.TodoListResponseDto;
import com.timebuddy.models.TodoList;
import com.timebuddy.models.User;
import com.timebuddy.repositories.TodoListRepository;
import com.timebuddy.repositories.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing TodoLists.
 */
@Service
public class TodoListService {

    private final TodoListRepository todoListRepository;

    private final UserRepository userRepository;

    /**
     * Constructor for TodoListService.
     *
     * @param todoListRepository The repository used to interact with the TodoList database table.
     */

    public TodoListService(TodoListRepository todoListRepository, UserRepository userRepository) {
        this.todoListRepository = todoListRepository;
        this.userRepository = userRepository;
    }
    /**
     * Retrieves an existing TodoList or creates a new one for the specified user and date.
     * If no TodoList exists for the given date, a new TodoList is created, saved, and returned.
     *
     * @param user The user whose TodoList is being retrieved or created.
     * @param date The date for the TodoList.
     * @return The existing or newly created TodoList entity.
     */
    public TodoList getOrCreateTodoList(User user, LocalDate date) {
        // Kontrollera om en TodoList redan finns för användaren och datumet
        Optional<TodoList> existingTodoList = todoListRepository.findByUserAndDate(user, date);

        // Om den inte finns, skapa en ny TodoList
        if (existingTodoList.isEmpty()) {
            TodoList newTodoList = new TodoList();
            newTodoList.setUser(user);  // Länka användaren
            newTodoList.setDate(date);  // Sätt datumet
            newTodoList.setTodos(new ArrayList<>());  // Initialisera tom lista

            // Spara och returnera den nya TodoList
            return todoListRepository.save(newTodoList);
        }

        // Returnera den existerande TodoList
        return existingTodoList.get();
    }

    /**
     * Retrieves all TodoLists for a specific user.
     *
     * @param user The user whose TodoLists are to be retrieved.
     * @return A list of TodoLists associated with the user.
     */
    public List<TodoListResponseDto> getTodoListsForUser(User user) {
        List<TodoList> todoLists = todoListRepository.findByUser(user);
        return todoLists.stream()
                .map(todoList -> new TodoListResponseDto(todoList.getDate(), todoList.getTodos().stream()
                        .map(Todo::getTitle)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * Fetches TodoLists for the currently logged-in user within a specific month and year.
     * The method first ensures that the user is authenticated by checking the logged-in user's username.
     *
     * @param month The month (1-12) for which TodoLists should be fetched.
     * @param year The year for which TodoLists should be fetched.
     * @param userDetails The currently logged-in user's details, injected via @AuthenticationPrincipal.
     * @return A list of TodoListResponseDto objects, each representing a TodoList for a specific date in the given month.
     * @throws RuntimeException if the user is not found in the system or there is an error fetching the data.
     */
    public List<TodoListResponseDto> getTodoListsForMonth(int month, int year, @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the logged-in user's username
        String loggedInUsername = userDetails.getUsername();

        // Fetch the user from the database based on the username
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Define the start and end date of the requested month
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        // Fetch the TodoLists for the user within the date range of the specified month
        List<TodoList> todoLists = todoListRepository.findByUserAndDateBetween(user, startOfMonth, endOfMonth);

        // Map TodoLists to TodoListResponseDto and return as a list
        return todoLists.stream()
                .map(todoList -> new TodoListResponseDto(todoList.getDate(), todoList.getTodos().stream()
                        .map(Todo::getTitle)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * Fetches TodoLists for the currently logged-in user within the current week.
     * The method first ensures that the user is authenticated by checking the logged-in user's username.
     *
     * @param userDetails The currently logged-in user's details, injected via @AuthenticationPrincipal.
     * @return A list of TodoListResponseDto objects, each representing a TodoList for a specific day in the current week.
     * @throws RuntimeException if the user is not found in the system or there is an error fetching the data.
     */
    public List<TodoListResponseDto> getTodoListsForCurrentWeek(
            @AuthenticationPrincipal UserDetails userDetails) {
        // Retrieve the logged-in user's username
        String loggedInUsername = userDetails.getUsername();

        // Fetch the user from the database based on the username
        User user = userRepository.findByUsername(loggedInUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate the start of the current week (Monday of the current week)
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);

        // Calculate the end of the current week (Sunday of the current week)
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Fetch the TodoLists for the user within the date range of the current week
        List<TodoList> todoLists = todoListRepository.findByUserAndDateBetween(user, startOfWeek, endOfWeek);

        // Map TodoLists to TodoListResponseDto and return as a list
        return todoLists.stream()
                .map(todoList -> new TodoListResponseDto(todoList.getDate(), todoList.getTodos().stream()
                        .map(Todo::getTitle)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}

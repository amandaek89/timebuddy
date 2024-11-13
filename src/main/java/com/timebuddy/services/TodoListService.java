package com.timebuddy.services;

import com.timebuddy.models.Todo;
import com.timebuddy.dtos.TodoListResponseDto;
import com.timebuddy.models.TodoList;
import com.timebuddy.models.User;
import com.timebuddy.repositories.TodoListRepository;
import org.springframework.stereotype.Service;

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

    /**
     * Constructor for TodoListService.
     *
     * @param todoListRepository The repository used to interact with the TodoList database table.
     */
    public TodoListService(TodoListRepository todoListRepository) {
        this.todoListRepository = todoListRepository;
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


}

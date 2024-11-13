package com.timebuddy.services;

import com.timebuddy.dtos.TodoRequestDto;
import com.timebuddy.dtos.TodoResponseDto;
import com.timebuddy.exceptions.TodoNotFoundException;
import com.timebuddy.models.Todo;
import com.timebuddy.models.TodoList;
import com.timebuddy.models.User;
import com.timebuddy.repositories.TodoListRepository;
import com.timebuddy.repositories.TodoRepository;
import com.timebuddy.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing Todo tasks.
 * Provides methods to create, update, delete, and retrieve Todo tasks.
 */
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoListService todoListService;
    private final UserRepository userRepository;

    private final TodoListRepository todoListRepository;
    /**
     * Constructor for TodoService.
     *
     * @param todoRepository
     * @param todoListService
     * @param userRepository
     * @param todoListRepository
     */
    public TodoService(TodoRepository todoRepository, TodoListService todoListService,
                       UserRepository userRepository, TodoListRepository todoListRepository) {
        this.todoRepository = todoRepository;
        this.todoListService = todoListService;
        this.userRepository = userRepository;
        this.todoListRepository = todoListRepository;
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
     * Adds a new Todo task to the TodoList for the specified user on a specific date.
     *
     * @param user          The authenticated user to which the Todo belongs.
     * @param date          The date for which the Todo task is created.
     * @param todoRequestDto The request data containing title and description for the Todo task.
     * @return The created Todo task.
     */
    public Todo addTodoToSpecificDay(User user, LocalDate date, TodoRequestDto todoRequestDto) {
        // Hämta eller skapa TodoList för det specifika datumet
        TodoList todoList = todoListService.getOrCreateTodoList(user, date);

        // Skapa en ny Todo med information från TodoRequestDto
        Todo todo = new Todo();
        todo.setTitle(todoRequestDto.getTitle());
        todo.setDescription(todoRequestDto.getDescription());
        todo.setDone(false);  // Ny Todo är inte klar som standard
        todo.setTodoList(todoList);  // Koppla Todo till TodoList för det aktuella datumet

        // Spara Todo och returnera den
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
     * Retrieves a Todo task by its title.
     *
     * @param title The title of the Todo task to be retrieved.
     * @return A list of Todo tasks matching the title, or an empty list if none are found.
     */
    public List<Todo> getTodoByTitle(String title) {
        return todoRepository.findByTitle(title);
    }

    /**
     * Retrieves all Todos for a specific user and date by finding the associated TodoList.
     * If no TodoList exists for the specified date, an exception is thrown.
     *
     * @param user The user whose Todos are being retrieved.
     * @param date The date for which Todos are to be retrieved.
     * @return A list of Todos associated with the specified user and date.
     * @throws RuntimeException if no TodoList is found for the given user and date.
     */
    public List<TodoResponseDto> getTodosForDate(User user, LocalDate date) {
        // Retrieve the TodoList for the user and date
        TodoList todoList = todoListRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new RuntimeException("TodoList not found for this date"));

        // Map the Todos to TodoResponseDto
        return todoList.getTodos().stream()
                .map(todo -> new TodoResponseDto(
                        todo.getTitle(),
                        todo.getDescription(),
                        todo.isDone(),
                        todo.getTodoList().getDate()
                ))
                .collect(Collectors.toList());
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
                    return todoRepository.save(todo);
                });
    }

    /**
     * Deletes a Todo task by its ID.
     *
     * @param id The ID of the Todo task to be deleted.
     * @throws TodoNotFoundException If the Todo task with the given ID does not exist.
     */
    public void deleteTodo(Long id) throws TodoNotFoundException {
        Optional<Todo> todo = todoRepository.findById(id);

        if (todo.isEmpty()) {
            throw new TodoNotFoundException("Todo task with ID " + id + " not found.");
        }

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
package com.timebuddy.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a TodoList entity that contains a list of todos.
 * This class is mapped to the "todolist" table in the database.
 * A TodoList is associated with a specific user and holds multiple todos.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todolist")
public class TodoList {

    /** The unique identifier for the TodoList. */
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID value.
    private Long id;

    /** The date of the TodoList. */
    @Column(nullable = false)
    private LocalDate date; // Using LocalDate for the date to make it more standardized.

    /** The user who created the TodoList. */
    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key column to associate with the user.
    private User user;

    /** The list of todos that belong to the TodoList. */
    @OneToMany(mappedBy = "todoList", cascade = CascadeType.ALL, orphanRemoval = true) // Cascades all operations to related Todo entities.
    private List<Todo> todos = new ArrayList<>(); // Initialize with an empty list.

    /** Add Todo to the TodoList. */
    public void addTodo(Todo todo) {
        todos.add(todo);
        todo.setTodoList(this);  // Set the back reference from Todo to TodoList
    }

}


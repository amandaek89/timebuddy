package com.timebuddy.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Represents a TodoList entity that contains a list of todos.
 * This class is mapped to the "todolist" table in the database.
 * A TodoList is associated with a specific user, and it holds multiple todos.
 *
 * @param id The unique identifier for the TodoList.
 * @param title The title of the TodoList.
 * @param user The user who owns the TodoList.
 * @param todos The list of todos contained in the TodoList.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todolist") // Specifies the table name "todolist" in the database.
public class TodoList {

    /** The unique identifier for the TodoList. */
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID value.
    private Long id;

    /** The title of the TodoList. */
    private String title;

    /** The user who created the TodoList. */
    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key column to associate with the user.
    private User user;

    /** The list of todos that belong to the TodoList. */
    @OneToMany(mappedBy = "todoList", cascade = CascadeType.ALL) // Cascades all operations to related Todo entities.
    private List<Todo> todos;
}

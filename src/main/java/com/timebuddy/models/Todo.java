package com.timebuddy.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalTime;

/**
 * Represents a Todo task that belongs to a specific TodoList and User.
 * Contains information about the task's title, description, time, and completion status.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todo")
public class Todo {

    /**
     * The unique ID for this Todo task.
     */
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generates an ID
    private Long id;

    /**
     * The title of the Todo task.
     */
    @Column(nullable = false)
    @Size(min = 1, max = 100)
    private String title;

    /**
     * A description of the Todo task.
     */
    private String description;

    /**
     * Indicates whether the Todo task is completed or not.
     */
    private boolean done;

    /**
     * The time at which this Todo task should be performed. Null if it is an all-day task.
     */
    private LocalTime time;

    /**
     * Indicates whether this Todo task is an "all-day" task (i.e., no specific time).
     */
    private boolean allDay;

    /**
     * A reference to the TodoList to which this Todo belongs.
     */
    @ManyToOne
    @JoinColumn(name = "todolist_id") // Links the Todo to a specific TodoList
    private TodoList todoList;


    /**
        * A reference to the User who created this Todo.
        */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Todo(long l, String existingTodo, String existingDescription, boolean b, LocalTime of) {
    }
}

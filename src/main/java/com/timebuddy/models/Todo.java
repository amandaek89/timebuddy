package com.timebuddy.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
/**
 * Represents a Todo task, which can be associated with a specific TodoList.
 * A Todo has a title, description, and a status field to indicate whether it is completed or not.
 * Example usage:
 *   - Title: "Buy milk"
 *   - Description: "Don't forget to buy milk while shopping"
 *   - Status: "Not done"
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
     * A reference to the TodoList to which this Todo belongs.
     */
    @ManyToOne
    @JoinColumn(name = "todolist_id") // Links the Todo to a specific TodoList
    private TodoList todoList;

}

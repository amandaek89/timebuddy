package com.timebuddy.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a user in the system.
 * This class holds the user’s details including their username, password,
 * creation and update timestamps, and the lists of todos they have created.
 * It is mapped to the "user" table in the database.
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")  // Specifies the table name "user" in the database.
public class User {

    /** The unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** The unique username for the user. */
    @Column(nullable = false, unique = true)
    private String username;

    /** The password for the user, stored in a hashed format. */
    @Column(nullable = false)
    private String password;

    /** The date and time when the user was created. */
    private Date createdAt;

    /** The date and time when the user was last updated. */
    private Date updatedAt;

    /** The list of todo lists created by the user. */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TodoList> todoLists;

}


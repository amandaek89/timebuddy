package com.timebuddy.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;

/**
 * Represents a user in the system.
 * This class holds the user’s details including their username, password,
 * creation and update timestamps, and the lists of todos they have created.
 * It is mapped to the "user" table in the database.
 */

@Data
@Entity
@Table(name = "user")  // Specifies the table name "user" in the database.
public class User implements UserDetails {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The unique username for the user.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The password for the user, stored in a hashed format.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The date and time when the user was created.
     */
    private Date createdAt;

    /**
     * The date and time when the user was last updated.
     */
    private Date updatedAt;

    /**
     * The list of todo lists created by the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TodoList> todoLists;

    /**
    * The list of todos created by the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Todo> todos;


    /**
        * The list of roles assigned to the user.
        */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }

    public User(long l, String validUser, String validPassword, Date date, Date date1, Object o) {
    }

    public <E> User(String testuser, String password, ArrayList<E> es) {
    }

    public Set<Role> getRoles() {
        return authorities;
    }

    public void setRoles(Set<Role> authorities) {
        this.authorities = authorities;
    }
}
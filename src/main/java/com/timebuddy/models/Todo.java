package com.timebuddy.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todo")
public class Todo {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //automatiskt generering av ett id
    private Long id;
    private String title;
    private String description;
    private boolean done;

    @ManyToOne
    @JoinColumn(name = "todolist_id") // Kopplar till TodoList
    private TodoList todoList;

}

package com.timebuddy.dtos;

import lombok.Data;

@Data
public class TodoRequestDto {
    private String title;
    private String description;
    private boolean done;

    public TodoRequestDto() {
    }

    public TodoRequestDto(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.done = done;
    }
}

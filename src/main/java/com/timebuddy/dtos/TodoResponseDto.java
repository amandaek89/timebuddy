package com.timebuddy.dtos;

import lombok.Data;

@Data
public class TodoResponseDto {

    private Long id;
    private String title;
    private String description;
    private boolean done;
}

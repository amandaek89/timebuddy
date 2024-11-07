package com.timebuddy.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic class for encapsulating API responses.
 * It includes the status, message, and any associated data.
 *
 * @param <T> The type of the data included in the response (e.g., JWT token, user info).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;  // HTTP status code
    private String message;  // A message indicating the result of the operation
    private T data;  // Data related to the response (e.g., JWT token)
}
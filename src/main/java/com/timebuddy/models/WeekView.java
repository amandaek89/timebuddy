package com.timebuddy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents the week view, which holds a list of days.
 * This class is used for displaying the tasks organized by day in a weekly format.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekView {

    /** List of days in the week. Each day contains its own list of todos. */
    private List<Day> days;
}


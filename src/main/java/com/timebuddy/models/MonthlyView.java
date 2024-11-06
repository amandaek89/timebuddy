package com.timebuddy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

/**
 * Represents a monthly view that contains all events occurring during the month.
 * It uses a map to associate specific dates with their corresponding events.
 *
 * Example usage:
 *   - Date: 2024-11-06
 *   - Event: "Planning meeting"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyView {

    /**
     * A mapping of dates in the month to a list of events scheduled for those dates.
     * The key is the date, and the value is a list of events planned for that day.
     */
    private Map<LocalDate, List<MonthlyEvent>> events;

    /**
     * Adds an event for a specific date in the monthly view.
     * If events already exist for the date, the new event is added to the list.
     *
     * @param date The date to which the event will be added.
     * @param event The event to be added.
     */
    public void addEvent(LocalDate date, MonthlyEvent event) {
        events.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
    }
}



package com.timebuddy.repositories;

import com.timebuddy.models.MonthlyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for the MonthlyEvent entity.
 * Provides CRUD operations for MonthlyEvent objects and custom query methods.
 * Extends JpaRepository to benefit from built-in functionality for database operations.
 */
@Repository
public interface MonthlyEventRepository extends JpaRepository<MonthlyEvent, Long> {

    /**
     * Finds MonthlyEvents by their event date.
     * This method will return all events that are scheduled for a specific date.
     *
     * @param date The date for which the events should be retrieved.
     * @return A list of MonthlyEvent objects that are scheduled for the provided date.
     */
    List<MonthlyEvent> findByEventDate(LocalDate date);
}



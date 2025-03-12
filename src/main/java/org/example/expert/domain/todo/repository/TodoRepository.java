package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN t.user " +
            "WHERE t.id = :todoId")
    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);

    @Query("SELECT T FROM Todo T WHERE LOWER(T.weather) LIKE LOWER(CONCAT('%', :weather, '%'))")
    Page<Todo> findAllByWeatherLikeIgnoreCase(Pageable pageable, String weather);

    @Query("SELECT t FROM Todo t WHERE t.modifiedAt BETWEEN :startDate AND :endDate")
    Page<Todo> findAllByModifiedAtBetween(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT T FROM Todo T WHERE LOWER(T.weather) LIKE LOWER(CONCAT('%', :weather, '%')) AND" +
            " T.modifiedAt BETWEEN :startDate AND :endDate")
    Page<Todo> findAllByWeatherAndModifiedAt(Pageable pageable, String weather, LocalDateTime startDate, LocalDateTime endDate);
}

package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoQueryDSLDTO;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoRepositoryCustom {
    Optional<Todo> findByIdWithUser(Long id);

    Page<TodoQueryDSLDTO> findAllByTitle(Pageable pageable, String title);
}

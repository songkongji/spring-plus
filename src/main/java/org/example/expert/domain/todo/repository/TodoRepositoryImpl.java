package org.example.expert.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Optional<Todo> findByIdWithUser(Long id) {
        Todo result = query.selectFrom(QTodo.todo)
                .leftJoin(QTodo.todo.user, QUser.user).fetchJoin()
                .where(QTodo.todo.id.eq(id))
                .fetchOne();    //단일 결과를 가져옴
        return Optional.ofNullable(result);
    }
}

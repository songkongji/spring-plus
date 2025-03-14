package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoQueryDSLDTO;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.comment.entity.QComment.comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Optional<Todo> findByIdWithUser(Long id) {
        Todo result = query.selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(id))
                .fetchOne();    //단일 결과를 가져옴
        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoQueryDSLDTO> findAllByTitle(Pageable pageable, String title) {
        List<TodoQueryDSLDTO> todoList = query.select(Projections.constructor(TodoQueryDSLDTO.class,
                        todo.id,
                        todo.title,
                        query.select(manager.count())   //서브쿼리로 매니저랑 코멘트 숫자 가져와서 N + 1 문제 해결. List 타입은 fetchJoin 2개 이상 하면 오류 터짐
                                .from(manager)
                                .where(manager.todo.id.eq(todo.id)),
                        query.select(comment.count())
                                .from(comment)
                                .where(comment.todo.id.eq(todo.id))))
                .from(todo)
                .leftJoin(todo.user, user)
                .where(todo.title.likeIgnoreCase(title))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(todoList, pageable, todoList.size());
    }
}

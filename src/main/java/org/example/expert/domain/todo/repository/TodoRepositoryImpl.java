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

import java.time.LocalDateTime;
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
                .where(todo.title.likeIgnoreCase("%" + title + "%"))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(todoList, pageable, todoList.size());
    }

    @Override
    public Page<TodoQueryDSLDTO> findAllByCreatedAtDesc(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate) {
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
                .where(todo.createdAt.between(startDate, endDate))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(todoList, pageable, todoList.size());
    }

    @Override
    public Page<TodoQueryDSLDTO> findAllByNickname(Pageable pageable, String nickname) {
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
                .leftJoin(todo.managers, manager)   //매니저 닉네임 찾기위해 조인
                .where(manager.user.nickname.like("%" +nickname + "%"))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(todoList, pageable, todoList.size());
    }
}

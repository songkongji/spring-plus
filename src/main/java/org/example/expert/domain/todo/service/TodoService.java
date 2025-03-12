package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.todo.repository.TodoRepositoryImpl;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final TodoRepositoryImpl todoRepositoryImpl;

    @Transactional
    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail(), user.getNickname())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, String start, String end) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if (weather != null && start != null && end != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter); //string 타입으로 적은 시작날짜와 끝날짜 파싱

            Page<Todo> allByWeatherAndModifiedAt = todoRepository.findAllByWeatherAndModifiedAt(pageable, weather, startDate, endDate);
            return todoResponsePage(allByWeatherAndModifiedAt);
        }

        if(weather != null) {
            String weatherPattern = "%" + weather + "%";    //jpql 미사용시 필요함. 대소문자 구분 없이 부분 일치하는 검색을 할 수 있도록 와일드 카드 사용

            Page<Todo> weatherTodos = todoRepository.findAllByWeatherLikeIgnoreCase(pageable, weatherPattern);
            return todoResponsePage(weatherTodos);
        }

        if (start != null && end != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");   
            LocalDateTime startDate = LocalDateTime.parse(start, formatter);
            LocalDateTime endDate = LocalDateTime.parse(end, formatter); //string 타입으로 적은 시작날짜와 끝날짜 파싱

            Page<Todo> modifiedAtTodos = todoRepository.findAllByModifiedAtBetween(pageable, startDate, endDate);   //적은 기간 사이의 할일들이 나옴
            return todoResponsePage(modifiedAtTodos);
        }

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        return todoResponsePage(todos);
    }

    public TodoResponse getTodo(long todoId) {
//        Todo todo = todoRepository.findByIdWithUser(todoId)
//                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        Todo todo = todoRepositoryImpl.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail(), user.getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

    private Page<TodoResponse> todoResponsePage(Page<Todo> page) {
        return page.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail(), todo.getUser().getNickname()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }
}

package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity
@NoArgsConstructor
@Getter
//@AttributeOverride(name = "modifiedAt", column = @Column(insertable = false, updatable = false))
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Enumerated(EnumType.STRING)
    private HttpStatus httpStatus;

    private LocalDateTime createdAt;

    public Log(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}

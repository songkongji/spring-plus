package org.example.expert.domain.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

    @PrePersist
    public void onPrePersist() {    //생성시 나노초 저장안하기
        this.createdAt = createdAt.truncatedTo(ChronoUnit.SECONDS);
        this.modifiedAt = modifiedAt.truncatedTo(ChronoUnit.SECONDS);
    }

    @PreUpdate
    public void onPreUpdate() { //업뎃시 나노초 저장안하기
        this.modifiedAt = modifiedAt.truncatedTo(ChronoUnit.SECONDS);
    }
}

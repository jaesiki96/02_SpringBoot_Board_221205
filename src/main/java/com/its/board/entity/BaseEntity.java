package com.its.board.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {
    @CreationTimestamp // insert 발생 했을 때 작동
    @Column(updatable = false) // update 가 발생했을 때 작동 X => 이 옵션을 주지 않으면 최초 작성시간이 바뀐다.
    private LocalDateTime createdTime;

    @UpdateTimestamp  // update 발생 했을 때 작동
    @Column(insertable = false) // insert 가 발생했을 때 작동 X => 이 옵션을 주지 않으면 insert 작동
    private LocalDateTime updatedTime;
}

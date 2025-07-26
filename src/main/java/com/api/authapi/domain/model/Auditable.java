package com.api.authapi.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Audited
public abstract class Auditable<T> {

    @CreatedBy
    @Column(name = "created_by", updatable = false, nullable = false, length = 100)
    protected T createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    protected Instant createdAt;

    @LastModifiedBy
    @Column(name = "modified_by", length = 100)
    protected T modifiedBy;

    @LastModifiedDate
    @Column(name = "modified_at")
    protected Instant modifiedAt;
}

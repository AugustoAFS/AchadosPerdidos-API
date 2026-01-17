package com.AchadosPerdidos.API.Domain.Entity;

import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseEntity {
    private boolean Active;
    private LocalDateTime Created_At;
    private LocalDateTime Deleted_At;
}

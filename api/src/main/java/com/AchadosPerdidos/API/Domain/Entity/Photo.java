package com.AchadosPerdidos.API.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "photo", schema = "ap")
public class Photo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "file_type", length = 50)
    private String fileType;
}

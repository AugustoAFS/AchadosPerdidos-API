package com.AchadosPerdidos.API.Domain.Entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "item_photo", schema = "ap")
public class Item_Photo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "photo_id", nullable = false)
    private Integer photoId;
}

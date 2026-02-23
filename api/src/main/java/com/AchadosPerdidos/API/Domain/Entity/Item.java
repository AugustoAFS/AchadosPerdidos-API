package com.AchadosPerdidos.API.Domain.Entity;

import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "item", schema = "ap")
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_item", nullable = false, length = 20)
    private Type_Item typeItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_item", nullable = false, length = 20)
    private Status_Item statusItem;

    @Column(name = "meeting_location", length = 255)
    private String meetingLocation;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "campus_id", nullable = false)
    private Integer campusId;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "author_user_id", nullable = false)
    private Integer authorUserId;

    @Column(name = "receiver_user_id")
    private Integer receiverUserId;
}

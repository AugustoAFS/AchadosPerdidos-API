package com.AchadosPerdidos.API.Application.DTOs.Response.Item;

import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemResponseDTO {

    private Integer id;
    private String title;
    private String description;
    private Type_Item typeItem;
    private Status_Item statusItem;
    private String meetingLocation;
    private LocalDateTime postedAt;
    private LocalDateTime deliveredAt;
    private Integer campusId;
    private String campusName;
    private Integer categoryId;
    private String categoryName;
    private Integer authorUserId;
    private String authorUserName;
    private Integer receiverUserId;
    private List<String> photoUrls;
}

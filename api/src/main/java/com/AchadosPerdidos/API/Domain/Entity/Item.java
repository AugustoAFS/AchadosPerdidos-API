package com.AchadosPerdidos.API.Domain.Entity;

import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item extends BaseEntity {
    private Integer Id;
    private String Title;
    private String Description;
    private Type_Item Type_Item;
    private Status_Item Status_Item;
    private String Meeting_Location;

    private LocalDate Posted_At;
    private LocalDate Delivered_At;

    private Integer Campus_Id;
    private Integer Category_Id;
    private Integer Autor_User_Id;
    private Integer Receiver_User_Id;
}

package com.AchadosPerdidos.API.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item_Photo extends BaseEntity {
    private Integer Id;
    private Integer Item_Id;
    private Integer Photo_Id;
}

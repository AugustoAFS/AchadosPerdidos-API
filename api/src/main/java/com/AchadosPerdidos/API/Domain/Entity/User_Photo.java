package com.AchadosPerdidos.API.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_Photo extends BaseEntity {
    private Integer Id;
    private Integer User_Id;
    private Integer Photo_Id;
}

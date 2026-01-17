package com.AchadosPerdidos.API.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users extends BaseEntity {
    private Integer Id;
    private String Name;
    private String Email;
    private String Password_Hash;
    private Date Birth_Date;

    private Integer Role_Id;
    private Integer Campus_Id;
}

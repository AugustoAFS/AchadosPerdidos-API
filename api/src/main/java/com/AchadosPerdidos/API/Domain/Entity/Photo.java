package com.AchadosPerdidos.API.Domain.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends BaseEntity {
    private Integer Id;
    private String Url;
    private String File_Name;
    private BigInteger Size_Bytes;
    private String File_Type;
}

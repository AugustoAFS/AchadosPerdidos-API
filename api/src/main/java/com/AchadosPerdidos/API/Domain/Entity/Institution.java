package com.AchadosPerdidos.API.Domain.Entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Institution extends BaseEntity {
    private Integer Id;
    private String Name;
    private String Prefix;
    private String CNPJ;
}

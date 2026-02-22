package com.AchadosPerdidos.API.Application.DTOs.Request.User;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpdateUserRequestDTO {

    private String name;

    private Date birthDate;

    private List<Integer> campusIds;
}

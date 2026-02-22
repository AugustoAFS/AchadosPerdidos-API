package com.AchadosPerdidos.API.Application.DTOs.Response.User;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UsuariosDTO {

    private Integer id;
    private String name;
    private String email;
    private Date birthDate;
    private Integer roleId;
    private String roleName;
    private List<Integer> campusIds;
    private List<String> campusNames;
    private String photoUrl;
    private boolean active;
}

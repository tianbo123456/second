package com.example.project.controller.domain;

import com.example.project.entity.Permission;
import com.example.project.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user;
    private String token;
    private List<Permission> menus;
    private List<Permission> auths;
}

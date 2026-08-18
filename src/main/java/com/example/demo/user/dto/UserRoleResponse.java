package com.example.demo.user.dto;

import com.example.demo.user.entity.Role;

public record UserRoleResponse(
        Long id,
        String nome,

        Role role,
        String userName
) {
}

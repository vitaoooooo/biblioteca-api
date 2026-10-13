package br.com.biblioteca_api.dto;

import br.com.biblioteca_api.model.AppUser;
import br.com.biblioteca_api.model.Role;

public record UserResponse(Long id, String name, String email, Role role, boolean active) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive());
    }
}

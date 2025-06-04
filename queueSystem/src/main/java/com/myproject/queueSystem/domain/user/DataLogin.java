package com.myproject.queueSystem.domain.user;

import jakarta.validation.constraints.NotBlank;

public record DataLogin(@NotBlank
                         String login,
                         @NotBlank
                         String senha) {
}

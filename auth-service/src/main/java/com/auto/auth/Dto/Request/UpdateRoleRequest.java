package com.auto.auth.Dto.Request;

import com.auto.auth.Enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotNull
    private Role role;
}

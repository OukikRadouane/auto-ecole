package com.auto.auth.Dto.Response;

import com.auto.auth.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private boolean emailVerified;
}

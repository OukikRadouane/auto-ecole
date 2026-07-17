package com.auto.auth.Dto.Request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;

    @Size(min = 10, max = 20)
    private String phone;
}

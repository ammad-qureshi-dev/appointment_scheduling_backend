package com.booker_app.backend_service.controllers.response.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private UUID userId;
}

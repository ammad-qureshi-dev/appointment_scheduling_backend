package com.booker_app.backend_service.controllers.response.dto;

import com.booker_app.backend_service.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String label;
    private String role;
    private UUID contextId;
}

package com.booker_app.backend_service.controllers.response.dto;


import com.booker_app.backend_service.models.ServiceLength;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    private String name;
    private String description;
    private Double price;
    private Integer time;
    private ServiceLength serviceLength;
}

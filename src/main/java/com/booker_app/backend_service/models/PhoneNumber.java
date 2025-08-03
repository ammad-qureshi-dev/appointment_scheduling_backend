package com.booker_app.backend_service.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PhoneNumber {
    private Integer countryCode;
    private Integer areaCode;
    private Integer number;
}

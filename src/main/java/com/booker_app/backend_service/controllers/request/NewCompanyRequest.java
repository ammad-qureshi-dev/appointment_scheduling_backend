package com.booker_app.backend_service.controllers.request;

import com.booker_app.backend_service.models.PhoneNumber;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class NewCompanyRequest {

    @NonNull
    private String name;

    private String description;

    @NonNull
    private String address;
}

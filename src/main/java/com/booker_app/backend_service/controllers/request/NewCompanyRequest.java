package com.booker_app.backend_service.controllers.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompanyRequest {

    @NonNull
    private String name;

    private String description;

    @NonNull
    private String address;
}

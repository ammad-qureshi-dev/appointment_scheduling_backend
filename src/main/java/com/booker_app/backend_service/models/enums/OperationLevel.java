package com.booker_app.backend_service.models.enums;

import lombok.Getter;

@Getter
public enum OperationLevel {
    NONE(0),
    READ(1),
    UPDATE(2),
    CREATE(3),
    DELETE(4);

    private final int level;

    OperationLevel(int level) {
        this.level = level;
    }

}

package com.booker_app.backend_service.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverterUtil implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return "{" + String.join(",", attribute) + "}";
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.length() < 2) return Collections.emptyList();
        String trimmed = dbData.substring(1, dbData.length() - 1);
        return Arrays.asList(trimmed.split(","));
    }
}


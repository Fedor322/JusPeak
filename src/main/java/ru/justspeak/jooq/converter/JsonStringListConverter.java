package ru.justspeak.jooq.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSON;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.AbstractConverter;

import java.util.List;

/** Converts the JSON type exposed by DDLDatabase to a string list. */
public final class JsonStringListConverter extends AbstractConverter<JSON, List<String>> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> LIST_TYPE = new TypeReference<>() {};

    public JsonStringListConverter() {
        super(JSON.class, listType());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Class<List<String>> listType() {
        return (Class) List.class;
    }

    @Override
    public List<String> from(JSON value) {
        if (value == null) {
            return List.of();
        }
        try {
            JsonNode node = MAPPER.readTree(value.data());
            if (node != null && node.isNull()) {
                return List.of();
            }
            if (node == null || !node.isArray()) {
                throw new DataTypeException("Profile photos must be a JSON array of strings");
            }
            for (JsonNode item : node) {
                if (!item.isTextual()) {
                    throw new DataTypeException("Profile photos must contain only strings");
                }
            }
            return MAPPER.convertValue(node, LIST_TYPE);
        } catch (JsonProcessingException e) {
            throw new DataTypeException("Cannot deserialize profile photos", e);
        }
    }

    @Override
    public JSON to(List<String> value) {
        if (value == null) {
            return null;
        }
        for (Object item : value) {
            if (!(item instanceof String)) {
                throw new DataTypeException("Profile photos must contain only strings");
            }
        }
        try {
            return JSON.valueOf(MAPPER.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new DataTypeException("Cannot serialize profile photos", e);
        }
    }
}

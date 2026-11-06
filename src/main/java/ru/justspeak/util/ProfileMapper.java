package ru.justspeak.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSONB;
import ru.justspeak.api.dto.ProfileResponse;
import ru.justspeak.api.dto.UpdateProfileRequest;
import ru.justspeak.jooq.tables.records.ProfilesRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProfileMapper {

    private static final ObjectMapper mapper = new ObjectMapper();



    private static List<String> convertJsonbToList(Object jsonbData) {
        if (jsonbData == null) {
            return new ArrayList<>();
        }

        try {
            if (jsonbData instanceof String) {
                return mapper.readValue((String) jsonbData, new TypeReference<List<String>>() {
                });
            }
            return mapper.convertValue(jsonbData, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    private static JSONB convertListToJsonb(List<?> jsonbData) {
        if (jsonbData == null) {
            return null;
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(jsonbData);
            return JSONB.valueOf(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert list to JSONB", e);
        }
    }


    public static ProfilesRecord updateProfileRecordFromRequest(ProfilesRecord record, UpdateProfileRequest request ) {
        updateIfPresent(request.getFirstName(), record::setFirstName);
        updateIfPresent(request.getLastName(), record::setLastName);
        updateIfPresent(request.getDisplayName(), record::setDisplayName);
        updateIfPresent(request.getBiography(), record::setBiography);
        updateIfPresent(request.getAvatarUrl(), record::setAvatarUrl);
        updateIfPresent(request.getProfileUrl(), record::setProfileUrl);
        if (request.getProfilePhotos() != null) {
            record.setProfilePhotos(convertListToJsonb(request.getProfilePhotos()));
        }

        return record;
    }

    public static ProfileResponse mapToProfileResponse(ProfilesRecord profile) {
        return new ProfileResponse()
                .id(profile.getId())
                .userId(profile.getUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .displayName(profile.getDisplayName())
                .biography(profile.getBiography())
                .avatarUrl(profile.getAvatarUrl())
                .profilePhotos(convertJsonbToList(profile.getProfilePhotos()))
                .profileUrl(profile.getProfileUrl());
    }


    private static <T> void updateIfPresent( T value, Consumer<T> setter ) {
        if (value != null) {
            setter.accept(value);
        }
    }




}

package ru.justspeak.util;

import ru.justspeak.api.dto.ProfileResponse;
import ru.justspeak.api.dto.UpdateProfileRequest;
import ru.justspeak.jooq.tables.records.ProfilesRecord;

import java.util.List;
import java.util.function.Consumer;

public final class ProfileMapper {

    private ProfileMapper() {
    }

    public static ProfilesRecord updateProfileRecordFromRequest(ProfilesRecord record, UpdateProfileRequest request) {
        updateIfPresent(request.getFirstName(), record::setFirstName);
        updateIfPresent(request.getLastName(), record::setLastName);
        updateIfPresent(request.getDisplayName(), record::setDisplayName);
        updateIfPresent(request.getBiography(), record::setBiography);
        updateIfPresent(request.getAvatarUrl(), record::setAvatarUrl);
        updateIfPresent(request.getProfileUrl(), record::setProfileUrl);
        updateIfPresent(request.getProfilePhotos(), record::setProfilePhotos);
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
                .profilePhotos(profile.getProfilePhotos() == null ? List.of() : profile.getProfilePhotos())
                .profileUrl(profile.getProfileUrl());
    }

    private static <T> void updateIfPresent(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}

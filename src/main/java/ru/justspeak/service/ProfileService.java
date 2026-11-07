package ru.justspeak.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.justspeak.api.dto.ProfileResponse;
import ru.justspeak.api.dto.UpdateProfileRequest;
import ru.justspeak.exception.ResourceNotFoundException;
import ru.justspeak.jooq.tables.records.ProfilesRecord;
import ru.justspeak.repository.ProfileRepository;
import ru.justspeak.util.ProfileMapper;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileResponse getProfileById(Long profileId) {
        ProfilesRecord profilesRecord = profileRepository.findById(profileId);
        if (profilesRecord == null) {
            throw new ResourceNotFoundException("Profile not found with id: " + profileId);
        }
        return ProfileMapper.mapToProfileResponse(profilesRecord);
    }

    public ProfileResponse getProfileByUserId(Long userId) {
        ProfilesRecord record = profileRepository.findByUserId(userId);
        if (record == null) {
            throw new ResourceNotFoundException("Profile not found for userId: " + userId);
        }
        return ProfileMapper.mapToProfileResponse(record);
    }

    public ProfileResponse updateProfile(Long profileId, UpdateProfileRequest request, Long userId) {
        ProfilesRecord profilesRecord = profileRepository.findById(profileId);
        if (profilesRecord == null) {
            throw new ResourceNotFoundException("Profile not found with id: " + profileId);
        }

        if (!profilesRecord.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied: you can only update your own profile");
        }

        ProfileMapper.updateProfileRecordFromRequest(profilesRecord, request);
        if (!profilesRecord.changed()) {
            return ProfileMapper.mapToProfileResponse(profilesRecord);
        }

        int updated = profileRepository.update(profilesRecord, userId);
        if (updated == 0) {
            throw new ResourceNotFoundException("Profile not found with id: " + profileId);
        }

        return ProfileMapper.mapToProfileResponse(profilesRecord);
    }
}

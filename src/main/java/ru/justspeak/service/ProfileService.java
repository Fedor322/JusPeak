package ru.justspeak.service;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.justspeak.api.dto.ProfileResponse;
import ru.justspeak.api.dto.UpdateProfileRequest;
import ru.justspeak.jooq.tables.records.ProfilesRecord;
import ru.justspeak.repository.ProfileRepository;
import ru.justspeak.util.ProfileMapper;

import java.nio.file.AccessDeniedException;

import static ru.justspeak.util.ProfileMapper.mapToProfileResponse;


@Service
public class ProfileService {

    private final ProfileRepository profileRepository;


    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }


    public ProfileResponse getProfileById(Long profileId) {
        ProfilesRecord profilesRecord = profileRepository.findById(profileId);
        //TODO обработка ошибок
        return ProfileMapper.mapToProfileResponse(profilesRecord);
    }

    public ProfileResponse getProfileByUserId(Long userId) {
        ProfilesRecord record = profileRepository.findByUserId(userId);
        //TODO обработка ошибок
        return ProfileMapper.mapToProfileResponse(record);
    }




    public ProfileResponse updateProfile(Long profileId, UpdateProfileRequest request, Long userId) throws AccessDeniedException {
        ProfilesRecord profilesRecord = profileRepository.findById(profileId);

        //TODO обработка ошибок

        if (!profilesRecord.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        ProfileMapper.updateProfileRecordFromRequest(profilesRecord, request);
        profileRepository.update(profilesRecord);
        return ProfileMapper.mapToProfileResponse(profilesRecord);

    }









}

package ru.justspeak.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.justspeak.api.dto.ProfileResponse;
import ru.justspeak.api.dto.UpdateProfileRequest;
import ru.justspeak.model.CustomUserDetails;
import ru.justspeak.service.ProfileService;

import java.nio.file.AccessDeniedException;

@RestController
public class ProfileController {


    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @GetMapping("/api/users/{userId}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    @GetMapping("/api/profiles/{profileId}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable Long profileId) {
        return ResponseEntity.ok(profileService.getProfileById(profileId));
    }

    @PutMapping("/api/profiles/{profileId}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException {
        return ResponseEntity.ok(profileService.updateProfile(profileId, request, userDetails.getId()));
    }




}

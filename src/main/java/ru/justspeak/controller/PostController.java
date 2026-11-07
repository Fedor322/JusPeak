package ru.justspeak.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.justspeak.api.dto.ApiPostsGet200Response;
import ru.justspeak.api.dto.ApiPostsPost201Response;
import ru.justspeak.api.dto.ApiPostsPostIdPut200Response;
import ru.justspeak.api.dto.PostCreateRequest;
import ru.justspeak.api.dto.PostResponse;
import ru.justspeak.api.dto.PostUpdateRequest;
import ru.justspeak.model.CustomUserDetails;
import ru.justspeak.service.PostService;

@RestController
@RequestMapping("api/posts")
@Validated
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<ApiPostsPost201Response> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest postCreateRequest) {
        if (userDetails == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        Long currId = userDetails.getId();
        return ResponseEntity.status(201).body(postService.savePost(postCreateRequest, currId));
    }

    @GetMapping
    public ResponseEntity<ApiPostsGet200Response> getPosts(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index must not be less than zero") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size must not be greater than 100") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Boolean published,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.getPosts(page, size, sortBy, direction, published, currentUserId));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiPostsGet200Response> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return ResponseEntity.ok(postService.getPostsByUserId(userDetails.getId()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(postService.getPostById(postId, currentUserId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiPostsPostIdPut200Response> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateRequest, userDetails.getId()));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        postService.deletePost(postId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}

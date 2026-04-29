package ru.justspeak.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.justspeak.api.dto.ApiPostsPostIdCommentsGet200Response;
import ru.justspeak.api.dto.ApiPostsPostIdCommentsPost201Response;
import ru.justspeak.api.dto.CommentCreateRequest;
import ru.justspeak.model.CustomUserDetails;
import ru.justspeak.service.CommentService;

@RestController
@RequestMapping("api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiPostsPostIdCommentsPost201Response> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(201).body(commentService.createComment(postId, userDetails.getId(), request));
    }

    @GetMapping
    public ResponseEntity<ApiPostsPostIdCommentsGet200Response> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(commentService.getComments(postId, page, size, sortBy, direction));
    }
}

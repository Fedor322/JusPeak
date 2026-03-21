package ru.justspeak.controller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class PostController {


    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;

    }


    @PostMapping
    public ResponseEntity<ApiPostsPost201Response> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest postCreateRequest ) {


            Long currId = userDetails.getId();

            return ResponseEntity.status(201).body(postService.savePost(postCreateRequest, currId));
    }


// TODO   @GetMapping
//    public > getPosts(
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        return ResponseEntity.ok(postService.)
//    }
//
//    )


    @PutMapping("/{postId}")
    public ResponseEntity<ApiPostsPostIdPut200Response> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequest postUpdateRequest ) {
        return ResponseEntity.ok(postService.updatePost(postId, postUpdateRequest));
    }


    @GetMapping
    public ResponseEntity<ApiPostsGet200Response> getPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(postService.getPostsByUserId(userDetails.getId()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId) {
            return ResponseEntity.ok(postService.getPostById(postId));

    }




}

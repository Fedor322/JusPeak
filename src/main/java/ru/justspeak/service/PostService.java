package ru.justspeak.service;


import org.springframework.stereotype.Service;
import ru.justspeak.api.dto.ApiPostsGet200Response;
import ru.justspeak.api.dto.ApiPostsPost201Response;
import ru.justspeak.api.dto.ApiPostsPostIdPut200Response;
import ru.justspeak.api.dto.PostCreateRequest;
import ru.justspeak.api.dto.PostResponse;
import ru.justspeak.api.dto.PostUpdateRequest;
import ru.justspeak.api.dto.UserResponse;
import ru.justspeak.jooq.tables.records.PostsRecord;
import ru.justspeak.repository.PostRepository;
import ru.justspeak.util.PostMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public ApiPostsPost201Response savePost(PostCreateRequest request, Long userId) {
        PostsRecord record = PostMapper.toRecord(request);
        record.setUserid(userId);
        record.setContent(request.getContent());
        record.setViews(0L);
        record.setCommentCount(0L);
        record.setCreatedAt(LocalDateTime.now());
        PostsRecord savedRecord = postRepository.save(record);
        return PostMapper.toPost201Response(savedRecord);

    }


    public ApiPostsGet200Response getPostsByUserId(Long userId) {
        List<PostsRecord> records = postRepository.findAllPostByUserId(userId);
        return PostMapper.toPostsGet200Response(records);
    }

    public PostResponse getPostById(Long postId) {
        PostsRecord record = postRepository.findById(postId);
        return PostMapper.toResponse(record);
    }



    public ApiPostsPostIdPut200Response updatePost(Long id, PostUpdateRequest request) {
        PostsRecord record = postRepository.findById(id);
        PostsRecord newRecord = PostMapper.updateFromResponse(record, request);
        postRepository.update(newRecord);
        return new ApiPostsPostIdPut200Response().id(newRecord.getId()).title(newRecord.getTitle());

    }







}

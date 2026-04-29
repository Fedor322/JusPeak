package ru.justspeak.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.justspeak.api.dto.ApiPostsGet200Response;
import ru.justspeak.api.dto.ApiPostsPost201Response;
import ru.justspeak.api.dto.ApiPostsPostIdPut200Response;
import ru.justspeak.api.dto.PostCreateRequest;
import ru.justspeak.api.dto.PostResponse;
import ru.justspeak.api.dto.PostUpdateRequest;
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

    @Transactional
    public ApiPostsPost201Response savePost(PostCreateRequest request, Long userId) {
        PostsRecord record = PostMapper.toRecord(request);
        record.setUserid(userId);
        record.setViews(0L);
        record.setCommentCount(0L);
        record.setCreatedAt(LocalDateTime.now());
        PostsRecord savedRecord = postRepository.save(record);
        return PostMapper.toPost201Response(savedRecord);
    }

    public ApiPostsGet200Response getPosts(int page, int size, String sortBy, String direction, Boolean published) {
        List<PostsRecord> records = postRepository.findAll(page, size, sortBy, direction, published);
        long totalElements = postRepository.count(published);
        return PostMapper.toPostsGet200Response(records, totalElements, page, size);
    }

    public ApiPostsGet200Response getPostsByUserId(Long userId, int page, int size, String sortBy, String direction, Boolean published) {
        List<PostsRecord> records = postRepository.findAllByUserId(userId, page, size, sortBy, direction, published);
        long totalElements = postRepository.countByUserId(userId, published);
        return PostMapper.toPostsGet200Response(records, totalElements, page, size);
    }

    public PostResponse getPostById(Long postId) {
        PostsRecord record = postRepository.findById(postId);
        if (record == null) {
            throw new RuntimeException("Post not found");
        }
        return PostMapper.toResponse(record);
    }

    @Transactional
    public ApiPostsPostIdPut200Response updatePost(Long id, PostUpdateRequest request, Long userId) {
        if (!postRepository.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Post not found or access denied");
        }
        PostsRecord record = postRepository.findById(id);
        PostsRecord newRecord = PostMapper.updateFromResponse(record, request);
        postRepository.update(newRecord);
        return new ApiPostsPostIdPut200Response().id(newRecord.getId()).title(newRecord.getTitle());
    }

    @Transactional
    public void deletePost(Long id, Long userId) {
        if (!postRepository.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Post not found or access denied");
        }
        postRepository.deleteById(id);
    }
}


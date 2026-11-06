package ru.justspeak.service;

import org.jooq.Condition;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.justspeak.api.dto.ApiPostsGet200Response;
import ru.justspeak.api.dto.ApiPostsPost201Response;
import ru.justspeak.api.dto.ApiPostsPostIdPut200Response;
import ru.justspeak.api.dto.PostCreateRequest;
import ru.justspeak.api.dto.PostResponse;
import ru.justspeak.api.dto.PostUpdateRequest;
import ru.justspeak.exception.ResourceNotFoundException;
import ru.justspeak.jooq.tables.records.PostsRecord;
import ru.justspeak.repository.PostRepository;
import ru.justspeak.util.PostMapper;

import java.time.LocalDateTime;
import java.util.List;

import static ru.justspeak.jooq.tables.Posts.POSTS;

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

    public ApiPostsGet200Response getPosts(int page, int size, String sortBy, String direction, Boolean published) {
        Condition condition = DSL.noCondition();
        if (published != null) {
            condition = condition.and(POSTS.PUBLISHED.eq(published));
        }

        SortField<?> sortField = "asc".equalsIgnoreCase(direction) ? POSTS.CREATED_AT.asc() : POSTS.CREATED_AT.desc();
        if ("views".equalsIgnoreCase(sortBy)) {
            sortField = "asc".equalsIgnoreCase(direction) ? POSTS.VIEWS.asc() : POSTS.VIEWS.desc();
        } else if ("title".equalsIgnoreCase(sortBy)) {
            sortField = "asc".equalsIgnoreCase(direction) ? POSTS.TITLE.asc() : POSTS.TITLE.desc();
        }

        int totalElements = postRepository.count(condition);
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int offset = Math.max(0, page * size);

        List<PostsRecord> records = postRepository.findPage(condition, sortField, size, offset);
        return PostMapper.toPostsGet200Response(records, totalElements, totalPages);
    }

    public ApiPostsGet200Response getPostsByUserId(Long userId) {
        List<PostsRecord> records = postRepository.findAllPostByUserId(userId);
        return PostMapper.toPostsGet200Response(records);
    }

    public PostResponse getPostById(Long postId) {
        PostsRecord record = postRepository.incrementViewsAndGet(postId);
        if (record == null) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        return PostMapper.toResponse(record);
    }

    public ApiPostsPostIdPut200Response updatePost(Long id, PostUpdateRequest request, Long userId) {
        PostsRecord record = postRepository.findById(id);
        if (record == null) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        if (!record.getUserid().equals(userId)) {
            throw new AccessDeniedException("Access denied: you are not the author of this post");
        }
        if (request.getTitle() == null && request.getContent() == null && request.getPublished() == null) {
            return new ApiPostsPostIdPut200Response()
                    .id(id)
                    .title(record.getTitle());
        }
        int updated = postRepository.updateFields(id, request.getTitle(), request.getContent(), request.getPublished());
        if (updated == 0) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        return new ApiPostsPostIdPut200Response()
                .id(id)
                .title(request.getTitle() != null ? request.getTitle() : record.getTitle());
    }

    public void deletePost(Long id, Long userId) {
        PostsRecord record = postRepository.findById(id);
        if (record == null) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        if (!record.getUserid().equals(userId)) {
            throw new AccessDeniedException("Access denied: you are not the author of this post");
        }
        postRepository.deleteById(id);
    }
}

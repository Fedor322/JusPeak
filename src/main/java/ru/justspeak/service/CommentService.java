package ru.justspeak.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.justspeak.api.dto.ApiPostsPostIdCommentsGet200Response;
import ru.justspeak.api.dto.ApiPostsPostIdCommentsPost201Response;
import ru.justspeak.api.dto.CommentCreateRequest;
import ru.justspeak.api.dto.CommentResponse;
import ru.justspeak.model.mongodb.CommentDocument;
import ru.justspeak.repository.CommentRepository;
import ru.justspeak.repository.PostRepository;
import ru.justspeak.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApiPostsPostIdCommentsPost201Response createComment(Long postId, Long userId, CommentCreateRequest request) {
        var userOpt = userRepository.findByUserId(userId);
        String username = userOpt.map(u -> u.getUsername()).orElse("unknown");

        CommentDocument doc = new CommentDocument();
        doc.setPostId(postId);
        doc.setUserId(userId);
        doc.setAuthorUsername(username);
        doc.setContent(request.getContent());
        doc.setParentCommentId(request.getParentCommentId());
        doc.setCreatedAt(LocalDateTime.now());

        CommentDocument saved = commentRepository.save(doc);
        postRepository.incrementCommentCount(postId);

        return new ApiPostsPostIdCommentsPost201Response()
                .id(saved.getId())
                .postId(saved.getPostId())
                .userId(saved.getUserId())
                .createdAt(OffsetDateTime.parse(saved.getCreatedAt().toString()));
    }

    public ApiPostsPostIdCommentsGet200Response getComments(Long postId, int page, int size, String sortBy, String direction) {
        Sort sort = "asc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CommentDocument> result = commentRepository.findByPostId(postId, pageable);

        List<CommentResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new ApiPostsPostIdCommentsGet200Response()
                .content(content)
                .totalElements((int) result.getTotalElements())
                .totalPages(result.getTotalPages());
    }

    private CommentResponse toResponse(CommentDocument doc) {
        return new CommentResponse()
                .id(doc.getId())
                .postId(doc.getPostId())
                .userId(doc.getUserId())
                .authorUsername(doc.getAuthorUsername())
                .content(doc.getContent())
                .createdAt(doc.getCreatedAt() != null ? OffsetDateTime.parse(doc.getCreatedAt().toString()) : null)
                .parentCommentId(doc.getParentCommentId());
    }
}

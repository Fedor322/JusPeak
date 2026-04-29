package ru.justspeak.util;

import ru.justspeak.api.dto.ApiPostsGet200Response;
import ru.justspeak.api.dto.ApiPostsPost201Response;
import ru.justspeak.api.dto.PostCreateRequest;
import ru.justspeak.api.dto.PostResponse;
import ru.justspeak.api.dto.PostUpdateRequest;
import ru.justspeak.jooq.tables.records.PostsRecord;

import java.util.List;

public class PostMapper {

    public static PostsRecord toRecord(PostCreateRequest request) {
        return new PostsRecord()
                .setTitle(request.getTitle())
                .setPublished(request.getPublished())
                .setContent(request.getContent());
    }

    public static PostResponse toResponse(PostsRecord record) {
        return new PostResponse()
                .id(record.getId())
                .userId(record.getUserid())
                .title(record.getTitle())
                .published(record.getPublished())
                .views(record.getViews() != null ? record.getViews().intValue() : 0)
                .commentsCount(record.getCommentCount() != null ? record.getCommentCount().intValue() : 0)
                .createdAt(String.valueOf(record.getCreatedAt()));
    }

    public static ApiPostsPost201Response toPost201Response(PostsRecord record) {
        return new ApiPostsPost201Response()
                .postId(record.getId())
                .title(record.getTitle())
                .userId(record.getUserid());
    }

    public static ApiPostsGet200Response toPostsGet200Response(List<PostsRecord> records, long totalElements, int page, int size) {
        List<PostResponse> content = records.stream().map(PostMapper::toResponse).toList();
        long totalPages = (totalElements + size - 1) / size;

        return new ApiPostsGet200Response()
                .content(content)
                .totalElements((int) totalElements)
                .totalPages((int) totalPages);
    }

    public static PostsRecord updateFromResponse(PostsRecord record, PostUpdateRequest request) {
        if (request.getTitle() != null) {
            record.setTitle(request.getTitle());
        }
        if (request.getPublished() != null) {
            record.setPublished(request.getPublished());
        }
        if (request.getContent() != null) {
            record.setContent(request.getContent());
        }
        return record;
    }
}

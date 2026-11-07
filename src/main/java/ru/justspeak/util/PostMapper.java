package ru.justspeak.util;

import java.util.List;

import ru.justspeak.api.dto.ApiPostsGet200Response;
import ru.justspeak.api.dto.ApiPostsPost201Response;
import ru.justspeak.api.dto.PostCreateRequest;
import ru.justspeak.api.dto.PostResponse;
import ru.justspeak.jooq.tables.records.PostsRecord;

public class PostMapper {

    public static PostsRecord toRecord(PostCreateRequest request) {
        return new PostsRecord()
                .setTitle(request.getTitle())
                .setPublished(request.getPublished())
                .setContent(request.getContent());
    }

    public static PostResponse toResponse(PostsRecord record) {
        if (record == null) {
            return null;
        }
        return new PostResponse()
                .id(record.getId())
                .userId(record.getUserid())
                .title(record.getTitle())
                .content(record.getContent())
                .published(record.getPublished())
                .views(record.getViews() != null ? record.getViews().intValue() : 0)
                .commentsCount(record.getCommentCount() != null ? record.getCommentCount().intValue() : 0)
                .createdAt(record.getCreatedAt() != null ? record.getCreatedAt().toString() : null);
    }

    public static ApiPostsPost201Response toPost201Response(PostsRecord record) {
        return new ApiPostsPost201Response()
                .postId(record.getId())
                .title(record.getTitle())
                .userId(record.getUserid());
    }

    public static ApiPostsGet200Response toPostsGet200Response(List<PostsRecord> records) {
        List<PostResponse> content = records.stream().map(PostMapper::toResponse).toList();

        return new ApiPostsGet200Response()
                .content(content)
                .totalElements(content.size())
                .totalPages(1);
    }

    public static ApiPostsGet200Response toPostsGet200Response(List<PostsRecord> records, int totalElements, int totalPages) {
        List<PostResponse> content = records.stream().map(PostMapper::toResponse).toList();

        return new ApiPostsGet200Response()
                .content(content)
                .totalElements(totalElements)
                .totalPages(totalPages);
    }
}

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
                .userId(record.getUserid())
                .title(record.getTitle())
                .published(record.getPublished())
                .createdAt(String.valueOf(record.getCreatedAt(
                )));
    }

    public static ApiPostsPost201Response toPost201Response(PostsRecord record) {
        return new ApiPostsPost201Response()
                .postId(record.getId())
                .title(record.getTitle())
                .userId(record.getUserid());
    }

    public static ApiPostsGet200Response toPostsGet200Response( List<PostsRecord> records) {
        List<PostResponse> content = records.stream().map(PostMapper::toResponse).toList();


        return new ApiPostsGet200Response()
                .content(content)
                .totalElements(content.size())
                .totalPages(1);
    }


    public static PostsRecord updateFromResponse(PostsRecord record, PostUpdateRequest request) {
        return record
                .setTitle(request.getTitle())
                .setPublished(request.getPublished())
                .setContent(request.getContent());
    }


}

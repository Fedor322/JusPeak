package ru.justspeak.model.mongodb;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = "comments")
public class CommentDocument {

    @Id
    private String id;

    @Field("postId")
    @Indexed
    private Long postId;

    @Field("userId")
    @Indexed
    private Long userId;

    @Field("audioFileUrl")
    private String audioFileUrl;

    @Field("durationSeconds")
    private Integer durationSeconds;

    @Field("fileSizeBytes")
    private Long fileSizeBytes;

    @Field("mimeType")
    private String mimeType;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("listenCount")
    private Integer listenCount = 0;

    @Field("hearts")
    private Integer hearts = 0;

}

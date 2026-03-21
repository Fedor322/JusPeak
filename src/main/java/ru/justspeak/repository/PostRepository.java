package ru.justspeak.repository;


import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.justspeak.config.JooqConfig;
import ru.justspeak.jooq.tables.records.PostsRecord;

import java.util.List;

import static ru.justspeak.jooq.tables.Posts.POSTS;

@Repository
public class PostRepository {

    private final JooqConfig config;
    private final DSLContext context;

    public PostRepository(JooqConfig config, DSLContext context) {
        this.config = config;
        this.context = context;
    }


    public PostsRecord save(PostsRecord record) {
       return context.insertInto(POSTS).set(record).returning().fetchOne();
    }


    public PostsRecord findById(Long id) {
        return context.selectFrom(POSTS).where(POSTS.ID.eq(id)).fetchOne();
    }

    public List<PostsRecord> findAllPostByUserId(Long userId) {
        return context.selectFrom(POSTS).where(POSTS.USERID.eq(userId)).fetch();
    }


    public void update(PostsRecord record) {
       context.update(POSTS)
                .set(record)
                .where(POSTS.ID.eq(record.getId()))
                .execute();

    }











}

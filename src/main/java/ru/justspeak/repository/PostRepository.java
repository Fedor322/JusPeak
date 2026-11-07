package ru.justspeak.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.stereotype.Repository;
import ru.justspeak.jooq.tables.records.PostsRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.justspeak.jooq.tables.Posts.POSTS;

@Repository
public class PostRepository {

    private final DSLContext context;

    public PostRepository(DSLContext context) {
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

    public int updateFields(Long id, String title, String content, Boolean published) {
        Map<Field<?>, Object> values = new HashMap<>();
        if (title != null) {
            values.put(POSTS.TITLE, title);
        }
        if (content != null) {
            values.put(POSTS.CONTENT, content);
        }
        if (published != null) {
            values.put(POSTS.PUBLISHED, published);
        }
        if (values.isEmpty()) {
            return 0;
        }
        return context.update(POSTS)
                .set(values)
                .where(POSTS.ID.eq(id))
                .execute();
    }

    public void deleteById(Long id) {
        context.deleteFrom(POSTS).where(POSTS.ID.eq(id)).execute();
    }

    public PostsRecord incrementViewsAndGet(Long id) {
        return context.update(POSTS)
                .set(POSTS.VIEWS, POSTS.VIEWS.plus(1))
                .where(POSTS.ID.eq(id))
                .returning()
                .fetchOne();
    }

    public int count(Condition condition) {
        return context.fetchCount(POSTS, condition);
    }

    public List<PostsRecord> findPage(Condition condition, SortField<?> sortField, int limit, int offset) {
        return context.selectFrom(POSTS)
                .where(condition)
                .orderBy(sortField)
                .limit(limit)
                .offset(offset)
                .fetch();
    }
}

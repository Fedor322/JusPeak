package ru.justspeak.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.justspeak.jooq.tables.records.PostsRecord;

import java.util.List;

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

    public boolean existsByIdAndUserId(Long postId, Long userId) {
        return context.fetchExists(
                context.selectFrom(POSTS)
                        .where(POSTS.ID.eq(postId).and(POSTS.USERID.eq(userId)))
        );
    }

    public List<PostsRecord> findAll(int page, int size, String sortBy, String direction, Boolean published) {
        Condition condition = DSL.noCondition();
        if (published != null) {
            condition = condition.and(POSTS.PUBLISHED.eq(published));
        }

        return context.selectFrom(POSTS)
                .where(condition)
                .orderBy(resolveSortField(sortBy, direction))
                .limit(size)
                .offset(page * size)
                .fetch();
    }

    public long count(Boolean published) {
        Condition condition = DSL.noCondition();
        if (published != null) {
            condition = condition.and(POSTS.PUBLISHED.eq(published));
        }
        Integer count = context.selectCount().from(POSTS).where(condition).fetchOne(0, int.class);
        return count != null ? count.longValue() : 0L;
    }

    public List<PostsRecord> findAllByUserId(Long userId, int page, int size, String sortBy, String direction, Boolean published) {
        Condition condition = POSTS.USERID.eq(userId);
        if (published != null) {
            condition = condition.and(POSTS.PUBLISHED.eq(published));
        }

        return context.selectFrom(POSTS)
                .where(condition)
                .orderBy(resolveSortField(sortBy, direction))
                .limit(size)
                .offset(page * size)
                .fetch();
    }

    private SortField<?> resolveSortField(String sortBy, String direction) {
        boolean isAsc = "asc".equalsIgnoreCase(direction);
        if ("title".equals(sortBy)) {
            return isAsc ? POSTS.TITLE.asc() : POSTS.TITLE.desc();
        } else if ("views".equals(sortBy)) {
            return isAsc ? POSTS.VIEWS.asc() : POSTS.VIEWS.desc();
        } else if ("commentCount".equals(sortBy)) {
            return isAsc ? POSTS.COMMENT_COUNT.asc() : POSTS.COMMENT_COUNT.desc();
        } else if ("createdAt".equals(sortBy)) {
            return isAsc ? POSTS.CREATED_AT.asc() : POSTS.CREATED_AT.desc();
        } else {
            return isAsc ? POSTS.CREATED_AT.asc() : POSTS.CREATED_AT.desc();
        }
    }

    public long countByUserId(Long userId, Boolean published) {
        Condition condition = POSTS.USERID.eq(userId);
        if (published != null) {
            condition = condition.and(POSTS.PUBLISHED.eq(published));
        }
        Integer count = context.selectCount().from(POSTS).where(condition).fetchOne(0, int.class);
        return count != null ? count.longValue() : 0L;
    }

    public void update(PostsRecord record) {
        context.update(POSTS)
                .set(record)
                .where(POSTS.ID.eq(record.getId()))
                .execute();
    }

    public void deleteById(Long id) {
        context.deleteFrom(POSTS).where(POSTS.ID.eq(id)).execute();
    }

    public void incrementCommentCount(Long postId) {
        context.update(POSTS)
                .set(POSTS.COMMENT_COUNT, POSTS.COMMENT_COUNT.plus(1))
                .where(POSTS.ID.eq(postId))
                .execute();
    }
}
















package ru.justspeak.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.justspeak.jooq.tables.records.UsersRecord;

import java.util.List;
import java.util.Optional;

import static ru.justspeak.jooq.tables.Users.USERS;

@Repository
public class UserRepository {

    private final DSLContext dsl;

    public UserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public UsersRecord save(UsersRecord record) {
        return dsl.insertInto(USERS)
                .set(record)
                .returning()
                .fetchOne();
    }

    public Optional<UsersRecord> findByUsername(String username) {
        return dsl.selectFrom(USERS).where(USERS.USERNAME.eq(username)).fetchOptional();
    }

    public Optional<UsersRecord> findByEmail(String email) {
        return dsl.selectFrom(USERS).where(USERS.EMAIL.eq(email)).fetchOptional();
    }

    public Optional<UsersRecord> findByUserId(Long userId) {
        return dsl.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOptional();
    }

    public List<UsersRecord> findAll() {
        return dsl.selectFrom(USERS).fetch();
    }

    public void deleteById(UsersRecord record) {
        dsl.deleteFrom(USERS).where(USERS.ID.eq(record.getId())).execute();
    }
}

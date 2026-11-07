package ru.justspeak.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.justspeak.api.dto.UserRegisterRequest;
import ru.justspeak.jooq.tables.records.ProfilesRecord;
import ru.justspeak.jooq.tables.records.UsersRecord;

import static ru.justspeak.jooq.Tables.PROFILES;

@Repository
public class ProfileRepository {

    private final DSLContext dsl;

    public ProfileRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public ProfilesRecord create(UsersRecord record, UserRegisterRequest request) {
        return dsl.insertInto(PROFILES)
                .set(PROFILES.USER_ID, record.getId())
                .set(PROFILES.FIRST_NAME, request.getFirstName())
                .set(PROFILES.LAST_NAME, request.getLastName())
                .returning()
                .fetchOne();
    }

    public ProfilesRecord findById(Long id) {
        return dsl.selectFrom(PROFILES)
                .where(PROFILES.ID.eq(id))
                .fetchOne();
    }

    public ProfilesRecord findByUserId(Long userId) {
        return dsl.selectFrom(PROFILES)
                .where(PROFILES.USER_ID.eq(userId))
                .fetchOne();
    }

    public int update(ProfilesRecord record, Long userId) {
        if (!record.changed()) {
            return 0;
        }
        return dsl.update(PROFILES)
                .set(record)
                .where(PROFILES.ID.eq(record.getId()).and(PROFILES.USER_ID.eq(userId)))
                .execute();
    }
}

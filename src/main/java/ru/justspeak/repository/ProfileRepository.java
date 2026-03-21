package ru.justspeak.repository;


import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.justspeak.api.dto.UserRegisterRequest;
import ru.justspeak.config.JooqConfig;
import ru.justspeak.jooq.tables.records.ProfilesRecord;
import ru.justspeak.jooq.tables.records.UsersRecord;

import static ru.justspeak.jooq.Tables.PROFILES;

@Repository
public class ProfileRepository {

    private final JooqConfig jooqConfig;
    private final DSLContext dsl;


    public ProfileRepository(JooqConfig jooqConfig, DSLContext dsl) {
        this.jooqConfig = jooqConfig;
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

    public ProfilesRecord findByUserId(Long id) {
        return dsl.selectFrom(PROFILES).where(PROFILES.USER_ID.eq(id)).fetchOne();
    }



    public void update(ProfilesRecord record) {
        dsl.update(PROFILES).set(record).where(PROFILES.ID.eq(record.getId())).execute();
    }


}

package ru.justspeak.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.justspeak.api.dto.UserRegisterRequest;
import ru.justspeak.api.dto.UserResponse;
import ru.justspeak.jooq.tables.records.UsersRecord;

public class UserMapper {

    public static UserResponse toUserResponse(UsersRecord user) {
        return new UserResponse()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail());
    }

    public static UsersRecord toRecord(UserRegisterRequest request, PasswordEncoder encoder) {
        UsersRecord record = new UsersRecord();
        record.setUsername(request.getUsername());
        record.setEmail(request.getEmail());
        record.setPasswordHash(encoder.encode(request.getPassword()));
        return record;
    }




}

package ru.justspeak.service;

import org.springframework.stereotype.Service;
import ru.justspeak.api.dto.UserResponse;
import ru.justspeak.jooq.tables.records.UsersRecord;
import ru.justspeak.repository.UserRepository;
import ru.justspeak.util.UserMapper;

import java.util.Optional;



@Service
public class UserService {



    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUserById(Long id) {
       UsersRecord record = userRepository.findByUserId(id)
               .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserResponse(record);
    }

    public UserResponse getUserByUsername(String username) {
        UsersRecord record = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserResponse(record);
    }




}

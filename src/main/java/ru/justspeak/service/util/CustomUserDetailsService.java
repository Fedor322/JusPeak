package ru.justspeak.service.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.justspeak.jooq.tables.records.UsersRecord;
import ru.justspeak.model.CustomUserDetails;
import ru.justspeak.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersRecord record = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username + "username not found"));
        return new CustomUserDetails(record);

    }
}

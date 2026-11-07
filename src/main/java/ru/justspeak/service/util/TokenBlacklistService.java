package ru.justspeak.service.util;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, Date expiration) {
        if (token == null || token.isBlank()) {
            return;
        }
        Instant expiresAt = expiration != null ? expiration.toInstant() : Instant.now().plusSeconds(86400);
        blacklist.put(token, expiresAt);
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Instant expiresAt = blacklist.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (Instant.now().isAfter(expiresAt)) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}


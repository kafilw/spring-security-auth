package com.springsecurity.securitydemo.jwtutil;

// import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

@Component
public class TokenBlacklist {

    // Use a concurrent set to store blacklisted tokens
    private final ConcurrentMap<String, Boolean> blacklist = new ConcurrentHashMap<>();

    public void addToBlacklist(String token) {
        blacklist.put(token, true);
        System.out.println(token + " added to blacklist");
        System.out.println(blacklist);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}

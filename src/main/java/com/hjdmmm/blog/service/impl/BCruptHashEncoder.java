package com.hjdmmm.blog.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hjdmmm.blog.service.HashEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class BCruptHashEncoder implements HashEncoder {

    private static final int COST = BCrypt.MIN_COST;

    @Override
    public String encode(String text) {
        return BCrypt.withDefaults().hashToString(COST, text.toCharArray());
    }

    @Override
    public boolean verify(String input, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(input.getBytes(StandardCharsets.UTF_8), hash.getBytes(StandardCharsets.UTF_8));
        return result.verified;
    }
}

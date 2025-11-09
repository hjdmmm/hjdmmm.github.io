package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.LoginConfig;
import com.hjdmmm.blog.service.TokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class TokenHandlerImpl implements TokenHandler {

    private static final String ENCRYPT_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private final Key key;

    private final LoginConfig loginConfig;

    public TokenHandlerImpl(LoginConfig loginConfig) throws Exception {
        this.loginConfig = loginConfig;
        key = getKey();
    }

    private static Key getKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPT_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(Instant.now().toEpochMilli());
        keyGenerator.init(128, random);
        return keyGenerator.generateKey();
    }

    private static String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeBase64(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    @Override
    public String createToken(String userId, HttpHeaders httpHeaders) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        String userAgent = httpHeaders.getFirst(HttpHeaders.USER_AGENT);
        if (userAgent == null) {
            return null;
        }

        Instant now = Instant.now();

        String token = encodeBase64(userId) + "." + encodeBase64(userAgent) + "." + now.getEpochSecond();
        byte[] tokenBytes = token.getBytes();
        byte[] encryptedToken = cipher.doFinal(tokenBytes);

        // 加密后会有非ASCII可打印字符，因此还要进行一次Base64编码
        return Base64.getEncoder().encodeToString(encryptedToken);
    }

    @Override
    public Long parseToken(String encryptedToken, HttpServletRequest request) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decryptedTokenBytes;
        try {
            byte[] decode = Base64.getDecoder().decode(encryptedToken);
            decryptedTokenBytes = cipher.doFinal(decode);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }

        String token = new String(decryptedTokenBytes);
        String[] split = token.split("\\.");
        if (split.length != 3) {
            return null;
        }

        Instant loginInstant;
        try {
            loginInstant = Instant.ofEpochSecond(Long.parseLong(split[2]));
        } catch (Exception e) {
            return null;
        }

        if (loginInstant.plus(loginConfig.getExpireSeconds()).isBefore(Instant.now())) {
            return null;
        }

        String userAgent = decodeBase64(split[1]);
        if (!userAgent.equals(request.getHeader(HttpHeaders.USER_AGENT))) {
            return null;
        }

        long userId;
        try {
            userId = Long.parseLong(decodeBase64(split[0]));
        } catch (Exception e) {
            return null;
        }

        return userId;
    }
}

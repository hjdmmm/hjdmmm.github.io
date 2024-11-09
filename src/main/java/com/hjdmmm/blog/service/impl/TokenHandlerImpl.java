package com.hjdmmm.blog.service.impl;

import com.hjdmmm.blog.config.LoginConfig;
import com.hjdmmm.blog.enums.ServiceCodeEnum;
import com.hjdmmm.blog.enums.UserOpCodeEnum;
import com.hjdmmm.blog.exception.BadTokenException;
import com.hjdmmm.blog.exception.ServiceException;
import com.hjdmmm.blog.exception.UserOpException;
import com.hjdmmm.blog.service.TokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.OptionalLong;

@Service
public class TokenHandlerImpl implements TokenHandler {

    private static final String ENCRYPT_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final Key key = getKey();

    private final LoginConfig loginConfig;

    public TokenHandlerImpl(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    private static Key getKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPT_ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(Instant.now().toEpochMilli());
            keyGenerator.init(128, random);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ServiceCodeEnum.TOKEN_ERROR, String.format("%s 算法不存在", ENCRYPT_ALGORITHM), e);
        }
    }

    private static String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeBase64(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }

    @Override
    public String createToken(String userId, HttpHeaders httpHeaders) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new ServiceException(ServiceCodeEnum.TOKEN_ERROR, String.format("对userId= %s ，生成Cipher时出错", userId), e);
        }

        String userAgent = httpHeaders.getFirst(HttpHeaders.USER_AGENT);
        if (userAgent == null) {
            throw new UserOpException(UserOpCodeEnum.LOGIN_ERROR, "请在正常的浏览器中打开");
        }

        Instant now = Instant.now();

        String token = encodeBase64(userId) + "." + encodeBase64(userAgent) + "." + now.getEpochSecond();
        byte[] tokenBytes = token.getBytes();
        byte[] encryptedToken;
        try {
            encryptedToken = cipher.doFinal(tokenBytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new ServiceException(ServiceCodeEnum.TOKEN_ERROR, String.format("对userId= %s ，生成密钥时出错", userId), e);
        }

        // 加密后会有非ASCII可打印字符，因此还要进行一次Base64编码
        return Base64.getEncoder().encodeToString(encryptedToken);
    }

    @Override
    public OptionalLong parseToken(String encryptedToken, HttpServletRequest request) throws BadTokenException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new ServiceException(ServiceCodeEnum.TOKEN_ERROR, "生成Cipher时出错", e);
        }

        byte[] decryptedTokenBytes;
        try {
            byte[] decode = Base64.getDecoder().decode(encryptedToken);
            decryptedTokenBytes = cipher.doFinal(decode);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new BadTokenException(String.format("Token：%s无法解密", encryptedToken), e);
        }

        String token = new String(decryptedTokenBytes);
        String[] split = token.split("\\.");
        if (split.length != 3) {
            throw new BadTokenException(String.format("解密后的Token：%s不符合三段的规则", token));
        }

        Instant loginInstant;
        try {
            loginInstant = Instant.ofEpochSecond(Long.parseLong(split[2]));
        } catch (Exception e) {
            throw new BadTokenException(String.format("解密后的Token：%s时间戳格式错误", token));
        }

        if (loginInstant.plus(loginConfig.getExpireSeconds()).isBefore(Instant.now())) {
            return OptionalLong.empty();
        }

        String userAgent = decodeBase64(split[1]);
        if (!userAgent.equals(request.getHeader(HttpHeaders.USER_AGENT))) {
            throw new BadTokenException(String.format("Token中的user-agent为%s，当前user-agent为%s，不匹配", userAgent, request.getHeader(HttpHeaders.USER_AGENT)));
        }

        long userId;
        try {
            userId = Long.parseLong(decodeBase64(split[0]));
        } catch (NumberFormatException e) {
            throw new BadTokenException(String.format("解密后的Token：%s用户ID格式错误", token));
        }

        return OptionalLong.of(userId);
    }
}

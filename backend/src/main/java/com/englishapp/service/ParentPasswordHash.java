package com.englishapp.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** 每次设置使用新盐，数据库不保存明文密码。 */
final class ParentPasswordHash {
    private static final int ITERATIONS = 120000;

    static String encode(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(derive(password, salt));
    }

    static boolean matches(String password, String encoded) {
        if (password == null || !password.matches("[0-9]{6}")) return false;
        String[] parts = encoded.split(":");
        return MessageDigest.isEqual(derive(password, Base64.getDecoder().decode(parts[0])),
                Base64.getDecoder().decode(parts[1]));
    }

    private static byte[] derive(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("无法生成家长密码摘要", e);
        } finally { spec.clearPassword(); }
    }
}

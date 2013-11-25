package com.mossle.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.crypto.util.EncodingUtils.concatenate;
import static org.springframework.security.crypto.util.EncodingUtils.subArray;

public class MessageDigestPasswordEncoder implements PasswordEncoder {
    private final MessageDigest messageDigest;
    private final byte[] secret;
    private final BytesKeyGenerator saltGenerator;

    public MessageDigestPasswordEncoder() {
        this("");
    }

    public MessageDigestPasswordEncoder(CharSequence secret) {
        this("SHA-256", secret);
    }

    public MessageDigestPasswordEncoder(String algorithm, CharSequence secret) {
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No such hashing algorithm", e);
        }

        this.secret = Utf8.encode(secret);
        this.saltGenerator = KeyGenerators.secureRandom();
    }

    public String encode(CharSequence rawPassword) {
        return encode(rawPassword, saltGenerator.generateKey());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = decode(encodedPassword);
        byte[] salt = subArray(digested, 0, saltGenerator.getKeyLength());

        return matches(digested, digest(rawPassword, salt));
    }

    private String encode(CharSequence rawPassword, byte[] salt) {
        byte[] digest = digest(rawPassword, salt);

        return new String(Hex.encode(digest));
    }

    private byte[] digest(CharSequence rawPassword, byte[] salt) {
        byte[] digest = messageDigest.digest(concatenate(salt, secret,
                Utf8.encode(rawPassword)));

        return concatenate(salt, digest);
    }

    private byte[] decode(CharSequence encodedPassword) {
        return Hex.decode(encodedPassword);
    }

    private boolean matches(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < expected.length; i++) {
            result |= (expected[i] ^ actual[i]);
        }

        return result == 0;
    }
}

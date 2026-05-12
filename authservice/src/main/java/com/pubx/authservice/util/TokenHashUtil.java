package com.pubx.authservice.util;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

    public class TokenHashUtil {

        /*
         * Why hash the refresh token?
         * --------
         * We store refresh tokens in the database.
         * If a hacker steals the database, they get all tokens.
         * With plain tokens → hacker can login as anyone.
         * With hashed tokens → hashes are useless, can't reverse them.
         *
         * Flow:
         *   User gets:  "abc123xyz" (actual token)
         *   DB stores:  sha256("abc123xyz") = "a8f5f167f44..." (hash)
         *   User sends: "abc123xyz"
         *   We compute: sha256("abc123xyz") = "a8f5f167f44..."
         *   We search:  findByTokenHash("a8f5f167f44...") → found!
         */

        public static String hashToken(String token) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(hashBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not available", e);
            }
        }
    }


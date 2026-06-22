package fh.swen.swen2tourplanner.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {

    private final PasswordHash passwordHash = new PasswordHash();

    @Test
    void hash_and_verify_correctPassword_returnsTrue() {
        String hashed = passwordHash.hash("mypassword");

        assertTrue(passwordHash.verify("mypassword", hashed));
    }

    @Test
    void verify_wrongPassword_returnsFalse() {
        String hashed = passwordHash.hash("mypassword");

        assertFalse(passwordHash.verify("wrongpassword", hashed));
    }

    @Test
    void hash_producesUniqueSaltedHashes() {
        String hash1 = passwordHash.hash("samepassword");
        String hash2 = passwordHash.hash("samepassword");

        assertNotEquals(hash1, hash2);
    }
}

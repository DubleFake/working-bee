package com.homework.task.web.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordManager {

    /**
     * Hashes the given password using a salt and the SHA-256 algorithm.
     * This method generates a salt, hashes the password with the salt, and then returns a string containing
     * the salt and the hashed password. The salt is encoded as a hexadecimal string, and the hashed password is
     * Base64-encoded.
     *
     * @param password - The password to hash.
     * @throws NoSuchAlgorithmException - If the hashing algorithm (SHA-256) or the salt generation algorithm (SHA1PRNG) is not available.
     * @return String - A string containing the salt (hex) and the hashed password (Base64-encoded), separated by a colon.
     */

    public static String hashPassword(String password) throws NoSuchAlgorithmException {

        byte[] salt = generateSalt();
        byte[] hashedPassword = hash(password, salt);

        String saltAsString = bytesToHex(salt);
        String hashedPasswordAsString = Base64.getEncoder().encodeToString(hashedPassword);

        return saltAsString + ":" + hashedPasswordAsString;
    }

    /**
     * Generates a random salt using the SHA1PRNG algorithm.
     * This method generates a 16-byte salt that is used in password hashing to add randomness and increase security.
     *
     * @throws NoSuchAlgorithmException - If the SHA1PRNG algorithm is not available.
     * @return byte[]- A 16-byte salt.
     */

    private static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];  // 16 bytes salt
        sr.nextBytes(salt);
        return salt;
    }

    /**
     * Hashes the given password with the provided salt using the SHA-256 algorithm.
     * This method uses SHA-256 to hash the password, incorporating the provided salt into the hashing process.
     *
     * @param password - The password to hash.
     * @param salt - The salt to use in the hashing process.
     * @throws NoSuchAlgorithmException - If the hashing algorithm (SHA-256) is not available.
     * @return byte[] - A byte array containing the hashed password.
     */

    private static byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(password.getBytes());
    }

    /**
     * Converts a byte array into a hexadecimal string.
     * This method takes a byte array and converts each byte into its corresponding two-character hexadecimal
     * representation, resulting in a string of hexadecimal characters.
     *
     * @param bytes - The byte array to convert.
     * @return String - A string representing the byte array in hexadecimal format.
     */

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Converts a hexadecimal string back into a byte array.
     * This method takes a string containing hexadecimal characters and converts it into a byte array,
     * effectively reversing the conversion done by `bytesToHex`.
     *
     * @param hex - The hexadecimal string to convert.
     * @return byte[] - A byte array representing the hexadecimal string.
     */

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Verifies if the given password matches the stored hashed password.
     * This method splits the stored hash into the salt and hashed password components. It then hashes the provided
     * password using the same salt and compares it with the stored hashed password to verify if they match.
     *
     * @param password - The password to verify.
     * @param storedHash - The stored hash containing the salt and hashed password (salt:hashedPassword).
     * @throws NoSuchAlgorithmException - If the hashing algorithm (SHA-256) or the salt generation algorithm (SHA1PRNG) is not available.
     * @return boolean - True if the provided password matches the stored hashed password, false otherwise.
     */

    public static boolean verifyPassword(String password, String storedHash) throws NoSuchAlgorithmException {
        String[] parts = storedHash.split(":");
        byte[] salt = hexToBytes(parts[0]);
        byte[] storedHashedPassword = Base64.getDecoder().decode(parts[1]);

        byte[] hashedPassword = hash(password, salt);

        return MessageDigest.isEqual(hashedPassword, storedHashedPassword);
    }

}

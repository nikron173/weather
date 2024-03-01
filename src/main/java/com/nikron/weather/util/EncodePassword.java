package com.nikron.weather.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class EncodePassword {
    public static String encodePassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static boolean verifyPassword(String password, String hashPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashPassword.toCharArray()).verified;
    }
}

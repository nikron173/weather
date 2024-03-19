package com.nikron.weather.util;

import java.util.Objects;
import java.util.regex.Pattern;

public class CheckParameter {

    private static final String regxPasswd = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
    private static final String regexEmail = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    private static final Pattern patternPasswd = Pattern.compile(regxPasswd, Pattern.CASE_INSENSITIVE);
    private static final Pattern patternEmail = Pattern.compile(regexEmail);

    public static boolean checkPassword(String passwd) {
        if (Objects.isNull(passwd)) return false;
        return patternPasswd.matcher(passwd).matches();
    }

    public static boolean checkLogin(String login) {
        return !Objects.isNull(login) && !login.isBlank() && login.length() >= 3;
    }

    public static boolean checkEmail(String email) {
        if (Objects.isNull(email)) return false;
        return patternEmail.matcher(email).matches();
    }

    public static boolean checkNameCity(String city) {
        return !Objects.isNull(city) && !city.isBlank() && !city.matches(".*[0-9,;!.><?&@#$%^*()/ ].*");
    }

    public static boolean checkLongId(String id) {
        if (Objects.isNull(id)) return false;
        try {
            return Long.parseLong(id) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

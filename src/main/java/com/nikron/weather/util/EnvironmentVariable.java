package com.nikron.weather.util;


import java.util.Objects;
public class EnvironmentVariable {
    public static final String APP_ID;
    public static final String PASSWORD_DB;
    public static final String USER_DB;
    public static final String URL_DB;

    static  {
        APP_ID = System.getenv().get("APP_ID");
        PASSWORD_DB = System.getenv().get("PASSWORD_DB");
        USER_DB = System.getenv().get("USER_DB");
        URL_DB = System.getenv().get("URL_DB");
        if (Objects.isNull(APP_ID) || Objects.isNull(PASSWORD_DB)
                || Objects.isNull(URL_DB) || Objects.isNull(USER_DB))
            throw new RuntimeException("Environment variables need to be set: APP_ID, USER_DB, PASSWORD_DB, URL_DB");
    }

}

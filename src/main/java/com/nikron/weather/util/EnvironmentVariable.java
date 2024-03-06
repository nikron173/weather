package com.nikron.weather.util;


import java.util.Objects;
public class EnvironmentVariable {
    public static final String APP_ID;
    public static final String PASSWORD_DB;
    public static final String USER_DB;
    public static final String URL_DB;

    static  {
        APP_ID = Objects.isNull(System.getenv().get("APP_ID")) ?
                "fa45522effc9e7966ecd44f894672fb4" : System.getenv().get("APP_ID");
        PASSWORD_DB = Objects.isNull(System.getenv().get("PASSWORD_DB")) ?
                "postgres" : System.getenv().get("PASSWORD_DB");
        USER_DB = Objects.isNull(System.getenv().get("USER_DB")) ?
                "postgres" : System.getenv().get("USER_DB");
        URL_DB = Objects.isNull(System.getenv().get("URL_DB")) ?
                "jdbc:postgresql://localhost:5432/weatherdb" : System.getenv().get("URL_DB");
    }

}

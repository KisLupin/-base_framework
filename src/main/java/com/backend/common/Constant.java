package com.backend.common;

public class Constant {
    public static final String TOKEN_PREFIX = "MY_APP";
    public static final class TokenAudience {
        public static final String WEB = "WEB";
        public static final String API = "API";
    }

    public static final class DateTimeFormat {
        public static final String YYYY_MM_DD = "yyyy-MM-dd";
        public static final String DD_MM_YYYY = "dd/MM/yyyy";
        public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";
        public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
        public static final String DD_MM_YYYY_MYSQL_FORMAT = "%d/%m/%Y";
        public static final String YYYY_MM_DD_MYSQL_FORMAT = "%Y/%m/%d";
    }
}

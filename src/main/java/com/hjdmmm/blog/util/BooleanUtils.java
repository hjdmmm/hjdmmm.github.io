package com.hjdmmm.blog.util;

public abstract class BooleanUtils {
    private BooleanUtils() {
        throw new AssertionError();
    }

    public static int toInt(Boolean bool) {
        if (bool == null || !bool) {
            return 0;
        }

        return 1;
    }

    public static boolean toBoolean(Integer value) {
        return value != null && value != 0;
    }
}

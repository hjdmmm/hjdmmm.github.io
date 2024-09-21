package com.hjdmmm.blog.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class CollectionUtils {
    private CollectionUtils() {
        throw new AssertionError();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        if (isEmpty(list)) {
            return Collections.emptyList();
        }

        return list;
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        if (isEmpty(map)) {
            return Collections.emptyMap();
        }

        return map;
    }
}

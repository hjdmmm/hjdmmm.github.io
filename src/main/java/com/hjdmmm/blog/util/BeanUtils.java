package com.hjdmmm.blog.util;

import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BeanUtils {
    private BeanUtils() {
        throw new AssertionError();
    }

    public static <S, T> T copyBean(S source, Class<T> targetClass) {
        T target = org.springframework.beans.BeanUtils.instantiateClass(targetClass);
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <S, T> List<T> copyBeanList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream().map(s -> copyBean(s, targetClass)).collect(Collectors.toList());
    }

    public static <E> Map<String, Object> toMap(E object) {
        Map<String, Object> map = new HashMap<>();

        ReflectionUtils.doWithFields(object.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = ReflectionUtils.getField(field, object);
            map.put(field.getName(), fieldValue);
        });

        return map;
    }
}

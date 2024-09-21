package com.hjdmmm.blog.context.impl;

import com.hjdmmm.blog.context.UserIdHolder;
import org.springframework.stereotype.Component;

@Component
public final class UserIdHolderImpl implements UserIdHolder {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    @Override
    public long get() {
        return threadLocal.get();
    }

    @Override
    public boolean exist() {
        return threadLocal.get() != null;
    }

    @Override
    public void set(long userId) {
        threadLocal.set(userId);
    }

    @Override
    public void clear() {
        threadLocal.remove();
    }
}

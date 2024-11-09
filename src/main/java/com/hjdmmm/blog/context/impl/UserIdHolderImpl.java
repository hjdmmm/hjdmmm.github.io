package com.hjdmmm.blog.context.impl;

import com.hjdmmm.blog.context.UserIdHolder;
import org.springframework.stereotype.Component;

@Component
public final class UserIdHolderImpl implements UserIdHolder {
    private static final ThreadLocal<SessionImpl> threadLocal = new ThreadLocal<>();

    @Override
    public Long get() {
        if (!exist()) {
            return null;
        }

        return threadLocal.get().userId;
    }

    @Override
    public boolean exist() {
        return threadLocal.get() != null;
    }

    @Override
    public Session newSession(long userId) {
        return new SessionImpl(userId);
    }

    private record SessionImpl(long userId) implements Session {
        private SessionImpl(long userId) {
            this.userId = userId;
            threadLocal.set(this);
        }

        @Override
        public void close() {
            threadLocal.remove();
        }
    }
}

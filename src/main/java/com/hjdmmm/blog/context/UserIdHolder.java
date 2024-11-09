package com.hjdmmm.blog.context;

public interface UserIdHolder {
    Long get();

    boolean exist();

    Session newSession(long userId);

    interface Session extends AutoCloseable {
        @Override
        void close();
    }
}

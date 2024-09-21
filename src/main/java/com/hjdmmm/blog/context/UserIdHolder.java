package com.hjdmmm.blog.context;

public interface UserIdHolder {

    long get();

    boolean exist();

    void set(long userId);

    void clear();
}

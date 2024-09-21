package com.hjdmmm.blog.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hjdmmm.blog.context.UserIdHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {
    private final UserIdHolder userIdHolder;

    public MybatisPlusMetaObjectHandler(UserIdHolder userIdHolder) {
        this.userIdHolder = userIdHolder;
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = userIdHolder.get();
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = userIdHolder.get();
        LocalDateTime now = LocalDateTime.now();
        this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
    }
}

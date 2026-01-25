package com.yuntan.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 填充创建时间
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        
        // 填充更新时间
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);

        // 可以填充更多字段，如状态字段等
        this.setFieldValByName("status", 1, metaObject);  // 默认状态为 1（正常）

        // 填充逻辑删除字段
        this.setFieldValByName("deleted", 0, metaObject);  // 默认
    }

    /**
     * 更新时自动填充字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 填充更新时间
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}

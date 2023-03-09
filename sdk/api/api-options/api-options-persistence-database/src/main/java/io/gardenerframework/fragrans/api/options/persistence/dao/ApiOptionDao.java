package io.gardenerframework.fragrans.api.options.persistence.dao;

import io.gardenerframework.fragrans.api.options.persistence.schema.ApiOptionDatabaseRecord;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/5/10 6:29 上午
 */
public interface ApiOptionDao {
    /**
     * 创建api选项记录
     *
     * @param record 记录
     */
    void createApiOption(ApiOptionDatabaseRecord record);

    /**
     * 读取选项
     *
     * @param id id
     * @return 结果
     */
    @Nullable
    ApiOptionDatabaseRecord readApiOption(String id);

    /**
     * 更新选项
     *
     * @param id     id
     * @param record 新记录
     */
    void updateApiOption(String id, ApiOptionDatabaseRecord record);
}

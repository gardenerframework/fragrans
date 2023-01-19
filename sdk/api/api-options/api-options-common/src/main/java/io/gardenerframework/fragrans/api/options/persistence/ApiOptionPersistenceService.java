package io.gardenerframework.fragrans.api.options.persistence;

import io.gardenerframework.fragrans.api.options.persistence.exception.ApiOptionPersistenceException;
import io.gardenerframework.fragrans.api.options.persistence.schema.ApiOptionRecordSkeleton;
import org.springframework.lang.Nullable;

/**
 * 选项持久化服务
 *
 * @author zhanghan30
 * @date 2022/5/10 2:42 上午
 */
public interface ApiOptionPersistenceService<R extends ApiOptionRecordSkeleton> {
    /**
     * 读取选项
     *
     * @param id 选项id
     * @return 选项
     * @throws ApiOptionPersistenceException 持久化问题
     */
    @Nullable
    R readOption(String id) throws ApiOptionPersistenceException;

    /**
     * 保存选项
     *
     * @param id     id
     * @param option 选项
     * @return 保存后使用的版本号
     * @throws ApiOptionPersistenceException 持久化问题
     */
    String saveOption(String id, Object option) throws ApiOptionPersistenceException;
}

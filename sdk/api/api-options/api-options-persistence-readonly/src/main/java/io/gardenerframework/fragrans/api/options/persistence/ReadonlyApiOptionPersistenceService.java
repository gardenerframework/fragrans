package io.gardenerframework.fragrans.api.options.persistence;

import io.gardenerframework.fragrans.api.options.persistence.exception.ApiOptionPersistenceException;
import io.gardenerframework.fragrans.api.options.persistence.schema.ApiOptionRecordSkeleton;
import io.gardenerframework.fragrans.api.standard.error.exception.server.NotImplementedException;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 将所有选项其实变成只读
 *
 * @author zhanghan30
 * @date 2022/9/23 15:42
 */
@AllArgsConstructor
public class ReadonlyApiOptionPersistenceService implements ApiOptionPersistenceService<ApiOptionRecordSkeleton> {
    @Nullable
    @Override
    public ApiOptionRecordSkeleton readOption(String id) throws ApiOptionPersistenceException {
        return null;
    }

    @Override
    public String saveOption(String id, Object option) throws ApiOptionPersistenceException {
        throw new NotImplementedException("all options is force readonly by this service");
    }
}

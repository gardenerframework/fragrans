package io.gardenerframework.fragrans.api.options.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import io.gardenerframework.fragrans.api.options.persistence.dao.ApiOptionDao;
import io.gardenerframework.fragrans.api.options.persistence.exception.ApiOptionVersionOutOfDateException;
import io.gardenerframework.fragrans.api.options.persistence.schema.ApiOptionDatabaseRecord;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionsRegistry;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/5/10 2:49 上午
 */
@AllArgsConstructor
public class DatabaseApiOptionPersistenceService implements ApiOptionPersistenceService<ApiOptionDatabaseRecord> {
    private final ApiOptionsRegistry registry;
    private final ApiOptionDao dao;
    private final ObjectMapper mapper;

    @Nullable
    @Override
    public ApiOptionDatabaseRecord readOption(String id) {
        return dao.readApiOption(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveOption(String id, Object option) {
        ApiOptionDatabaseRecord record = readOption(id);
        if (record == null) {
            return doSaveOption(id, option);
        }
        //从注册表拿出当前的选项的版本号
        String currentVersion = Objects.requireNonNull(registry.getItem(id)).getVersionNumber();
        String versionFromDatabase = record.getVersionNumber();
        //版本不同，禁止更新
        if (!Objects.equals(currentVersion, versionFromDatabase)) {
            throw new ApiOptionVersionOutOfDateException(id, currentVersion);
        }
        String newVersion = generateVersionNumber();
        record.setId(id);
        record.setOption(mapper.convertValue(option, new TypeReference<Map<String, Object>>() {
        }));
        record.setVersionNumber(newVersion);
        dao.updateApiOption(
                id,
                record
        );
        return newVersion;
    }

    private String generateVersionNumber() {
        return new SimpleDateFormat(StdDateFormat.DATE_FORMAT_STR_ISO8601).format(new Date());
    }

    private String doSaveOption(String id, Object option) {
        ApiOptionDatabaseRecord record = new ApiOptionDatabaseRecord();
        record.setOption(mapper.convertValue(option, new TypeReference<Map<String, Object>>() {
        }));
        record.setId(id);
        record.setVersionNumber(generateVersionNumber());
        dao.createApiOption(record);
        return record.getVersionNumber();
    }
}

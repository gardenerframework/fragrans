package com.jdcloud.gardener.fragrans.api.options.persistence.dao;

import com.jdcloud.gardener.fragrans.api.options.persistence.schema.ApiOptionDatabaseRecord;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.CommonScannerCallbacks;
import com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.schema.criteria.CommonCriteria;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.lang.Nullable;

/**
 * @author zhanghan30
 * @date 2022/5/10 6:28 上午
 */
@Mapper
public interface ApiOptionMysqlDao extends ApiOptionDao {
    /**
     * 创建记录
     *
     * @param record 记录
     */
    @Override
    @InsertProvider(SqlProvider.class)
    void createApiOption(@Param("record") ApiOptionDatabaseRecord record);

    /**
     * 读取记录
     *
     * @param id id
     * @return 结果
     */
    @Override
    @SelectProvider(SqlProvider.class)
    @Nullable
    ApiOptionDatabaseRecord readApiOption(@Param(CommonCriteria.QueryByIdCriteria.ID_PARAMETER_NAME) String id);

    /**
     * 更新选项
     *
     * @param id     id
     * @param record 选项
     */
    @Override
    @UpdateProvider(SqlProvider.class)
    void updateApiOption(@Param(CommonCriteria.QueryByIdCriteria.ID_PARAMETER_NAME) String id, @Param("record") ApiOptionDatabaseRecord record);

    class SqlProvider implements ProviderMethodResolver {
        public String createApiOption(ApiOptionDatabaseRecord record) {
            return StatementBuilderStaticAccessor.builder()
                    .insert(
                            ApiOptionDatabaseRecord.class,
                            new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(),
                            "record"
                    )
                    .build();
        }

        public String readApiOption(String id) {
            return StatementBuilderStaticAccessor.builder().select(
                    ApiOptionDatabaseRecord.class,
                    new CommonScannerCallbacks.SelectStatementIgnoredAnnotations()
            ).where(new CommonCriteria.QueryByIdCriteria()).build();
        }

        public String updateApiOption(String id, ApiOptionDatabaseRecord record) {
            return StatementBuilderStaticAccessor.builder().update(
                    ApiOptionDatabaseRecord.class,
                    new CommonScannerCallbacks.UpdateStatementIgnoredAnnotations(),
                    "record"
            ).where(new CommonCriteria.QueryByIdCriteria()).build();

        }
    }
}

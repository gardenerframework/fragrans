package com.jdcloud.gardener.fragrans.data.persistence.test.utils.fieldTest;

import com.jdcloud.gardener.fragrans.data.persistence.orm.entity.FieldScanner;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.StatementBuilderStaticAccessor;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BasicCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.BatchCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.InsertStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.SelectStatement;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.statement.StatementCharSequenceAdapter;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import com.jdcloud.gardener.fragrans.data.schema.annotation.DatabaseControlledField;
import com.jdcloud.gardener.fragrans.data.schema.annotation.ImmutableField;
import com.jdcloud.gardener.fragrans.data.schema.annotation.ReadBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.annotation.UpdateBySpecificOperation;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author ZhangHan
 * @date 2022/6/16 0:22
 */
@Mapper
public interface FieldTestDao extends ApplicationContextAware {
    @InsertProvider(SqlProvider.class)
    void add(@Param("object") FieldTestObject object);

    @InsertProvider(SqlProvider.class)
    void batchAdd(@Param("list") List<FieldTestObject> list);

    @DeleteProvider(SqlProvider.class)
    void deleteAll();

    @SelectProvider(SqlProvider.class)
    long count();

    @SelectProvider(SqlProvider.class)
    List<FieldTestObject> query(@Param("prefix") String prefix, int pageNo, int pageSize);

    @Select("select FOUND_ROWS()")
    long foundRows();

    @DeleteProvider(SqlProvider.class)
    void deleteById(@Param("id") String id);

    @SelectProvider(SqlProvider.class)
    FieldTestObject get(@Param("id") String id);

    @UpdateProvider(SqlProvider.class)
    void update(@Param("id") String id, @Param("test") String test);

    @UpdateProvider(SqlProvider.class)
    void updateRecord(@Param("id") String id, @Param("object") FieldTestObject object);

    @UpdateProvider(SqlProvider.class)
    void updateTwinFiled(@Param("id") String id, @Param("test") String test, @Param("other") boolean other);

    @SelectProvider(SqlProvider.class)
    Collection<FieldTestObject> batchNestedSelect(@Param("ids") Collection<String> ids);

    @SelectProvider(SqlProvider.class)
    Collection<FieldTestObject> batchNestedCollectionSelect(@Param("idHolder") IdsInNestedObject param);

    class SqlProvider implements ProviderMethodResolver {

        public StatementCharSequenceAdapter<InsertStatement> add(@Param("object") FieldTestObject object) {
            return new StatementCharSequenceAdapter<>(StatementBuilderStaticAccessor.builder().insert(FieldTestObject.class, new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(), "object"));
        }

        public String batchAdd(@Param("list") List<FieldTestObject> list) {
            return StatementBuilderStaticAccessor.builder().insert(FieldTestObject.class, new CommonScannerCallbacks.InsertStatementIgnoredAnnotations(), "list", "item").build();
        }

        public String deleteAll() {
            return StatementBuilderStaticAccessor.builder().delete(FieldTestObject.class).build();
        }

        public String count() {
            return StatementBuilderStaticAccessor.builder().select().table(FieldTestObject.class).column("count(1)", false).build();
        }

        public String query(@Param("prefix") String prefix, int pageNo, int pageSize) {
            return StatementBuilderStaticAccessor.builder()
                    .select(FieldTestObject.class, FieldScanner::columns)
                    .countFoundRows(true)
                    .where(
                            StringUtils.hasText(prefix) ?
                                    new BasicCriteria() {
                                        @Override
                                        public String build() {
                                            return String.format("`test` like concat(%s, \"%%\")", new ParameterNameValue("prefix").build());
                                        }
                                    } : null
                    )
                    .orderBy("id", SelectStatement.Order.DESC)
                    .limit((pageNo - 1L) * pageSize, pageSize).build();
        }

        public String get(@Param("id") String id) {
            return StatementBuilderStaticAccessor.builder().select(FieldTestObject.class, FieldScanner::columns).where(new CommonCriteria.QueryByIdCriteria()).build();
        }

        public String deleteById(@Param("id") String id) {
            return StatementBuilderStaticAccessor.builder().delete(FieldTestObject.class).where(new CommonCriteria.QueryByIdCriteria()).build();
        }

        public String update(@Param("id") String id, @Param("test") String test) {
            return StatementBuilderStaticAccessor.builder().update(FieldTestObject.class)
                    .column("test", ParameterNameValue::new)
                    .where(new CommonCriteria.QueryByIdCriteria())
                    .build();
        }

        public String updateRecord(@Param("id") String id, @Param("object") FieldTestObject object) {
            return StatementBuilderStaticAccessor.builder()
                    .update(
                            FieldTestObject.class,
                            new CommonScannerCallbacks.UpdateStatementIgnoredAnnotations(), "object"
                    ).where(new CommonCriteria.QueryByIdCriteria())
                    .build();
        }

        public String updateTwinFiled(@Param("id") String id, @Param("test") String test, @Param("other") boolean other) {
            return StatementBuilderStaticAccessor.builder().update(FieldTestObject.class)
                    .column("test", ParameterNameValue::new)
                    .column("other", ParameterNameValue::new)
                    .where(new CommonCriteria.QueryByIdCriteria()).build();
        }

        public String batchNestedSelect(@Param("ids") Collection<String> ids) {
            return StatementBuilderStaticAccessor.builder().
                    select(FieldTestObject.class, FieldScanner::columns).table(
                            StatementBuilderStaticAccessor.builder().select(
                                    FieldTestObject.class, FieldScanner::columns
                            ).where(new BatchCriteria().collection("ids").item("id").criteria(
                                    new EqualsCriteria("id", new ParameterNameValue("id"))
                            )),
                            "t"
                    ).where(
                            new BatchCriteria().collection("ids").item("id").criteria(
                                    new EqualsCriteria("id", new ParameterNameValue("id"))
                            )
                    ).build();
        }

        public String batchNestedCollectionSelect(@Param("idHolder") IdsInNestedObject param) {
            return StatementBuilderStaticAccessor.builder().
                    select(FieldTestObject.class, FieldScanner::columns, "t").table(
                            StatementBuilderStaticAccessor.builder().select(
                                    FieldTestObject.class, FieldScanner::columns
                            ).where(new BatchCriteria().collection("idHolder.ids").item("id").criteria(
                                    new EqualsCriteria("id", new ParameterNameValue("id"))
                            )),
                            "t"
                    ).where(
                            new BatchCriteria().collection("idHolder.ids").item("id").criteria(
                                    new EqualsCriteria("id", new ParameterNameValue("id"))
                            )
                    ).build();
        }
    }

    public class CommonCriteria {

        /**
         * 按id查询 id` = #{id} 或使用方给一个参数的名称
         */
        public static class QueryByIdCriteria extends EqualsCriteria {
            private static final String ID_FIELD_NAME = "id";

            static {
                try {
                    BasicEntity.class.getDeclaredField(ID_FIELD_NAME);
                } catch (NoSuchFieldException exception) {
                    throw new IllegalStateException(exception);
                }
            }

            /**
             * 构建一个id判等条件
             */
            public QueryByIdCriteria() {
                this(ID_FIELD_NAME);
            }

            /**
             * 构建一个id判等条件
             *
             * @param idParameterName id对应的参数名
             */
            public QueryByIdCriteria(String idParameterName) {
                super(ID_FIELD_NAME, new ParameterNameValue(idParameterName));
            }
        }
    }

    class CommonScannerCallbacks {
        /**
         * 跳过常见的select语句忽略的注解
         */
        public static class SelectStatementIgnoredAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {

            @Override
            public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
                return fieldScanner.columns(aClass, Collections.singletonList(ReadBySpecificOperation.class), false);
            }
        }

        /**
         * 跳过常见的插入数据忽略的注解
         */
        public static class InsertStatementIgnoredAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {

            @Override
            public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
                return fieldScanner.columns(aClass, Collections.singletonList(DatabaseControlledField.class), false);
            }
        }

        /**
         * 跳过常见的更新数据忽略的注解
         */
        public static class UpdateStatementIgnoredAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {

            @Override
            public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
                return fieldScanner.columns(aClass, Arrays.asList(ImmutableField.class, DatabaseControlledField.class, UpdateBySpecificOperation.class), false);
            }
        }
    }
}

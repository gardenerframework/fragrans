package io.gardenerframework.fragrans.data.persistence.criteria.support;

import io.gardenerframework.fragrans.data.persistence.criteria.annotation.Batch;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.CriteriaProvider;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.TypeConstraints;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory.CriteriaFactory;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory.EqualsFactory;
import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScanner;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.BatchCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAnyCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.FieldNameValue;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * @author ZhangHan
 * @date 2022/11/28 13:51
 */
public class CriteriaBuilder {
    /**
     * 存储一下每个criteria类型实现的trait清单
     */
    private final static Map<Class<?>, Collection<Field>> criteriaFieldsCache = new ConcurrentHashMap<>();
    private final static Map<Class<? extends CriteriaFactory>, CriteriaFactory> factories = new ConcurrentHashMap<>();
    @Getter
    private final static CriteriaBuilder instance = new CriteriaBuilder();
    /**
     * 当没有注解出现时自动使用判等
     */
    @Setter
    private boolean usingEqualsIfNoAnnotationPresent = true;

    public static void addCriteriaFactory(CriteriaFactory factory) {
        factories.put(factory.getClass(), factory);
    }

    /**
     * 按照一种指定must/should的模式创建搜索条件
     *
     * @param tableName             表名
     * @param entityType            试题类型
     * @param criteria              条件
     * @param criteriaParameterName 条件参数名
     * @param must                  哪些trait是必须的
     * @param should                哪些trait是可选的
     * @param <C>                   条件类型
     * @return 搜索条件
     */
    public <E, C> MatchAllCriteria createCriteria(
            @Nullable String tableName,
            @NonNull Class<E> entityType,
            @NonNull C criteria,
            @NonNull String criteriaParameterName,
            @Nullable Collection<String> must,
            @Nullable Collection<String> should
    ) {
        return createCriteria(
                tableName,
                entityType,
                criteria,
                criteriaParameterName,
                new DefaultFilter<>(),
                must,
                should
        );
    }

    /**
     * 按照一种指定must/should的模式创建搜索条件
     *
     * @param tableName             表名
     * @param entityType            试题类型
     * @param criteria              条件
     * @param criteriaParameterName 条件参数名
     * @param filter                过滤器
     * @param must                  哪些trait是必须的
     * @param should                哪些trait是可选的
     * @param <C>                   条件类型
     * @return 搜索条件
     */
    public <E, C> MatchAllCriteria createCriteria(
            @Nullable String tableName,
            @NonNull Class<E> entityType,
            @NonNull C criteria,
            @NonNull String criteriaParameterName,
            @NonNull BiFunction<? super C, Field, Boolean> filter,
            @Nullable Collection<String> must,
            @Nullable Collection<String> should
    ) {
        //条件中的字段 -> 对应的搜索条件
        return createCriteria(
                createFieldCriteriaMapping(tableName, entityType, criteria, criteriaParameterName, filter),
                must,
                should
        );
    }

    /**
     * 按照一种指定must/should的模式创建搜索条件
     *
     * @param criteriaFieldMapping 条件字段-> 条件搜索的映射
     * @param must                 哪些trait是必须的
     * @param should               哪些trait是可选的
     * @return 搜索条件
     */
    public MatchAllCriteria createCriteria(
            Map<String, DatabaseCriteria> criteriaFieldMapping,
            @Nullable Collection<String> must,
            @Nullable Collection<String> should
    ) {
        MatchAllCriteria criteriaCreated = new MatchAllCriteria();
        if (!CollectionUtils.isEmpty(must)) {
            MatchAllCriteria mustCriteria = new MatchAllCriteria();
            must.forEach(
                    fieldName -> {
                        DatabaseCriteria basicCriteria = criteriaFieldMapping.get(fieldName);
                        if (basicCriteria != null) {
                            mustCriteria.and(basicCriteria);
                        }
                    }
            );
            if (!mustCriteria.isEmpty()) {
                criteriaCreated.and(mustCriteria);
            }
        }
        if (!CollectionUtils.isEmpty(should)) {
            MatchAnyCriteria shouldCriteria = new MatchAnyCriteria();
            should.forEach(
                    fieldName -> {
                        DatabaseCriteria basicCriteria = criteriaFieldMapping.get(fieldName);
                        if (basicCriteria != null) {
                            shouldCriteria.or(basicCriteria);
                        }
                    }
            );
            if (!shouldCriteria.isEmpty()) {
                criteriaCreated.and(shouldCriteria);
            }
        }
        return criteriaCreated;
    }

    /**
     * 创建 trait class与搜索条件之间的mapping关系
     * <p>
     * 自动过滤以下字段:
     * <p>
     * * null的字段
     * <p>
     * * 没有字符的字符串
     * <p>
     * * 空的集合
     *
     * @param tableName             表名
     * @param entityType            实体类型
     * @param criteria              搜索条件
     * @param criteriaParameterName 搜索条件参数名
     * @param <E>实体类型
     * @param <C>                   搜索条件类型
     * @return 映射
     */
    public <E, C> Map<String, DatabaseCriteria> createFieldCriteriaMapping(
            @Nullable String tableName,
            @NonNull Class<E> entityType,
            @NonNull C criteria,
            @NonNull String criteriaParameterName
    ) {
        return createFieldCriteriaMapping(
                tableName, entityType, criteria, criteriaParameterName,
                new DefaultFilter<>()
        );
    }

    /**
     * 创建 trait class与搜索条件之间的mapping关系
     *
     * @param tableName             表名
     * @param entityType            实体类型，要求搜索条件实现的trait，实体必须全部实现
     * @param criteria              搜索条件
     * @param criteriaParameterName 搜索条件参数名
     * @param filter                过滤器，如果返回true则说明这个字段包含了真正要搜索的值，否则就忽略这个字段了
     * @param <C>                   搜索条件类型
     * @return 映射
     */
    public <E, C> Map<String, DatabaseCriteria> createFieldCriteriaMapping(
            @Nullable String tableName,
            @NonNull Class<E> entityType,
            @NonNull C criteria,
            @NonNull String criteriaParameterName,
            @NonNull BiFunction<? super C, Field, Boolean> filter
    ) {
        Map<String, DatabaseCriteria> mapping = new HashMap<>();
        //获取搜索条件的所有Field
        Collection<Field> criteriaFields = CriteriaBuilder.criteriaFieldsCache.get(criteria.getClass());
        //缓存miss则执行解析
        if (CollectionUtils.isEmpty(criteriaFields)) {
            criteriaFields = parseCriteriaFields(criteria.getClass());
            //放到缓存中以免重复解析
            CriteriaBuilder.criteriaFieldsCache.put(criteria.getClass(), criteriaFields);
        }
        criteriaFields.forEach(
                criteriaField -> {
                    //假设实体中也有同名的待搜索字段
                    try {
                        //过滤器声明当前字段不需要执行搜索条件的创建
                        if (!Boolean.TRUE.equals(filter.apply(criteria, criteriaField))) {
                            //如果当前过滤器说不需要处理这个字段
                            return;
                        }
                        //条件字段映射的实体字段名
                        String criteriaMappedEntityFieldName = criteriaField.getName();
                        //查看是否是批量操作
                        DatabaseCriteria criteriaCreated = null;
                        //判断集合逻辑
                        Batch batch = AnnotationUtils.findAnnotation(criteriaField, Batch.class);
                        if (batch != null) {
                            //检查标记的属性是否是个集合，集合元素是否是支持的类型
                            //获取当前字段类型
                            Class<?> fieldType = criteriaField.getType();
                            //字段类型必须是集合
                            Assert.isTrue(Collection.class.isAssignableFrom(fieldType), criteriaField.getName() + " is not a collection for @Batch");
                            //获取集合的值
                            boolean isAccessible = criteriaField.isAccessible();
                            criteriaField.setAccessible(true);
                            Object o = criteriaField.get(criteria);
                            criteriaField.setAccessible(isAccessible);
                            //不是空的集合
                            if (!CollectionUtils.isEmpty((Collection<?>) o)) {
                                Optional<?> first = ((Collection<?>) o).stream().findFirst();
                                Object element = first.get();
                                Assert.isTrue(element instanceof String || element.getClass().isPrimitive(), "only primitive type or String is supported for @Batch");
                            }
                            criteriaCreated = new BatchCriteria();
                            //设置集合名称
                            ((BatchCriteria) criteriaCreated).collection(criteriaParameterName, criteriaField.getName());
                            //设置元素为item
                            ((BatchCriteria) criteriaCreated).item("item");
                            if (!void.class.equals(batch.value()) && batch.value() != null) {
                                Class<?> fieldTrait = batch.value();
                                //在实体类中检查是否有这个字段
                                //修改映射的字段名称
                                criteriaMappedEntityFieldName = FieldScanner.getInstance().field(entityType, fieldTrait);
                            }
                        }
                        //检查同名字段在实体的类型中是否存在
                        //在@Batch注解中，有可能将复数命名的字段转为实体的某个字段
                        if (ReflectionUtils.findField(entityType, criteriaMappedEntityFieldName) == null) {
                            //不存在不创建条件 - 与约定不同
                            throw new IllegalArgumentException(entityType + " did not have a field of " + criteriaMappedEntityFieldName);
                        }
                        //反向获取列名 - 使用实体的列名而不是搜索条件通过扫描获得的列名
                        String column = FieldScanner.getInstance().getConverter(entityType).fieldToColumn(criteriaMappedEntityFieldName);
                        //最后一个处理equals
                        CriteriaProvider criteriaProvider = AnnotationUtils.findAnnotation(criteriaField, CriteriaProvider.class);
                        if (criteriaProvider != null || usingEqualsIfNoAnnotationPresent) {
                            //不是集合
                            CriteriaFactory criteriaFactory = factories.get(criteriaProvider == null ? EqualsFactory.class : criteriaProvider.value());
                            Assert.notNull(criteriaFactory, "cannot get CriteriaFactory bean of " + (criteriaProvider == null ? EqualsFactory.class : criteriaProvider.value()));
                            TypeConstraints typeConstraints = AnnotationUtils.findAnnotation(criteriaFactory.getClass(), TypeConstraints.class);
                            if (typeConstraints != null && !typeConstraints.value().isAssignableFrom(criteriaField.getType())) {
                                //检查条件中的字段是否符合类型约定
                                throw new IllegalArgumentException("\"" + criteriaField.getName() + "\"" + " did not match type constraints: " + typeConstraints.value().getSimpleName());
                            }
                            if (criteriaCreated == null) {
                                criteriaCreated = criteriaFactory.createCriteria(
                                        entityType,
                                        criteria,
                                        criteriaParameterName,
                                        new Column(tableName, column),
                                        //这里要去搜索条件参数的字段而不是映射到实体的字段
                                        new FieldNameValue(criteriaParameterName, criteriaField.getName())
                                );
                            } else {
                                //集合增加内置条件
                                ((BatchCriteria) criteriaCreated).criteria(criteriaFactory.createCriteria(
                                        entityType,
                                        criteria,
                                        criteriaParameterName,
                                        new Column(tableName, column),
                                        new ParameterNameValue("item")
                                ));
                            }
                            mapping.put(criteriaField.getName(), criteriaCreated);
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
        );
        return mapping;
    }

    private Collection<Field> parseCriteriaFields(@NonNull Class<?> type) {
        Collection<Field> fields = new LinkedList<>();
        Collection<String> fieldNames = new HashSet<>();
        ReflectionUtils.doWithFields(type, fields::add, field -> {
            //如果已经出现同名字段则不再添加到解析清单中
            if (fieldNames.contains(field.getName())) {
                return false;
            }
            fieldNames.add(field.getName());
            return true;
        });
        return fields;
    }

    private static class DefaultFilter<C> implements BiFunction<C, Field, Boolean> {
        @Override
        public Boolean apply(C c, Field field) {
            try {
                boolean isAccessible = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(c);
                field.setAccessible(isAccessible);
                if (o == null) {
                    //过滤空字段
                    return false;
                } else {
                    if (o instanceof String) {
                        //字符串不是空的
                        return StringUtils.hasText((String) o);
                    }
                    if (o instanceof Collection) {
                        //集合不是空的
                        return !CollectionUtils.isEmpty((Collection<?>) o);
                    }
                }
                //没好友其它过滤理由，放行
                return true;
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

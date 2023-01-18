package io.gardenerframework.fragrans.data.persistence.criteria.support;

import io.gardenerframework.fragrans.data.persistence.criteria.annotation.Batch;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.CriteriaProvider;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory.CriteriaFactory;
import io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory.EqualsFactory;
import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScannerStaticAccessor;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.column.Column;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.BatchCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.DatabaseCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAllCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.criteria.MatchAnyCriteria;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.FieldNameValue;
import io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import io.gardenerframework.fragrans.sugar.trait.utils.TraitUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangHan
 * @date 2022/11/28 13:51
 */
@Component
public class CriteriaBuilder {
    /**
     * 存储一下每个criteria类型实现的trait清单
     */
    private final static Map<Class<?>, Collection<Class<?>>> criteriaTraitClasses = new ConcurrentHashMap<>();
    private final Map<Class<?>, CriteriaFactory> factoryCache = new ConcurrentHashMap<>();
    /**
     * 当没有注解出现时自动使用判等
     */
    @Setter
    private boolean usingEqualsIfNoAnnotationPresent = true;

    public CriteriaBuilder(Collection<CriteriaFactory> criteriaFactories) {
        if (!CollectionUtils.isEmpty(criteriaFactories)) {
            criteriaFactories.forEach(
                    criteriaFactory -> factoryCache.put(criteriaFactory.getClass(), criteriaFactory)
            );
        }
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
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
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
            @NonNull TriFunction<Class<?>, ? super C, Field, Boolean> filter,
            @Nullable Collection<Class<?>> must,
            @Nullable Collection<Class<?>> should
    ) {
        Map<Class<?>, DatabaseCriteria> criteriaTraitMapping = createCriteriaTraitMapping(tableName, entityType, criteria, criteriaParameterName, filter);
        MatchAllCriteria criteriaCreated = new MatchAllCriteria();
        if (!CollectionUtils.isEmpty(must)) {
            MatchAllCriteria mustCriteria = new MatchAllCriteria();
            must.forEach(
                    trait -> {
                        DatabaseCriteria basicCriteria = criteriaTraitMapping.get(trait);
                        if (basicCriteria != null) {
                            mustCriteria.and(basicCriteria);
                        }
                    }
            );
            criteriaCreated.and(mustCriteria);
        }
        if (!CollectionUtils.isEmpty(should)) {
            MatchAnyCriteria shouldCriteria = new MatchAnyCriteria();
            should.forEach(
                    trait -> {
                        DatabaseCriteria basicCriteria = criteriaTraitMapping.get(trait);
                        if (basicCriteria != null) {
                            shouldCriteria.or(basicCriteria);
                        }
                    }
            );
            criteriaCreated.and(shouldCriteria);
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
    public <E, C> Map<Class<?>, DatabaseCriteria> createCriteriaTraitMapping(
            @Nullable String tableName,
            @NonNull Class<E> entityType,
            @NonNull C criteria,
            @NonNull String criteriaParameterName
    ) {
        return createCriteriaTraitMapping(
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
    public <E, C> Map<Class<?>, DatabaseCriteria> createCriteriaTraitMapping(
            @Nullable String tableName,
            @NonNull Class<E> entityType,
            @NonNull C criteria,
            @NonNull String criteriaParameterName,
            @NonNull TriFunction<Class<?>, ? super C, Field, Boolean> filter
    ) {
        Map<Class<?>, DatabaseCriteria> mapping = new HashMap<>();
        //获取接口实现的所有traits
        Collection<Class<?>> traits = criteriaTraitClasses.get(criteria.getClass());
        if (CollectionUtils.isEmpty(traits)) {
            traits = resolveTraits(criteria.getClass());
            criteriaTraitClasses.put(criteria.getClass(), traits);
        }
        traits.forEach(
                trait -> {
                    //假设实体实现的trait和当前trait一样
                    Class<?> entityTrait = trait;
                    try {
                        FieldFinder fieldFinder = new FieldFinder(criteria.getClass(), trait);
                        //获取每一个trait对应的字段
                        ReflectionUtils.doWithFields(criteria.getClass(), fieldFinder);
                        Field criteriaField = fieldFinder.getField();
                        //检查当前字段是否已经被过滤掉(即不进行搜索)
                        if (!Boolean.TRUE.equals(filter.apply(trait, criteria, criteriaField))) {
                            //如果当前过滤器说不需要处理这个字段
                            return;
                        }
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
                                if (first.isPresent()) {
                                    Object element = first.get();
                                    Assert.isTrue(element instanceof String || element.getClass().isPrimitive(), "only primitive type or String is supported for @Batch");
                                }
                            }
                            criteriaCreated = new BatchCriteria();
                            //设置集合名称
                            ((BatchCriteria) criteriaCreated).collection(criteriaParameterName, criteriaField.getName());
                            //设置元素为item
                            ((BatchCriteria) criteriaCreated).item("item");
                            if (batch.value() != null) {
                                //执行trait的重定向
                                //也就是实体实现的trait和搜索条件使用的trait不是一个类型
                                entityTrait = batch.value();
                            }
                        }
                        //要求实体必须实现指定的trait，也就是开发不能随便写一个实体没有实现的类型
                        fieldFinder = new FieldFinder(
                                entityType,
                                entityTrait
                        );
                        ReflectionUtils.doWithFields(entityType, fieldFinder);
                        //反向获取列名 - 使用实体的列名而不是搜索条件通过扫描获得的列名
                        String column = FieldScannerStaticAccessor.scanner().column(entityType, entityTrait);
                        //最后一个处理equals
                        CriteriaProvider criteriaProvider = AnnotationUtils.findAnnotation(criteriaField, CriteriaProvider.class);
                        if (criteriaProvider != null || usingEqualsIfNoAnnotationPresent) {
                            //不是集合
                            CriteriaFactory criteriaFactory = factoryCache.get(criteriaProvider == null ? EqualsFactory.class : criteriaProvider.value());
                            Assert.notNull(criteriaFactory, "cannot get CriteriaFactory bean of " + (criteriaProvider == null ? EqualsFactory.class : criteriaProvider.value()));
                            if (criteriaCreated == null) {
                                criteriaCreated = criteriaFactory.createCriteria(
                                        entityType,
                                        criteria,
                                        criteriaParameterName,
                                        new Column(tableName, column),
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
                            mapping.put(trait, criteriaCreated);
                        }
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
        );
        return mapping;
    }

    private Collection<Class<?>> resolveTraits(@NonNull Class<?> type) {
        Collection<Class<?>> traits = new HashSet<>();
        Class<?>[] interfaces = type.getInterfaces();
        //遍历当前类的接口
        for (Class<?> _interface : interfaces) {
            if (_interface.getInterfaces().length > 0) {
                //该接口实现其它接口，解析父接口
                traits.addAll(resolveTraits(_interface));
            } else {
                if (TraitUtils.isTraitField(_interface)) {
                    traits.add(_interface);
                } else {
                    //原则上来说，搜索条件应当只实现trait接口，不应当实现别的什么功能性的接口
                    throw new IllegalArgumentException(_interface + "is not a field trait which should have one and only one getter/setter");
                }
            }
        }
        //有基类继续处理基类
        Class<?> superclass = type.getSuperclass();
        if (superclass != null && !Object.class.equals(superclass)) {
            traits.addAll(resolveTraits(superclass));
        }
        return traits;
    }

    @RequiredArgsConstructor
    private static class FieldFinder implements ReflectionUtils.FieldCallback {
        private final Class<?> type;
        private final Class<?> trait;
        @Getter
        @Setter
        private Field field;

        @Override
        public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
            if (field.getName().equals(
                    FieldScannerStaticAccessor.scanner().field(
                            type,
                            trait
                    )
            )) {
                this.field = field;
            }
        }
    }

    private static class DefaultFilter<C> implements TriFunction<Class<?>, C, Field, Boolean> {

        @Override
        public Boolean apply(Class<?> aClass, C c, Field field) {
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

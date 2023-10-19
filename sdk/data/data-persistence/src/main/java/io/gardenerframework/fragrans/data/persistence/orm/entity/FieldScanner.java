package io.gardenerframework.fragrans.data.persistence.orm.entity;

import io.gardenerframework.fragrans.data.persistence.orm.entity.annotation.UsingColumnNameConverter;
import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.CamelToUnderscoreConverter;
import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;
import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.NoopConverter;
import io.gardenerframework.fragrans.sugar.trait.utils.TraitUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用来扫描字段
 *
 * @author zhanghan30
 * @date 2022/6/14 4:49 下午
 */
@RequiredArgsConstructor
public class FieldScanner {
    /**
     * 转换器缓存
     */
    private static final Map<Class<? extends ColumnNameConverter>, ColumnNameConverter> converters = new ConcurrentHashMap<>();
    /**
     * 扫描缓存
     * <p>
     * 在多个实例间共享
     */
    private final static SingleFieldResultCache singleFieldResultCache = new SingleFieldResultCache();
    /**
     * 静态单例
     */
    @Getter
    private static final FieldScanner instance = new FieldScanner();
    /**
     * 设置是否使用缓存
     */
    @Setter
    private static boolean cacheSingleFieldScanningResult = true;
    /**
     * 默认是驼峰转下划线
     */
    @Getter
    private final ColumnNameConverter defaultConverter = new CamelToUnderscoreConverter();

    public static void addColumnNameConverter(ColumnNameConverter converter) {
        converters.put(converter.getClass(), converter);
    }

    /**
     * 使用注解或trait获取类型的属性，这个属性必须是唯一确定的
     *
     * @param clazz             类型
     * @param traitOrAnnotation 注解或trait
     * @return 属性列
     */
    public String column(Class<?> clazz, Class<?> traitOrAnnotation) {
        return column(clazz, null, traitOrAnnotation);
    }

    /**
     * 使用注解或trait获取类型的属性名而不是列名
     *
     * @param clazz             类型
     * @param traitOrAnnotation 注解或trait
     * @return 属性名
     */
    public String field(Class<?> clazz, Class<?> traitOrAnnotation) {
        return column(clazz, new NoopConverter(), traitOrAnnotation);
    }

    /**
     * 使用注解或trait获取类型的属性，这个属性必须是唯一确定的
     *
     * @param clazz               类型
     * @param columnNameConverter 列名转换器
     * @param traitOrAnnotation   注解或trait
     * @return 属性列
     */
    @SuppressWarnings("unchecked")
    public String column(Class<?> clazz, @Nullable ColumnNameConverter columnNameConverter, Class<?> traitOrAnnotation) {
        return singleFieldResultCache.getOrScan(clazz, columnNameConverter, traitOrAnnotation, () -> {
            Collection<String> fields;
            if (Annotation.class.isAssignableFrom(traitOrAnnotation)) {
                fields = columns(clazz, columnNameConverter, Collections.singletonList((Class<? extends Annotation>) traitOrAnnotation), true);
            } else {
                fields = columns(clazz, columnNameConverter, Collections.singletonList(traitOrAnnotation));
            }
            if (CollectionUtils.isEmpty(fields) || fields.size() != 1) {
                throw new IllegalArgumentException(clazz + " have " + (fields == null ? 0 : fields.size()) + " fields of ");
            }
            return CollectionUtils.firstElement(new HashSet<>(fields));
        });
    }

    /**
     * 取出所有属性列
     *
     * @param clazz           实体类
     * @param traitInterfaces 需要基于的特性接口
     * @return 属性列
     */
    public Collection<String> columns(
            Class<?> clazz,
            Collection<Class<?>> traitInterfaces
    ) {
        return columns(clazz, null, traitInterfaces);
    }

    /**
     * 取出所有属性字段
     *
     * @param clazz           实体类
     * @param traitInterfaces 需要基于的特性接口
     * @return 属性字段
     */
    public Collection<String> fields(
            Class<?> clazz,
            Collection<Class<?>> traitInterfaces
    ) {
        return columns(clazz, new NoopConverter(), traitInterfaces);
    }

    /**
     * 取出所有属性列
     *
     * @param clazz               实体类
     * @param columnNameConverter 属性名到表列名的转换器
     * @param traitInterfaces     需要基于的特性接口
     * @return 属性列
     */
    public Collection<String> columns(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter,
            Collection<Class<?>> traitInterfaces
    ) {
        Set<String> fields = new HashSet<>();
        for (Class<?> trait : traitInterfaces) {
            //必须是个接口
            Assert.isTrue(trait.isInterface(), trait + " is not a interface");
            //类型必须是trait的实现
            Assert.isTrue(trait.isAssignableFrom(clazz), clazz + " must implements " + trait);
            fields.addAll(getTraitFields(trait));
        }
        return columns(clazz, columnNameConverter, field -> fields.contains(field.getName()));
    }

    /**
     * 取出所有属性列
     *
     * @param clazz               实体类
     * @param columnNameConverter 属性名到表列名的转换器
     * @return 属性列
     */
    public Collection<String> columns(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter
    ) {
        return columns(clazz, columnNameConverter, field -> true);
    }

    /**
     * 取出所有属性列
     *
     * @param clazz 实体类
     * @return 属性列
     */
    public Collection<String> columns(
            Class<?> clazz
    ) {
        return columns(clazz, (ColumnNameConverter) null);
    }

    /**
     * 取出所有属性字段
     *
     * @param clazz 实体类
     * @return 属性字段
     */
    public Collection<String> fields(
            Class<?> clazz
    ) {
        return columns(clazz, new NoopConverter());
    }

    /**
     * 扫描所有属性
     *
     * @param clazz               实体类
     * @param columnNameConverter 属性名到表列名的转换器
     * @param annotations         注解
     * @param keep                符合注解的是保留还是去掉
     * @return 所有属性名
     */
    public Collection<String> columns(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter,
            Collection<Class<? extends Annotation>> annotations,
            boolean keep
    ) {
        return columns(
                clazz,
                columnNameConverter,
                field -> {
                    //没有要观察的注解则默认保留字段
                    if (CollectionUtils.isEmpty(annotations)) {
                        return true;
                    }
                    for (Class<? extends Annotation> annotation : annotations) {
                        if (AnnotationUtils.findAnnotation(field, annotation) != null) {
                            //清单中要求的注解在属性上出现了，则是否过滤掉取决于keep
                            return keep;
                        }
                    }
                    //清单中的注解没有出现，则如果要求是满足注解的keep，那么这时应当返回false(因为没有找到注解)
                    //而如果是带有注解的不keep，则应该返回true(因为注解没有出现)
                    return !keep;
                }
        );
    }

    /**
     * 扫描所有属性
     *
     * @param clazz       实体类
     * @param annotations 注解
     * @param keep        符合注解的是保留还是去掉
     * @return 所有属性名
     */
    public Collection<String> columns(
            Class<?> clazz,
            Collection<Class<? extends Annotation>> annotations,
            boolean keep
    ) {
        return columns(clazz, null, annotations, keep);
    }

    /**
     * 扫描所有属性字段
     *
     * @param clazz       实体类
     * @param annotations 注解
     * @param keep        符合注解的是保留还是去掉
     * @return 所有属性字段
     */
    public Collection<String> fields(
            Class<?> clazz,
            Collection<Class<? extends Annotation>> annotations,
            boolean keep
    ) {
        return columns(clazz, new NoopConverter(), annotations, keep);
    }

    /**
     * 扫描一个类的所有字段
     *
     * @param clazz               类
     * @param columnNameConverter 属性名到表列名的转换器
     * @param filter              过滤器, true = 保留 / false = 过滤掉
     * @return 符合的字段名
     */
    public Collection<String> columns(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter,
            Function<Field, Boolean> filter
    ) {
        List<String> columns = new LinkedList<>();
        ReflectionUtils.doWithFields(
                clazz,
                field -> {
                    //静态变量直接去掉
                    if (Modifier.isStatic(field.getModifiers())) {
                        return;
                    }
                    //是否过滤掉
                    if (!Boolean.TRUE.equals(filter.apply(field))) {
                        return;
                    }
                    String columnName = (columnNameConverter == null ? getConverter(clazz) : columnNameConverter).fieldToColumn(field.getName());
                    //相同的列已经出现
                    if (columns.contains(columnName)) {
                        return;
                    }
                    columns.add(columnName);
                }
        );
        return columns;
    }

    /**
     * 扫描一个类的所有字段
     *
     * @param clazz  类
     * @param filter 过滤器, true = 保留 / false = 过滤掉
     * @return 符合的字段名
     */
    public Collection<String> columns(
            Class<?> clazz,
            Function<Field, Boolean> filter
    ) {
        return columns(clazz, null, filter);
    }

    /**
     * 扫描一个类的所有字段
     *
     * @param clazz  类
     * @param filter 过滤器, true = 保留 / false = 过滤掉
     * @return 符合的字段名
     */
    public Collection<String> fields(
            Class<?> clazz,
            Function<Field, Boolean> filter
    ) {
        return columns(clazz, new NoopConverter(), filter);
    }

    /**
     * 获取指定trait的所有属性
     *
     * @param trait trait接口
     * @return 属性清单
     */
    private Collection<String> getTraitFields(Class<?> trait) {
        return TraitUtils.getTraitFieldNames(trait);
    }

    /**
     * 获取列名转换器
     *
     * @param clazz 类
     * @return 转换器实例
     */
    public ColumnNameConverter getConverter(Class<?> clazz) {
        UsingColumnNameConverter annotation = AnnotationUtils.findAnnotation(clazz, UsingColumnNameConverter.class);
        if (annotation == null) {
            return defaultConverter;
        } else {
            return Objects.requireNonNull(converters.get(annotation.value()));
        }
    }

    /**
     * 对于大量的单类的单字段扫描结果进行缓存
     */
    private static class SingleFieldResultCache {
        private final Map<Class<?>, Item> items = new ConcurrentHashMap<>();

        /**
         * 获取缓存或执行扫描
         *
         * @param type                类型
         * @param columnNameConverter 转换器
         * @param traitOrAnnotation   trait或者注解
         * @param resultSupplier      结果provider
         * @return 扫描结果
         */
        public String getOrScan(
                Class<?> type,
                @Nullable ColumnNameConverter columnNameConverter,
                Class<?> traitOrAnnotation,
                Supplier<String> resultSupplier
        ) {
            Item item = items.get(type);
            if (cacheSingleFieldScanningResult && item != null) {
                String cached = item.load(traitOrAnnotation, columnNameConverter);
                if (cached != null) {
                    return cached;
                }
            }
            //执行扫描
            String scanResult = Objects.requireNonNull(resultSupplier.get());
            if (cacheSingleFieldScanningResult) {
                item = (item == null ? new Item() : item);
                item.save(traitOrAnnotation, columnNameConverter, scanResult);
                items.put(type, item);
            }
            return scanResult;
        }

        private class Item {
            /**
             * 扫描结果
             * <p>
             * key = 要扫描的注解或trait类型
             * <p>
             * value = 字段值
             */
            private final Map<Class<?>, Result> results = new ConcurrentHashMap<>();

            public void save(Class<?> traitOrAnnotation, @Nullable ColumnNameConverter columnNameConverter, String result) {
                Result resultHolder = new Result();
                resultHolder.save(columnNameConverter, result);
                results.put(traitOrAnnotation, resultHolder);
            }

            @Nullable
            public String load(Class<?> traitOrAnnotation, @Nullable ColumnNameConverter columnNameConverter) {
                Result result = results.get(traitOrAnnotation);
                if (result != null) {
                    return result.load(columnNameConverter);
                }
                return null;
            }

            private class Result {
                private final Map<Class<?>, String> entries = new ConcurrentHashMap<>();

                public void save(@Nullable ColumnNameConverter columnNameConverter, String result) {
                    entries.put(columnNameConverter == null ? Result.class : columnNameConverter.getClass(), result);
                }

                @Nullable
                public String load(@Nullable ColumnNameConverter columnNameConverter) {
                    return entries.get(columnNameConverter == null ? Result.class : columnNameConverter.getClass());
                }
            }
        }
    }
}

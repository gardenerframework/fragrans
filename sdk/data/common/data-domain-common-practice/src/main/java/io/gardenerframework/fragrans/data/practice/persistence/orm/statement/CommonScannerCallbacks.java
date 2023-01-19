package io.gardenerframework.fragrans.data.practice.persistence.orm.statement;

import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScanner;
import io.gardenerframework.fragrans.data.schema.annotation.DatabaseControlledField;
import io.gardenerframework.fragrans.data.schema.annotation.ImmutableField;
import io.gardenerframework.fragrans.data.schema.annotation.SkipInGenericReadOperation;
import io.gardenerframework.fragrans.data.schema.annotation.SkipInGenericUpdateOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiFunction;

/**
 * 基于最佳实践给些常用的扫描回调
 *
 * @author zhanghan30
 * @date 2022/6/16 4:31 下午
 */
public interface CommonScannerCallbacks {
    class CompositeCallbacks implements BiFunction<FieldScanner, Class<?>, Collection<String>> {
        private final Collection<BiFunction<FieldScanner, Class<?>, Collection<String>>> includes = new LinkedList<>();
        private final Collection<BiFunction<FieldScanner, Class<?>, Collection<String>>> excludes = new LinkedList<>();

        @Override
        public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
            Collection<String> results = new HashSet<>();
            //添加所有扫描结果
            includes.forEach(
                    callback -> results.addAll(callback.apply(fieldScanner, aClass))
            );
            //干掉扫描结果中要求去掉的所有列
            excludes.forEach(
                    callback -> results.removeAll(callback.apply(fieldScanner, aClass))
            );
            return results;
        }

        public CompositeCallbacks include(@NonNull BiFunction<FieldScanner, Class<?>, Collection<String>> callback) {
            includes.add(callback);
            return this;
        }

        public CompositeCallbacks exclude(@NonNull BiFunction<FieldScanner, Class<?>, Collection<String>> callback) {
            excludes.add(callback);
            return this;
        }
    }

    /**
     * 使用Trait来完成字段扫描
     */
    @AllArgsConstructor
    class UsingTraits implements BiFunction<FieldScanner, Class<?>, Collection<String>> {

        private Collection<Class<?>> traits;

        @Override
        public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
            return fieldScanner.columns(aClass, traits);
        }
    }

    /**
     * 跳过注解
     */
    @AllArgsConstructor
    class IgnoreAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {
        private Collection<Class<? extends Annotation>> annotations;

        public Collection<Class<? extends Annotation>> getAnnotations() {
            return Collections.unmodifiableCollection(annotations);
        }

        @Override
        public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
            return fieldScanner.columns(aClass, annotations, false);
        }
    }

    /**
     * 保留注解
     */
    @AllArgsConstructor
    class KeepAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {
        private Collection<Class<? extends Annotation>> annotations;

        public Collection<Class<? extends Annotation>> getAnnotations() {
            return Collections.unmodifiableCollection(annotations);
        }

        @Override
        public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
            return fieldScanner.columns(aClass, annotations, true);
        }
    }

    /**
     * 跳过常见的select语句忽略的注解
     */
    class SelectStatementIgnoredAnnotations extends IgnoreAnnotations {

        public SelectStatementIgnoredAnnotations() {
            super(Collections.singletonList(SkipInGenericReadOperation.class));
        }
    }

    /**
     * 跳过常见的插入数据忽略的注解
     */
    class InsertStatementIgnoredAnnotations extends IgnoreAnnotations {

        public InsertStatementIgnoredAnnotations() {
            super(Collections.singletonList(DatabaseControlledField.class));
        }
    }

    /**
     * 跳过常见的更新数据忽略的注解
     */
    class UpdateStatementIgnoredAnnotations extends IgnoreAnnotations {

        public UpdateStatementIgnoredAnnotations() {
            super(Arrays.asList(ImmutableField.class, DatabaseControlledField.class, SkipInGenericUpdateOperation.class));
        }
    }
}

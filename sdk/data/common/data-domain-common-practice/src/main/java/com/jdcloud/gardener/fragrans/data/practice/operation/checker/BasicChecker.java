package com.jdcloud.gardener.fragrans.data.practice.operation.checker;

import com.jdcloud.gardener.fragrans.data.practice.operation.checker.log.schema.detail.IdsDetail;
import com.jdcloud.gardener.fragrans.log.GenericBasicLoggerMethodTemplate;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericBasicLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import com.jdcloud.gardener.fragrans.log.schema.word.Word;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:49
 */
@Slf4j
@SuperBuilder
public abstract class BasicChecker<I, R> implements RecordChecker<R>, RecordCollectionChecker<R>, RecordIdExtractor<I, R> {
    private final InitFlag initFlag = new InitFlag();
    /**
     * 类型
     */
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Class<?> type;
    /**
     * 记录id
     * <p>
     * 和下面的{@code recordIds}会最终合并到一起
     */
    private I recordId;
    /**
     * 记录id集合
     */
    @Getter(AccessLevel.PROTECTED)
    private Collection<I> recordIds;
    /**
     * 日志记录的等级
     */
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    private GenericBasicLoggerMethodTemplate basicLogTemplate = GenericLoggerStaticAccessor.basicLogger()::error;
    /**
     * 异常工厂方法
     */
    @Setter(AccessLevel.PROTECTED)
    private BiFunction<Collection<I>, REASON, ? extends RuntimeException> exceptionFactory;
    /**
     * 遇到空记录集合是否失败
     */
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    private boolean failOnEmptyRecordCollection = false;
    /**
     * 遇到非空记录集合是否失败
     */
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    private boolean failOnNonEmptyRecordCollection = false;

    /**
     * 给出日志记录中的发生了什么的字段
     *
     * @return 发生了什么的字段
     */
    protected abstract Word getLogHow();

    /**
     * 多个记录不存在的日志详情
     *
     * @param ids 哪些id不符合需求
     * @return 详情
     */
    @Nullable
    protected Detail getLogDetail(Collection<I> ids) {
        return new IdsDetail<>(ids);
    }

    /**
     * 执行单个记录的内部检查逻辑
     *
     * @param record 记录
     * @return 不符合的id清单
     */
    protected abstract boolean doCheck(@Nullable R record);

    protected void init() {

    }


    @Override
    public <T extends R> void check(T record) {
        check(record == null ? Collections.emptyList() : Collections.singletonList(record));
    }

    @Override
    public <T extends R> void check(Collection<T> records) {
        if (!initFlag.isInited()) {
            init();
            initFlag.setInited(true);
        }
        if (!CollectionUtils.isEmpty(recordIds) || recordId != null) {
            Collection<I> ids = prepareIds();
            //如果读取出来的集合是空的，且不允许空集合
            if (CollectionUtils.isEmpty(records)) {
                //空集合意味着失败
                if (failOnEmptyRecordCollection) {
                    fail(ids, REASON.EMPTY_RECORD_COLLECTION);
                }
            } else {
                //非空集合意味着失败
                if (failOnNonEmptyRecordCollection) {
                    fail(ids, REASON.NONEMPTY_RECORD_COLLECTION);
                } else {
                    records.forEach(
                            record -> {
                                if (doCheck(record)) {
                                    ids.remove(extractId(record));
                                }
                            }
                    );
                    if (!CollectionUtils.isEmpty(ids)) {
                        fail(ids, REASON.CHECK);
                    }
                }
            }
        }
    }

    /**
     * 返回一个待检查记录id清单的副本
     *
     * @return 副本
     */
    private Collection<I> prepareIds() {
        List<I> ids = new ArrayList<>();
        if (recordId != null) {
            ids.add(recordId);
        }
        if (!CollectionUtils.isEmpty(recordIds)) {
            ids.addAll(recordIds);
        }
        return ids;
    }

    /**
     * 抛出异常中断流程
     *
     * @param ids    不符合的id
     * @param reason 失败原因
     * @throws RuntimeException 异常
     */
    private void fail(Collection<I> ids, REASON reason) throws RuntimeException {
        if (this.type == null) {
            //默认取子类的模板参数
            ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
            this.type = (Class<?>) genericSuperclass.getActualTypeArguments()[1];
        }
        RuntimeException exception = exceptionFactory.apply(ids, reason);
        basicLogTemplate.log(
                log,
                GenericBasicLogContent.builder()
                        .what(type)
                        .how(getLogHow())
                        .detail(getLogDetail(ids))
                        .build(),
                exception
        );
        throw exception;
    }

    public enum REASON {
        /**
         * 检查失败
         */
        CHECK,
        /**
         * 空记录集合
         */
        EMPTY_RECORD_COLLECTION,
        /**
         * 非空记录集合
         */
        NONEMPTY_RECORD_COLLECTION,
    }

    @Setter
    @Getter
    @NoArgsConstructor
    private class InitFlag {
        private boolean inited = false;
    }
}

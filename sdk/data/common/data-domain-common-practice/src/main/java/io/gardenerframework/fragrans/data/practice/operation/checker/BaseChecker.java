package io.gardenerframework.fragrans.data.practice.operation.checker;

import io.gardenerframework.fragrans.data.practice.operation.checker.log.schema.detail.IdsDetail;
import io.gardenerframework.fragrans.log.GenericLoggerMethodTemplate;
import io.gardenerframework.fragrans.log.GenericLoggerStaticAccessor;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@SuperBuilder
@Slf4j
public abstract class BaseChecker<I, R> implements RecordChecker<R>, RecordCollectionChecker<R>, RecordIdExtractor<I, R> {
    /**
     * 要求检查的id清单
     * <p>
     * 为空就是只检查记录
     */
    @Singular
    @Nullable
    private Collection<I> ids;

    /**
     * 日志记录的等级
     * <p>
     * 大部分检查的失败可以写为info或debug级别
     */
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    private GenericLoggerMethodTemplate<GenericBasicLogContent> basicLogTemplate = GenericLoggers.basicLogger()::debug;

    /**
     * 检查的记录类型
     */
    @NonNull
    @Setter(AccessLevel.PROTECTED)
    private Class<?> target;


    @Override
    public <T extends R> void check(@Nullable T record) {
        //统一到集合检查方法
        check(record == null ? Collections.emptyList() : Collections.singletonList(record));
    }

    @Override
    public <T extends R> void check(Collection<T> records) {
        //不合法的id清单
        Collection<I> invalidIds = CollectionUtils.isEmpty(ids) ? new HashSet<>() : new HashSet<>(ids);
        //检查集合的长度，大小等特征
        if (!checkCollection(records)) {
            fail(invalidIds, Phase.COLLECTION, records);
        }
        records.forEach(
                record -> {
                    I id = extractId(record);
                    //集合检查通过再检查每一个记录
                    if (!checkEachRecord(record)) {
                        //找到不合法记录的id
                        if (CollectionUtils.isEmpty(ids)) {
                            //向非法记录集中添加id
                            invalidIds.add(id);
                        }
                    } else {
                        if (!CollectionUtils.isEmpty(ids)) {
                            //从非法记录集中去掉id
                            invalidIds.remove(id);
                        }
                    }
                }
        );
        if (!CollectionUtils.isEmpty(invalidIds)) {
            fail(invalidIds, Phase.RECORD, records);
        }
    }

    /**
     * 失败处理
     *
     * @param invalidIds 非法id清单
     * @param phase      在什么阶段结束的
     * @throws RuntimeException 抛出异常
     */
    private <T extends R> void fail(Collection<I> invalidIds, Phase phase, Collection<T> records) throws RuntimeException {
        RuntimeException exception = raiseException(invalidIds, phase);
        basicLogTemplate.log(
                log,
                GenericBasicLogContent.builder()
                        .what(target)
                        .how(getHow())
                        .detail(getDetail(invalidIds, phase, records))
                        .build(),
                exception
        );
        throw exception;
    }

    /**
     * 检查读取出的记录集合的长度，大小是否符合要求
     *
     * @param records 集合
     * @param <T>     记录类型
     * @return 是否符合要求
     */
    protected abstract <T extends R> boolean checkCollection(Collection<T> records);

    /**
     * 检查每一个记录
     *
     * @param record 记录
     * @param <T>    记录类型
     * @return 是否符合预期
     */
    protected abstract <T extends R> boolean checkEachRecord(T record);

    /**
     * 检查失败，抛出异常
     *
     * @param invalidIds 不合法的id清单
     * @param phase      检查阶段
     * @return 浴场
     */
    protected abstract RuntimeException raiseException(Collection<I> invalidIds, Phase phase);

    /**
     * 发生了什么
     *
     * @return 日志的how
     */
    protected abstract Word getHow();

    /**
     * 获取日志的详情，默认就是只有不合法的id清单
     *
     * @param invalidIds id清单
     * @param phase
     * @return
     */
    protected <T extends R> Detail getDetail(Collection<I> invalidIds, Phase phase, Collection<T> records) {
        return new IdsDetail<>(invalidIds);
    }

    public enum Phase {
        /**
         * 检查集合时不合法
         */
        COLLECTION,
        /**
         * 检查记录时不合法
         */
        RECORD;
    }
}

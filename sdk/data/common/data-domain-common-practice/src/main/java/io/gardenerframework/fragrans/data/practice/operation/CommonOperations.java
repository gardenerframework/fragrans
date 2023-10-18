package io.gardenerframework.fragrans.data.practice.operation;

import io.gardenerframework.fragrans.data.practice.configuration.DataDomainCommonPracticeComponent;
import io.gardenerframework.fragrans.data.practice.operation.checker.RecordChecker;
import io.gardenerframework.fragrans.data.practice.operation.checker.RecordCollectionChecker;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author ZhangHan
 * @date 2022/6/17 1:33
 */
@DataDomainCommonPracticeComponent
@AllArgsConstructor
public class CommonOperations {

    public ReadThenCheck readThenCheck() {
        return new ReadThenCheck();
    }

    public static class ReadThenCheck {

        /**
         * 读取后验证
         *
         * @param supplier 复杂提供数据
         * @param checkers 负责消费数据
         * @param <R>      数据类型
         * @return 最终独取出来的数据
         */
        @Nullable
        @SafeVarargs
        public final <R> R single(Supplier<R> supplier, RecordChecker<? super R>... checkers) {
            R record = supplier.get();
            for (RecordChecker<? super R> checker : checkers) {
                checker.check(record);
            }
            return record;
        }

        /**
         * 读取后验证
         *
         * @param supplier 复杂提供数据
         * @param checkers 负责消费数据
         * @param <R>      数据类型
         * @return 最终独取出来的数据
         */
        @Nullable
        @SafeVarargs
        public final <R> Collection<R> collection(Supplier<Collection<R>> supplier, RecordCollectionChecker<? super R>... checkers) {
            Collection<R> records = supplier.get();
            for (RecordCollectionChecker<? super R> checker : checkers) {
                checker.check(records);
            }
            return records;
        }
    }
}

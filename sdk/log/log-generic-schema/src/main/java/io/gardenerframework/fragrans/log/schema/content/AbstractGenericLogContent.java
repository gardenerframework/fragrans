package io.gardenerframework.fragrans.log.schema.content;

import io.gardenerframework.fragrans.log.annotation.LogTarget;
import io.gardenerframework.fragrans.log.annotation.ReferLogTarget;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import io.gardenerframework.fragrans.log.schema.word.Word;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

/**
 * @author ZhangHan
 * @date 2022/6/9 0:57
 */
@SuperBuilder
@Getter
public abstract class AbstractGenericLogContent implements Contents {
    /**
     * 什么东西
     */
    private final Class<?> what;
    /**
     * 详情
     */
    @Nullable
    private final Detail detail;

    protected Word getWhatInWord() {
        return new Word() {
            @Override
            public String toString() {

                return what == null ? "" : getLogTarget(what);
            }
        };
    }

    protected Word getDetailInWord() {
        return new Word() {
            @Override
            public String toString() {
                return detail == null ? "" : String.format("[%s]", String.join(", ", detail.getPairs()));
            }
        };
    }

    /**
     * 从带有@LogTarget的类中提取具体的名称
     *
     * @return 日志目标
     */
    private String getLogTarget(Class<?> targetClass) {
        ReferLogTarget referLogTarget = AnnotationUtils.findAnnotation(targetClass, ReferLogTarget.class);
        //优先处理引用的类
        if (referLogTarget != null && referLogTarget.value() != null) {
            String referredText = getLogTarget(referLogTarget.value());
            return String.format(
                    "%s%s%s",
                    referLogTarget.prefix() == null ? "" : referLogTarget.prefix(),
                    referredText,
                    referLogTarget.suffix() == null ? "" : referLogTarget.suffix()
            );
        }
        //处理LogTarget注解
        LogTarget annotation = AnnotationUtils.findAnnotation(targetClass, LogTarget.class);
        if (annotation == null) {
            //使用类名
            return targetClass.getSimpleName();
        } else {
            return annotation.value();
        }
    }
}

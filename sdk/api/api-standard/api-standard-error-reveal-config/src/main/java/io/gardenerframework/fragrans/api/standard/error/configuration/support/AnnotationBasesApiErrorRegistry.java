package io.gardenerframework.fragrans.api.standard.error.configuration.support;

import io.gardenerframework.fragrans.api.standard.error.configuration.ApiErrorRegistry;
import io.gardenerframework.fragrans.api.standard.error.configuration.HideError;
import io.gardenerframework.fragrans.api.standard.error.configuration.RevealError;
import io.gardenerframework.fragrans.log.GenericBasicLogger;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericBasicLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author zhanghan30
 * @date 2022/8/26 8:20 上午
 */
@AllArgsConstructor
@Slf4j
public class AnnotationBasesApiErrorRegistry implements ApplicationContextAware, InitializingBean, ApiErrorRegistry {
    private final Item revealed = new Item();
    private final Item hidden = new Item();
    private final GenericBasicLogger logger;
    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //获取所有隐藏错误注解的bean
        initRegistryItem(HideError.class, hideError -> {
            hidden.getSuperClasses().addAll(Arrays.asList(hideError.superClasses()));
            for (Class<?> clazz : hideError.basePackageClasses()) {
                hidden.getPackages().add(ClassUtils.getPackageName(ClassUtils.getUserClass(clazz)));
            }
        });
        logger.info(
                log,
                GenericBasicLogContent.builder()
                        .what(HideError.class)
                        .how(new Register())
                        .detail(new ItemDetail(hidden.getPackages(), hidden.getSuperClasses()))
                        .build(),
                null
        );
        //获取所有暴露注解的bean
        initRegistryItem(RevealError.class, revealError -> {
            revealed.getSuperClasses().addAll(Arrays.asList(revealError.superClasses()));
            for (Class<?> clazz : revealError.basePackageClasses()) {
                revealed.getPackages().add(ClassUtils.getPackageName(ClassUtils.getUserClass(clazz)));
            }
        });
        logger.info(
                log,
                GenericBasicLogContent.builder()
                        .what(RevealError.class)
                        .how(new Register())
                        .detail(new ItemDetail(revealed.getPackages(), revealed.getSuperClasses()))
                        .build(),
                null
        );
    }

    private <A extends Annotation> void initRegistryItem(Class<A> annotationClass, Consumer<A> consumer) {
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(annotationClass);
        for (String beanName : beanNames) {
            //尝试转为类
            Class<?> beanClass = applicationContext.getType(beanName);
            Assert.notNull(beanClass, "beanClass must not be null");
            A annotation = AnnotationUtils.findAnnotation(beanClass, annotationClass);
            Assert.notNull(annotation, beanClass + "did not have @" + annotationClass.getSimpleName());
            consumer.accept(annotation);
        }
    }

    @Override
    public boolean isErrorRevealed(Class<?> clazz) {
        Assert.notNull(clazz, "clazz must not be null");
        clazz = ClassUtils.getUserClass(clazz);
        //首先判断错误类上是否标记了HideError
        return AnnotationUtils.findAnnotation(clazz, HideError.class) != null ?
                false :
                //再看类上有没有标记说要展示
                AnnotationUtils.findAnnotation(clazz, RevealError.class) != null ?
                        true :
                        //查看是否属于隐藏的范畴
                        hidden.contains(clazz) ?
                                false :
                                //最后就交给展示的范畴
                                revealed.contains(clazz);
    }


    /**
     * 注册表元素
     */
    @Getter
    private static class Item {
        /**
         * 包路径
         */
        private final Set<String> packages = new HashSet<>();
        /**
         * 上级类
         */
        private final Set<Class<?>> superClasses = new HashSet<>();

        /**
         * 判断给定的类是否在表项内
         *
         * @param clazz 类
         * @return true - 包符合或基类符合 / false - 包不符合，基类也不符合
         */
        public boolean contains(Class<?> clazz) {
            clazz = ClassUtils.getUserClass(clazz);
            String packageName = ClassUtils.getPackageName(clazz);
            //首先查找所有包
            for (String packaze : packages) {
                if (packageName.startsWith(packaze + ".") || packageName.equals(packaze)) {
                    return true;
                }
            }
            //其次查看是否是子类
            for (Class<?> superClass : superClasses) {
                if (superClass.isAssignableFrom(clazz)) {
                    return true;
                }
            }
            //都没命中
            return false;
        }
    }

    @AllArgsConstructor
    private static class ItemDetail implements Detail {
        private final Set<String> packages;
        /**
         * 上级类
         */
        private final Set<Class<?>> superClasses;
    }
}

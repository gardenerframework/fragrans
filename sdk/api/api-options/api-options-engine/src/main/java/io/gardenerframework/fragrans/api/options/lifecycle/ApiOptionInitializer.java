package io.gardenerframework.fragrans.api.options.lifecycle;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.api.options.configuration.ApiOptionsEngineComponent;
import io.gardenerframework.fragrans.api.options.configuration.ApiOptionsEngineProperties;
import io.gardenerframework.fragrans.api.options.lifecycle.event.ApiOptionChangedEvent;
import io.gardenerframework.fragrans.api.options.persistence.ApiOptionPersistenceService;
import io.gardenerframework.fragrans.api.options.persistence.schema.ApiOptionRecordSkeleton;
import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionRegistryItem;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionsRegistry;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Create;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 负责从将所有的选项的数据读出来并进行初始化
 *
 * @author zhanghan30
 * @date 2022/5/10 2:14 上午
 */
@ApiOptionsEngineComponent
@Slf4j
@RequiredArgsConstructor
public class ApiOptionInitializer implements ApplicationContextAware, InitializingBean {
    private final ApiOptionsRegistry apiOptionsRegistry;
    private final ApiOptionPersistenceService<?> apiOptionPersistenceService;
    private final ObjectMapper mapper;
    private final GenericOperationLogger logger = GenericLoggers.operationLogger();
    private final ApiOptionsEngineProperties apiOptionsEngineProperties;
    private final Validator validator;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //这里造成了对api option bean的循环依赖，因此理论上应当不要直接初始化和实力化bean
        //于是有关选项在声明成bean到初始化期间期间的数据未初始化问题需要仔细考虑
        Map<String, Object> optionBeans = this.applicationContext.getBeansWithAnnotation(ApiOption.class);
        if (!CollectionUtils.isEmpty(optionBeans)) {
            for (Map.Entry<String, Object> entry : optionBeans.entrySet()) {
                String beanName = entry.getKey();
                Object option = entry.getValue();
                //从持久化中读取选项
                ApiOptionRecordSkeleton record = apiOptionPersistenceService.readOption(beanName);
                if (record != null) {
                    //使用保存的数据来初始化
                    BeanUtils.copyProperties(mapper.convertValue(record.getOption(), option.getClass()), option);
                }
                //新feature: 执行属性检查
                Set<ConstraintViolation<Object>> violations = validator.validate(option);
                if (!CollectionUtils.isEmpty(violations)) {
                    throw new IllegalStateException(violations.stream().map(
                            violation -> String.format("%s.%s:%s",
                                    violation.getRootBeanClass() == null ? "?" : violation.getRootBeanClass().getSimpleName(),
                                    violation.getPropertyPath(), violation.getMessage())
                    ).collect(Collectors.joining(",")));
                }
                apiOptionsRegistry.setItem(
                        beanName,
                        new ApiOptionRegistryItem(
                                option,
                                ClassUtils.getUserClass(option).getCanonicalName(),
                                Objects.requireNonNull(AnnotationUtils.findAnnotation(ClassUtils.getUserClass(option), ApiOption.class)).readonly(),
                                //初始化为当前保存的版本号
                                record == null ? null : record.getVersionNumber()
                        )
                );
            }
        }
        logger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ApiOptionsRegistry.class)
                        .operation(new Create())
                        .state(new Done())
                        .detail(new Detail() {
                            private final Collection<String> options = apiOptionsRegistry.getIds();
                        })
                        .build(),
                null
        );
    }

    /**
     * 当外部发生更新时
     *
     * @param event 更新事件
     */
    @EventListener
    public void onApiOptionChangedEvent(ApiOptionChangedEvent event) {
        String instanceId = event.getInstanceId();
        if (!Objects.equals(ApiOptionChangedEvent.Source.NOTIFICATION, event.getSource()) || Objects.equals(apiOptionsEngineProperties.getInstanceId(), instanceId)) {
            return;
        }
        ApiOptionRecordSkeleton record = apiOptionPersistenceService.readOption(event.getId());
        ApiOptionRegistryItem item = apiOptionsRegistry.getItem(event.getId());
        if (item == null) {
            return;
        }
        Object option = item.getOption();
        if (record != null && option != null) {
            //使用保存的数据来初始化
            BeanUtils.copyProperties(mapper.convertValue(record.getOption(), option.getClass()), option);
        }
    }
}

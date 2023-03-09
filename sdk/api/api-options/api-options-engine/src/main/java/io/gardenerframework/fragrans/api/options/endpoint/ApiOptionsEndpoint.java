package io.gardenerframework.fragrans.api.options.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gardenerframework.fragrans.api.options.configuration.ApiOptionsEngineProperties;
import io.gardenerframework.fragrans.api.options.exception.client.ApiOptionIsReadonlyException;
import io.gardenerframework.fragrans.api.options.exception.client.ApiOptionNotFoundException;
import io.gardenerframework.fragrans.api.options.exception.client.InvalidApiOptionException;
import io.gardenerframework.fragrans.api.options.lifecycle.event.ApiOptionChangedEvent;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionRegistryItem;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionsRegistry;
import io.gardenerframework.fragrans.api.options.schema.response.ReadApiOptionRegistryItemResponse;
import io.gardenerframework.fragrans.api.options.schema.response.ReadApiOptionRegistryResponse;
import io.gardenerframework.fragrans.messages.EnhancedMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanghan30
 * @date 2022/1/3 7:07 下午
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/options")
@Component
@Slf4j
public class ApiOptionsEndpoint implements ApiOptionsEndpointSkeleton, ApplicationEventPublisherAware {
    private final ApiOptionsRegistry registry;
    private final EnhancedMessageSource messageSource;
    private final Validator validator;
    private final ObjectMapper mapper;
    private final ApiOptionsEngineProperties apiOptionsEngineProperties;
    private ApplicationEventPublisher eventPublisher;

    /**
     * 获取api选项
     *
     * @return 选项
     */
    @GetMapping
    @Override
    public ReadApiOptionRegistryResponse readApiOptionRegistry() {
        Collection<String> ids = this.registry.getIds();
        Map<String, ReadApiOptionRegistryItemResponse> responseContent = new HashMap<>(ids.size());
        ids.forEach(
                (id) -> {
                    ApiOptionRegistryItem item = this.registry.getItem(id);
                    Assert.notNull(item, "item must not be null");
                    responseContent.put(
                            id,
                            new ReadApiOptionRegistryItemResponse(
                                    item.getOption(),
                                    item.getName(),
                                    item.isReadonly(),
                                    item.getVersionNumber(),
                                    messageSource.getMessage(
                                            item.getOption(),
                                            ClassUtils.getUserClass(item.getOption()).getCanonicalName(),
                                            LocaleContextHolder.getLocale()
                                    )
                            )
                    );
                }
        );
        return new ReadApiOptionRegistryResponse(responseContent);
    }

    /**
     * 读取指定的选项
     *
     * @param id id
     * @return 选项
     */
    @GetMapping("/{id}")
    @Override
    public ReadApiOptionRegistryItemResponse readApiOptionRegistryItem(@PathVariable("id") @Valid @NotBlank String id) {
        ApiOptionRegistryItem item = this.registry.getItem(id);
        if (item == null) {
            throw new ApiOptionNotFoundException(id);
        }
        return new ReadApiOptionRegistryItemResponse(
                item.getOption(),
                item.getName(),
                item.isReadonly(),
                item.getVersionNumber(),
                messageSource.getMessage(
                        item.getOption(),
                        ClassUtils.getUserClass(item.getOption()).getCanonicalName(),
                        LocaleContextHolder.getLocale()
                )
        );
    }

    /**
     * 覆盖指定选项
     *
     * @param id        id
     * @param newOption 选项值
     */
    @PutMapping("/{id}")
    @Override
    public void saveApiOption(@PathVariable("id") @Valid @NotBlank String id, @Valid @RequestBody Map<String, Object> newOption) {
        ApiOptionRegistryItem item = this.registry.getItem(id);
        if (item == null) {
            throw new ApiOptionNotFoundException(id);
        }
        if (item.isReadonly()) {
            throw new ApiOptionIsReadonlyException(id);
        }
        Object newOptionCopy = mapper.convertValue(newOption, ClassUtils.getUserClass(item.getOption().getClass()));
        //检查选项是否的值是否满足验证需求
        Set<ConstraintViolation<Object>> violations = validator.validate(newOptionCopy);
        if (!CollectionUtils.isEmpty(violations)) {
            throw new InvalidApiOptionException(violations);
        }
        //发布选项变更事件
        eventPublisher.publishEvent(new ApiOptionChangedEvent(id, newOptionCopy, apiOptionsEngineProperties.getInstanceId(), ApiOptionChangedEvent.Source.ENDPOINT));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}

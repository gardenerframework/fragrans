package io.gardenerframework.fragrans.api.options.lifecycle;

import io.gardenerframework.fragrans.api.options.configuration.ApiOptionsEngineComponent;
import io.gardenerframework.fragrans.api.options.exception.client.ApiOptionVersionNumberConflictException;
import io.gardenerframework.fragrans.api.options.lifecycle.event.ApiOptionChangedEvent;
import io.gardenerframework.fragrans.api.options.persistence.ApiOptionPersistenceService;
import io.gardenerframework.fragrans.api.options.persistence.exception.ApiOptionVersionOutOfDateException;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionRegistryItem;
import io.gardenerframework.fragrans.api.options.schema.ApiOptionsRegistry;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Update;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import javax.annotation.Priority;
import java.util.Objects;

/**
 * @author zhanghan30
 * @date 2022/5/10 5:51 上午
 */
@ApiOptionsEngineComponent
@Slf4j
@RequiredArgsConstructor
@Priority(Ordered.HIGHEST_PRECEDENCE)
public class ApiOptionUpdater {
    private final ApiOptionsRegistry apiOptionsRegistry;
    private final ApiOptionPersistenceService<?> apiOptionPersistenceService;
    private final GenericOperationLogger logger = GenericLoggers.operationLogger();

    @EventListener
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void onApiOptionChanged(ApiOptionChangedEvent event) {
        String id = event.getId();
        Object newOption = event.getOption();
        ApiOptionChangedEvent.Source source = event.getSource();
        if (!Objects.equals(ApiOptionChangedEvent.Source.ENDPOINT, source)) {
            //当前是一个外部更新的广播事件，没有需要持久化的数据
            return;
        }
        String newVersionNumber;
        try {
            newVersionNumber = apiOptionPersistenceService.saveOption(id, newOption);
        } catch (ApiOptionVersionOutOfDateException exception) {
            throw new ApiOptionVersionNumberConflictException(exception);
        }
        //完成持久化将本地的数据也进行更新
        ApiOptionRegistryItem item = apiOptionsRegistry.getItem(id);
        Assert.notNull(item, "item must not be null");
        BeanUtils.copyProperties(newOption, item.getOption());
        item.setVersionNumber(newVersionNumber);
        logger.info(
                log,
                GenericOperationLogContent.builder()
                        .what(ApiOptionRegistryItem.class)
                        .operation(new Update())
                        .state(new Done())
                        .detail(new Detail() {
                            private final String id = event.getId();
                        }).build(),
                null
        );
    }
}

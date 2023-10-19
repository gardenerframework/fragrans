package io.gardenerframework.fragrans.data.persistence.configuration;

import io.gardenerframework.fragrans.data.persistence.criteria.annotation.factory.CriteriaFactory;
import io.gardenerframework.fragrans.data.persistence.criteria.support.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

/**
 * @author chris
 * <p>
 * date: 2023/10/19
 */
@Configuration
@AllArgsConstructor
public class CriteriaFactoryAutoConfiguration implements InitializingBean {
    @NonNull
    private Collection<? extends CriteriaFactory> factories;

    @Override
    public void afterPropertiesSet() throws Exception {
        factories.forEach(CriteriaBuilder::addCriteriaFactory);
    }
}

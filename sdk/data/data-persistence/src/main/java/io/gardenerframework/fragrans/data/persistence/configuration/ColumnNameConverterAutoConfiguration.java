package io.gardenerframework.fragrans.data.persistence.configuration;

import io.gardenerframework.fragrans.data.persistence.orm.entity.FieldScanner;
import io.gardenerframework.fragrans.data.persistence.orm.entity.converter.ColumnNameConverter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;

@Configuration
@AllArgsConstructor
public class ColumnNameConverterAutoConfiguration implements InitializingBean {
    @NonNull
    private final Collection<ColumnNameConverter> converters;

    @Override
    public void afterPropertiesSet() throws Exception {
       converters.forEach(FieldScanner::addColumnNameConverter);
    }
}

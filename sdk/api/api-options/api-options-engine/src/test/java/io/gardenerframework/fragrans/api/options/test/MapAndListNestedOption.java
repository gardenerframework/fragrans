package io.gardenerframework.fragrans.api.options.test;

import io.gardenerframework.fragrans.api.options.schema.ApiOption;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/5/10 5:48 上午
 */
@Component
@Data
@ApiOption(readonly = false)
public class MapAndListNestedOption {
    private Map<String, List<String>> nested;
    private List<SimplePlainOption> simples;

    public MapAndListNestedOption() {
    }
}

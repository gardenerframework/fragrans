package io.gardenerframework.fragrans.data.persistence.test.utils.fieldTest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/9/24 00:02
 */
@AllArgsConstructor
@Getter
public class IdsInNestedObject {
    private Collection<String> ids;
}

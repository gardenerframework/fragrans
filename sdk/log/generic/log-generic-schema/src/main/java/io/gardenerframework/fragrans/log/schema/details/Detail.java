package io.gardenerframework.fragrans.log.schema.details;

import io.gardenerframework.fragrans.log.schema.word.Word;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanghan30
 * @date 2022/6/8 6:36 下午
 */
public interface Detail extends Word {
    /**
     * 转成字段名称:字段值的映射
     *
     * @return 映射
     */
    default Map<String, String> getFields() {
        Map<String, String> map = new HashMap<>(10);
        ReflectionUtils.doWithFields(this.getClass(), (field -> {
            if (field.getName().startsWith("val$")
                    || field.getName().startsWith("this$")
                    || Modifier.isStatic(field.getModifiers())
            ) {
                return;
            }
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            map.put(field.getName(), String.valueOf(field.get(this)));
            field.setAccessible(accessible);

        }));
        return map;
    }

    /**
     * 转成k=v形式的列表
     *
     * @return 列表
     */
    default List<String> getPairs() {
        Map<String, String> map = getFields();
        List<String> details = new ArrayList<>(map.size());
        map.forEach((k, v) -> details.add(k + "=" + v));
        return details;
    }
}

package io.gardenerframework.fragrans.data.persistence.orm.statement.schema.value;

import org.apache.commons.text.StringEscapeUtils;

/**
 * @author zhanghan30
 * @date 2022/6/15 5:13 下午
 */

public class TextValue extends RawValue<String> {
    public TextValue(String value) {
        super(value);
    }

    @Override
    public String build() {
        String value = getValue();
        return StringEscapeUtils.escapeJava(value);
    }
}

package io.gardenerframework.fragrans.log.schema.template;

/**
 * @author ZhangHan
 * @date 2022/6/9 0:39
 */

public abstract class AbstractGenericTemplate implements Template {
    private final String template;

    public AbstractGenericTemplate(int words) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words; i++) {
            builder.append("{}");
        }
        this.template = builder.toString();
    }

    @Override
    public String toString() {
        return template;
    }
}

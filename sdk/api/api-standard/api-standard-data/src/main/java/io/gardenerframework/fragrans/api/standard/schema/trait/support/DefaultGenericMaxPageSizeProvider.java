package io.gardenerframework.fragrans.api.standard.schema.trait.support;

/**
 * @author zhanghan30
 * @date 2022/6/23 12:54 下午
 */
public class DefaultGenericMaxPageSizeProvider implements GenericMaxPageSizeProvider {
    /**
     * 默认返回一页最大50
     *
     * @return 固定为50
     */
    @Override
    public Number getMax() {
        return 50L;
    }
}

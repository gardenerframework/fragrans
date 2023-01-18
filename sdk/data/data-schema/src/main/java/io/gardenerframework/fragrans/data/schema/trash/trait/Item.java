package io.gardenerframework.fragrans.data.schema.trash.trait;

/**
 * @author zhanghan30
 * @date 2022/6/14 4:28 下午
 */
public interface Item<I> {
    /**
     * 获取内容
     *
     * @return 内容
     */
    I getItem();

    /**
     * 设置内容物
     *
     * @param item 内容
     */
    void setItem(I item);
}

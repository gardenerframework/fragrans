package io.gardenerframework.fragrans.pattern.criteria.schema.object;

import io.gardenerframework.fragrans.pattern.criteria.schema.root.Criteria;

/**
 * @author zhanghan30
 * @date 2023/1/17 16:52
 */
public interface JavaObjectCriteria<O> extends Criteria {
    /**
     * 当前对象是否满足标准
     *
     * @param object 需要过滤的对象
     * @return 是否满足要求
     */
    boolean meetCriteria(O object);
}

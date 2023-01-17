package com.jdcloud.gardener.fragrans.api.options.persistence.schema;

import com.jdcloud.gardener.fragrans.data.trait.generic.GenericTraits;

import java.util.Map;

/**
 * 数据库存储的选项实体骨架
 *
 * @author zhanghan30
 * @date 2022/5/10 6:16 上午
 */

public interface ApiOptionRecordSkeleton extends GenericTraits.IdentifierTraits.Id<String> {
    /**
     * 获取选项
     *
     * @return 存储的选项
     */
    Map<String, Object> getOption();

    /**
     * 设置选项
     *
     * @param option 选项
     */
    void setOption(Map<String, Object> option);

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    String getVersionNumber();

    /**
     * 设置版本号
     *
     * @param versionNumber 版本号
     */
    void setVersionNumber(String versionNumber);
}

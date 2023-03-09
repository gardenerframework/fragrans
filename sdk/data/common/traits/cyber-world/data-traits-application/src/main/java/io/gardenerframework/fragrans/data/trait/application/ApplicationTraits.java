package io.gardenerframework.fragrans.data.trait.application;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/1/5 16:28
 */
public interface ApplicationTraits {
    /**
     * 应用的一些图片相关的特性
     */
    interface VisualTraits {
        @Trait
        class Logo {
            /**
             * 应用的logo图标
             */
            String logo;
        }

        @Trait
        class Icon {
            /**
             * 应用的图标
             */
            String icon;
        }
    }

    interface IdentifierTraits {
        /**
         * @author zhanghan30
         * @date 2022/8/11 6:50 下午
         */
        @Trait
        class ApplicationRelation<T> {
            /**
             * 关联的account id
             */
            private T applicationId;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/11 6:50 下午
         */
        @Trait
        class ApplicationRelations<T> {
            /**
             * 关联的account id
             */
            private Collection<T> applicationIds;
        }
    }
}

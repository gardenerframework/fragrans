package io.gardenerframework.fragrans.data.trait.generic;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;
import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/8/14 2:00 下午
 */
public interface GenericTraits {

    interface StatusTraits {
        /**
         * @author zhanghan30
         * @date 2022/8/13 6:14 下午
         */
        @Trait
        class EnableFlag {
            /**
             * 是否启用
             */
            private boolean enabled;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/13 6:17 下午
         */
        @Trait
        class LockFlag {
            /**
             * 是否被锁定
             */
            private boolean locked;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/13 6:17 下午
         */
        @Trait
        class ActiveFlag {
            /**
             * 是否被激活
             */
            private boolean active;
        }

        @Trait
        class SuccessFlag {
            /**
             * 是否成功
             */
            private boolean success;
        }
    }

    interface DatetimeTraits {
        @Trait
        class Timestamp {
            /**
             * 时间戳
             */
            private Date timestamp;
        }
    }

    interface GroupingTraits {
        /**
         * 类型
         *
         * @author zhanghan30
         * @date 2022/8/13 9:57 下午
         */
        @Trait
        class Type<T> {
            /**
             * 类型
             */
            private T type;
        }
    }

    interface IdentifierTraits {
        /**
         * 编码
         *
         * @author zhanghan30
         * @date 2022/8/13 9:57 下午
         */
        @Trait
        class Code<T> {
            /**
             * 类型
             */
            private T code;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/13 6:18 下午
         */
        @Trait
        class Id<T> {
            /**
             * id
             */
            private T id;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/13 6:18 下午
         */
        @Trait
        class Ids<T> {
            /**
             * id清单
             */
            private Collection<T> ids;
        }
    }

    interface LiteralTraits {
        /**
         * @author zhanghan30
         * @date 2022/8/13 6:35 下午
         */
        @Trait
        class Name {
            /**
             * 名字
             */
            private String name;
        }


        /**
         * @author zhanghan30
         * @date 2022/8/13 6:37 下午
         */
        @Trait
        class Comment {
            /**
             * 备注
             */
            private String comment;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/13 6:36 下午
         */
        @Trait
        class Description {
            /**
             * 描述
             */
            private String description;
        }
    }

}

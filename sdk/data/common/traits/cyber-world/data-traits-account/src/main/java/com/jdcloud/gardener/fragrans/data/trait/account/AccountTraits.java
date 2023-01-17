package com.jdcloud.gardener.fragrans.data.trait.account;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;
import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/8/14 2:04 下午
 */
public interface AccountTraits {
    interface IdentifierTraits {
        /**
         * 用户名
         */
        @Trait
        class Username {
            /**
             * 用户名
             */
            String username;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/11 6:50 下午
         */
        @Trait
        class AccountRelation<T> {
            /**
             * 关联的account id
             */
            private T accountId;
        }

        /**
         * @author zhanghan30
         * @date 2022/8/11 6:50 下午
         */
        @Trait
        class AccountRelations<T> {
            /**
             * 关联的account id
             */
            private Collection<T> accountIds;
        }
    }

    interface DatetimeTraits {
        /**
         * @author zhanghan30
         * @date 2022/8/11 6:31 下午
         */
        @Trait
        class AccountExpiryDate {
            /**
             * 账户过期时间
             */
            private Date accountExpiryDate;
        }
    }

    interface VisualTraits {
        @Trait
        class Avatar {
            /**
             * 头像
             * <p>
             * 或者可以是个链接，或者可以是个base64的图片数据串
             */
            private String avatar;
        }
    }

    interface LiteralTraits {
        @Trait
        class Nickname {
            /**
             * 昵称
             */
            private String nickname;
        }
    }
}

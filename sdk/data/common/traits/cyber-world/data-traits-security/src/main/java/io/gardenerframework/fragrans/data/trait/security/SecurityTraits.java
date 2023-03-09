package io.gardenerframework.fragrans.data.trait.security;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2022/8/14 2:04 下午
 */
public interface SecurityTraits {
    /**
     * 挑战与应答
     */
    interface ChallengeResponseTrait {
        @Trait
        class ChallengeId {
            /**
             * 挑战id
             */
            String challengeId;
        }

        @Trait
        class Response {
            /**
             * 应答
             */
            String response;
        }
    }

    /**
     * 图灵
     */
    interface TuringTraits {
        /**
         * 人机检测
         */
        @Trait
        class CaptchaToken {
            private String captchaToken;
        }
    }

    /**
     * 操作人
     */
    interface AuditingTraits {
        interface IdentifierTraits {
            /**
             * @author zhanghan30
             * @date 2022/8/13 6:19 下午
             */
            @Trait
            class Operator {
                /**
                 * 操作人
                 */
                private String operator;
            }

            /**
             * @author zhanghan30
             * @date 2022/8/13 6:19 下午
             */
            @Trait
            class Creator {
                /**
                 * 创建人
                 */
                private String creator;
            }

            /**
             * @author zhanghan30
             * @date 2022/8/13 6:19 下午
             */
            @Trait
            class Updater {
                /**
                 * 更新人
                 */
                private String updater;
            }

            /**
             * @author zhanghan30
             * @date 2022/8/13 6:19 下午
             */
            @Trait
            class Deleter {
                /**
                 * 删除人
                 */
                private String deleter;
            }
        }

        interface DatetimeTraits {

            /**
             *
             */
            @Trait
            class LastUpdateTime {
                /**
                 * 上一次更新时间
                 */
                private Date lastUpdateTime;
            }

            /**
             *
             */
            @Trait
            class CreatedTime {
                /**
                 * 创建时间
                 */
                private Date createdTime;
            }
        }
    }

    interface EncryptionTraits {
        @Trait
        class PrivateKey {
            /**
             * 私钥
             */
            private String privateKey;
        }

        @Trait
        class PublicKey {
            /**
             * 公钥
             */
            private String publicKey;
        }

        @Trait
        class SecretKey {
            /**
             * 对称加密密钥
             */
            private String secretKey;
        }
    }

    /**
     * 密码相关
     */
    interface SecretTraits {
        interface DatetimeTraits {
            /**
             * 密码过期时间
             *
             * @author zhanghan30
             * @date 2022/8/11 6:40 下午
             */
            @Trait
            class PasswordExpiryDate {
                /**
                 * 密码过期时间
                 */
                private Date passwordExpiryDate;
            }
        }

        /**
         * @author zhanghan30
         * @date 2022/8/11 6:40 下午
         */
        @Trait
        class Password {
            /**
             * 密码
             */
            private String password;
        }
    }
}

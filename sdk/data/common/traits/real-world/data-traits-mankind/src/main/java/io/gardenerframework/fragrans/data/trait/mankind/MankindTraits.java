package io.gardenerframework.fragrans.data.trait.mankind;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2023/1/5 16:44
 */
public interface MankindTraits {
    interface PersonalTraits {
        interface LiteralTraits {
            /**
             * @author zhanghan30
             * @date 2022/8/13 6:39 下午
             */
            @Trait
            class GivenName {
                /**
                 * 名
                 */
                private String givenName;
            }

            /**
             * @author zhanghan30
             * @date 2022/8/13 6:39 下午
             */
            @Trait
            class Surname {
                /**
                 * 姓
                 */
                private String surname;
            }
        }

        interface GroupingTraits {

            /**
             * 国籍
             *
             * @author zhanghan30
             * @date 2022/8/13 6:40 下午
             */
            @Trait
            class Nationality {
                /**
                 * 国家与地区编码。参考 ISO-3166
                 */
                private String nationality;
            }

            /**
             * 名族
             *
             * @author zhanghan30
             * @date 2022/8/13 6:46 下午
             */
            @Trait
            class EthnicGroup {
                /**
                 * 民族二字码，参考GB 3304
                 */
                private String ethnicGroup;
            }

            /**
             * @author zhanghan30
             * @date 2022/8/13 6:39 下午
             */
            @Trait
            class Gender {
                /**
                 * 性别编码，参考GB 2261
                 */
                private Integer gender;
            }
        }

        interface DatetimeTraits {

            /**
             * @author zhanghan30
             * @date 2022/8/13 6:43 下午
             */
            @Trait
            class DateOfBirth {
                /**
                 * 出生日期
                 */
                private Date dateOfBirth;
            }
        }

        interface IdentifierTraits {
            /**
             * 公民身份证号
             *
             * @author zhanghan30
             * @date 2022/8/13 6:51 下午
             */
            @Trait
            class CitizenIdentificationNumber {
                /**
                 * 公民身份证号
                 */
                private String citizenIdentificationNumber;
            }
        }
    }

    interface ContactTraits {
        /**
         * 电子邮箱地址
         *
         * @author zhanghan30
         * @date 2022/8/13 7:16 下午
         */
        @Trait
        class Email {
            /**
             * 邮箱地址
             */
            private String email;
        }

        /**
         * 传真号
         *
         * @author zhanghan30
         * @date 2022/8/13 7:16 下午
         */
        @Trait
        class Fax {
            /**
             * 传真号
             */
            private String fax;
        }

        /**
         * 手机号
         *
         * @author zhanghan30
         * @date 2022/8/13 7:16 下午
         */
        @Trait
        class MobilePhoneNumber {
            /**
             * 手机号
             */
            private String mobilePhoneNumber;
        }

        /**
         * 办公室座机号
         *
         * @author zhanghan30
         * @date 2022/8/13 7:16 下午
         */
        @Trait
        class OfficeTelephoneNumber {
            /**
             * 办公室座机号
             */
            private String officeTelephoneNumber;
        }
    }

    interface SnsTraits {
        interface IdentifierTraits {
            /**
             * 微信openid
             *
             * @author zhanghan30
             * @date 2022/8/13 7:16 下午
             */
            @Trait
            class WeChatOpenId {
                /**
                 * 邮箱地址
                 */
                private String weChatOpenId;
            }

            /**
             * 企业微信id
             */
            @Trait
            class EnterpriseWeChatOpenId {
                /**
                 * 邮箱地址
                 */
                private String enterpriseWeChatOpenId;
            }

            /**
             * 支付宝openid
             *
             * @author zhanghan30
             * @date 2022/8/13 7:16 下午
             */
            @Trait
            class AlipayOpenId {
                /**
                 * 邮箱地址
                 */
                private String alipayOpenId;
            }

            /**
             * 钉钉openid
             */
            @Trait
            class DingTalkOpenId {
                private String dingTalkOpenId;
            }

            /**
             * 飞书openid
             */
            @Trait
            class LarkOpenId {
                private String larkOpenId;
            }
        }
    }
}

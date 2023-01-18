package io.gardenerframework.fragrans.data.trait.network;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

/**
 * @author zhanghan30
 * @date 2023/1/5 15:29
 */
public interface NetworkTraits {
    interface TcpIpTraits {

        @Trait
        class Hostname {
            private String hostname;
        }

        @Trait
        class IpAddress {
            /**
             * ip地址
             */
            private String ip;
        }

        @Trait
        class Port {
            /**
             * 端口号
             */
            private int port;
        }
    }

    interface UrlTraits {
        @Trait
        class Url {
            private String url;
        }

        @Trait
        class Uri {
            private String uri;
        }

        @Trait
        class Scheme {
            private String url;
        }

        @Trait
        class QueryString {
            private String queryString;
        }

        @Trait
        class Fragment {
            private String fragment;
        }
    }
}

package io.gardenerframework.fragrans.data.trait.network;

import io.gardenerframework.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;
import java.util.Map;

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

    interface HttpTraits {
        @Trait
        interface Method {
            /**
             * http方法
             */
            String method = "";
        }

        @Trait
        interface Status {
            /**
             * http状态码
             */
            int status = 0;
        }

        @Trait
        interface Headers {
            /**
             * http头
             */
            Map<String, Collection<String>> headers = null;
        }

        @Trait
        interface RequestBody {
            /**
             * http请求体
             */
            String requestBody = null;
        }

        @Trait
        interface ResponseBody {
            /**
             * http响应体
             */
            String responseBody = null;
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

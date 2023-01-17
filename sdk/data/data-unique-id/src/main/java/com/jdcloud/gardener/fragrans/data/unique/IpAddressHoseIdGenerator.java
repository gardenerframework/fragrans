package com.jdcloud.gardener.fragrans.data.unique;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 基于ip地址生成主机id
 *
 * @author zhanghan
 * @date 2021/8/19 15:39
 */
@Slf4j
public class IpAddressHoseIdGenerator implements HostIdGenerator {
    /**
     * 给出主机的ip地址作为id使用
     *
     * @return 主机id
     */
    @Override
    public String getHostId() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            byte[] address = inetAddress.getAddress();
            int addressHigh = 0;
            int addressLow = 0;
            if (address.length == 4) {
                //进行无符号转换
                addressHigh = address[2] & 0x0FF;
                addressLow = address[3] & 0x0FF;
            } else if (address.length == 8) {
                addressHigh = address[6] & 0x0FF;
                addressLow = address[7] & 0x0FF;
            }
            return String.format("%03d%03d", addressHigh, addressLow);
        } catch (UnknownHostException exception) {
            throw new IllegalStateException(exception);
        }
    }
}

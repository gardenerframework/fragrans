package com.jdcloud.gardener.fragrans.data.unique;

import com.jdcloud.gardener.fragrans.data.unique.annotation.BusinessCode;
import com.jdcloud.gardener.fragrans.data.unique.exception.ClockTurnedBackException;
import com.jdcloud.gardener.fragrans.log.GenericLoggerStaticAccessor;
import com.jdcloud.gardener.fragrans.log.common.schema.state.Done;
import com.jdcloud.gardener.fragrans.log.common.schema.verb.Create;
import com.jdcloud.gardener.fragrans.log.schema.content.GenericOperationLogContent;
import com.jdcloud.gardener.fragrans.log.schema.details.Detail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 是一种改进了写雪花id生成算法，包含
 * <ul>
 *     <li>1个业务前缀字符</li>
 *     <li>14个日期与时间字符</li>
 *     <li>6个节点id字符，不足6个补0，超过6个报错</li>
 *     <li>6个序号字符</li>
 * </ul>
 * <p>
 * 在这样的设计下，预计每秒支持最大为100万个id
 *
 * @author zhanghan
 * @date 2021/8/19 14:42
 * @since 1.0.0
 */
@Slf4j
public class UniqueIdGenerator {
    /**
     * 指示业务字符为1个
     */
    private static final int BUSINESS_CHAR = 1;
    /**
     * yyyyMMDDhhmmss格式的字符串
     */
    private static final int DATETIME_CHAR = 14;
    /**
     * 节点字符数
     */
    private static final int NODE_ID_CHAR = 6;
    /**
     * 序号字符数
     */
    private static final int SEQUENCE_CHAR = 6;
    /**
     * 最大序号
     */
    private static final long MAX_SEQUENCE = tenPower(SEQUENCE_CHAR) - 1;
    /**
     * 当前节点id
     */
    private final String nodeId;
    /**
     * 上一次记录的时间戳
     */
    private volatile long lastSecond = -1L;
    /**
     * 每毫秒的计数器
     */
    private volatile long sequence = 0L;

    public UniqueIdGenerator(String nodeId) {
        if (!StringUtils.hasText(nodeId) || nodeId.length() > NODE_ID_CHAR) {
            throw new IllegalArgumentException("node id cannot be null and less than " + NODE_ID_CHAR + " char");
        }
        this.nodeId = org.apache.commons.lang3.StringUtils.leftPad(nodeId, 6, '0');
        GenericLoggerStaticAccessor.operationLogger().info(
                log,
                GenericOperationLogContent.builder()
                        .what(UniqueIdGenerator.class)
                        .operation(new Create())
                        .state(new Done())
                        .detail(new Detail() {
                            private String nodeId;

                            Detail nodeId(String nodeId) {
                                this.nodeId = nodeId;
                                return this;
                            }
                        }.nodeId(nodeId))
                        .build(),
                null
        );
    }

    /**
     * 算10的次方
     *
     * @param p 次方
     * @return 结果
     */
    private static long tenPower(int p) {
        long tenPower = 1;
        for (int i = 0; i < p; i++) {
            tenPower *= 10;
        }
        return tenPower;
    }

    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param object 带有@BusinessCode注解的对象
     * @return id
     */
    public synchronized String nextId(Object object) {
        Assert.notNull(object, "object must not be null");
        if (!(object instanceof CharSequence) || ((CharSequence) object).length() == 0) {
            return nextId(ClassUtils.getUserClass(object));
        } else {
            return nextId(((CharSequence) object).charAt(0));
        }
    }

    /**
     * @param objectClass 带有@BusinessCode注解的类
     * @return id
     */
    public synchronized String nextId(Class<?> objectClass) {
        Assert.notNull(objectClass, "objectClass must not be null");
        BusinessCode businessCode = AnnotationUtils.findAnnotation(objectClass, BusinessCode.class);
        Assert.notNull(businessCode, objectClass + " must with @BusinessCode annotation");
        return nextId(businessCode.value());
    }

    /**
     * 生成id
     *
     * @param business 业务前缀
     * @return id
     */
    public synchronized String nextId(char business) {
        Instant currentDatetime = Instant.now();
        long currentSecond = currentDatetime.getEpochSecond();
        //检查时钟是否回退
        if (currentSecond < lastSecond) {
            throw new ClockTurnedBackException("Invalid System Clock!");
        }
        //检查当前秒下序列号是否耗尽
        if (currentSecond == lastSecond) {
            sequence++;
            if (sequence > MAX_SEQUENCE) {
                currentSecond = waitNextSecond(currentSecond);
            }
        } else {
            //新的秒开始，序列号归0
            sequence = 0;
        }

        lastSecond = currentSecond;
        return String.format(
                "%c%s%s%06d",
                business,
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.ofInstant(currentDatetime, ZoneId.systemDefault())),
                nodeId,
                sequence
        );
    }

    /**
     * 等待到下一个秒
     *
     * @param currentTimestamp 当前时间戳
     * @return 等待后的时间戳
     */
    private long waitNextSecond(long currentTimestamp) {
        while (currentTimestamp == lastSecond) {
            currentTimestamp = Instant.now().getEpochSecond();
        }
        return currentTimestamp;
    }
}

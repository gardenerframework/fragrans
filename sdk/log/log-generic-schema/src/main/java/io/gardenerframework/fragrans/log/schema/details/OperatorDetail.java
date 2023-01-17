package io.gardenerframework.fragrans.log.schema.details;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ZhangHan
 * @date 2022/6/9 1:02
 */
@AllArgsConstructor
@Getter
public class OperatorDetail implements Detail {
    /**
     * 用户
     * <p>
     * 这是个Object，也就是可以把用户对象传进来
     */
    private final Object user;
    /**
     * 客户端
     * <p>
     * 一样，可以把客户端传进来
     */
    private final Object client;
}

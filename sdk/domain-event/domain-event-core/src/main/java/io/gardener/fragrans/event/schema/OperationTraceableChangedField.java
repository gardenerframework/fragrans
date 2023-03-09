package com.jdcloud.gardener.fragrans.event.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * @author zhanghan30
 * @date 2021/11/5 3:21 下午
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class OperationTraceableChangedField<F> {
    private F value;
    private String updater;
    private Date lastUpdateTime;
}

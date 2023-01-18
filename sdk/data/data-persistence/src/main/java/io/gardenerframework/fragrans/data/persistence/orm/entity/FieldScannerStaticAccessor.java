package io.gardenerframework.fragrans.data.persistence.orm.entity;

import org.springframework.stereotype.Component;

/**
 * @author zhanghan30
 * @date 2022/9/24 01:25
 */
@Component
public class FieldScannerStaticAccessor {

    /**
     * 实际的builder
     */
    private static final FieldScanner scanner = new FieldScanner();


    public static FieldScanner scanner() {
        return scanner;
    }
}

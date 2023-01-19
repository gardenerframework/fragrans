package io.gardenerframework.fragrans.data.persistence.orm.statement.exception;

import lombok.Getter;
import org.springframework.boot.jdbc.DatabaseDriver;


@Getter
public class UnsupportedDriverException extends UnsupportedOperationException {
    private final DatabaseDriver driver;

    public UnsupportedDriverException(DatabaseDriver driver) {
        super(driver + " is not supported");
        this.driver = driver;
    }
}

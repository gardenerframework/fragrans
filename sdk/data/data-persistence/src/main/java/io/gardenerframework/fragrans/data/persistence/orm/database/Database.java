package io.gardenerframework.fragrans.data.persistence.orm.database;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.jdbc.DatabaseDriver;

@Setter
@Getter
public class Database {
    /**
     * 驱动，默认是mysql
     */
    @NonNull
    @Setter
    @Getter
    private static DatabaseDriver driver = DatabaseDriver.MYSQL;
}

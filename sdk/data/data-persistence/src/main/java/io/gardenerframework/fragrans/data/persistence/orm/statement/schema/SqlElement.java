package io.gardenerframework.fragrans.data.persistence.orm.statement.schema;

import io.gardenerframework.fragrans.data.persistence.orm.database.Database;
import io.gardenerframework.fragrans.data.persistence.orm.statement.exception.UnsupportedDriverException;
import org.springframework.boot.jdbc.DatabaseDriver;

/**
 * @author zhanghan30
 * @date 2022/6/14 7:08 下午
 */
public interface SqlElement {
    /**
     * 给出元素对应的语句字符串
     *
     * @return 语句字符串
     */
    String build();


    /**
     * 为字段加上mysql的``或者sql sever的[]这种界定符
     *
     * @param element 要加的东西
     * @return 加完的结果
     */
    default String addDelimitIdentifier(String element) {
        String startingDelimitIdentifier;
        String endingDelimitIdentifier;
        DatabaseDriver driver = Database.getDriver();
        switch (driver) {
            case MYSQL:
                startingDelimitIdentifier = "`";
                endingDelimitIdentifier = "`";
                break;
            case SQLSERVER:
                startingDelimitIdentifier = "[";
                endingDelimitIdentifier = "]";
                break;
            default:
                throw new UnsupportedDriverException(driver);
        }
        return String.format("%s%s%s", startingDelimitIdentifier, element, endingDelimitIdentifier);
    }
}

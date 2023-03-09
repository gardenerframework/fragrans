package io.gardenerframework.fragrans.data.persistence.test.utils.fieldTest;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gardenerframework.fragrans.data.persistence.orm.handler.JsonTypeHandler;
import io.gardenerframework.fragrans.data.persistence.orm.mapping.annotation.ColumnMapping;
import io.gardenerframework.fragrans.data.persistence.orm.mapping.annotation.ColumnTypeHandler;
import io.gardenerframework.fragrans.data.persistence.orm.mapping.annotation.ColumnTypeHandlerProvider;
import org.apache.ibatis.annotations.*;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * @author zhanghan30
 * @date 2022/9/25 01:53
 */
@Mapper
public interface JsonTestDao {
    @Insert("insert into `json_test` (`primitive`, `list`, `map`, `object`) values(#{object.primitive}, #{object.list}, #{object.map}, #{object.object})")
    void create(@Param("object") JsonTestObject object);

    @Select("select * from `json_test`")
    @ColumnTypeHandler(provider = ListFieldProvider.class)
    Collection<JsonTestObject> read();

    @Delete("delete from `json_test`")
    void delete();

    class ListFieldProvider extends JsonTypeHandler<List<JsonTestObject.Nested>> implements ColumnTypeHandlerProvider {
        @Override
        public Type getTypeReference() {
            return new TypeReference<List<JsonTestObject.Nested>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            }.getType();
        }

        @Override
        public ColumnMapping provide(Class<?> mapperInterface, Method mapperMethod) {
            return new ColumnMapping(
                    "list",
                    "list",
                    new ListFieldProvider(),
                    Collection.class
            );
        }
    }
}

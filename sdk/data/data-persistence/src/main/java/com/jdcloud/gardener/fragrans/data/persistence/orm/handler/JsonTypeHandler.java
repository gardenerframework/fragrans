package com.jdcloud.gardener.fragrans.data.persistence.orm.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Setter;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * @author zhanghan30
 * @date 2021/10/22 6:12 下午
 */
public class JsonTypeHandler<J> extends BaseTypeHandler<J> {
    /**
     * 大量的情况是以行为单位组成的json，这时候mysql的时间格式不是iso08601
     * <p>
     * 这个mapper就是用来处理从数据行组成的json
     */
    public static final ObjectMapper RECORD_ROW_OBJECT_MAPPER;

    static {
        RECORD_ROW_OBJECT_MAPPER = new ObjectMapper();
        //设置时间格式
        RECORD_ROW_OBJECT_MAPPER.setDateFormat(
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        );
    }

    @Setter(AccessLevel.PROTECTED)
    private ObjectMapper objectMapper;

    public JsonTypeHandler() {
        objectMapper = ObjectMapperAccessor.getObjectMapper();
    }

    public JsonTypeHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 获取反序列化的类型参考
     *
     * @return 参考类型
     */
    public Type getTypeReference() {
        return getRawType();
    }

    /**
     * 将字符串转成对象
     *
     * @param json json字符串
     * @return 对象
     */
    protected J convertJsonToObject(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            Type typeReference = getTypeReference();
            return objectMapper.readValue(json, new TypeReference<J>() {
                @Override
                public Type getType() {
                    return typeReference;
                }
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(json + "is not a valid json", e);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, J parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(parameter + "is not a valid json", e);
        }
    }

    @Override
    public J getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertJsonToObject(rs.getString(columnName));
    }

    @Override
    public J getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertJsonToObject(rs.getString(columnIndex));
    }

    @Override
    public J getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertJsonToObject(cs.getString(columnIndex));
    }

    @Component
    private static class ObjectMapperAccessor {
        private static ObjectMapper objectMapper = null;

        public ObjectMapperAccessor(ObjectMapper objectMapper) {
            ObjectMapperAccessor.objectMapper = objectMapper;
        }

        public static ObjectMapper getObjectMapper() {
            return objectMapper == null ? new ObjectMapper() : objectMapper;
        }
    }
}

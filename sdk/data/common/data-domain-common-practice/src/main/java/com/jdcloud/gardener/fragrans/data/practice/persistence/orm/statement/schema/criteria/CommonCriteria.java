package com.jdcloud.gardener.fragrans.data.practice.persistence.orm.statement.schema.criteria;

import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.criteria.EqualsCriteria;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.FieldNameValue;
import com.jdcloud.gardener.fragrans.data.persistence.orm.statement.schema.value.ParameterNameValue;
import com.jdcloud.gardener.fragrans.data.schema.entity.BasicEntity;

/**
 * @author zhanghan30
 * @date 2022/6/16 5:06 下午
 */
public interface CommonCriteria {

    /**
     * 按id查询 id` = #{id} 或使用方给一个参数的名称
     */
    class QueryByIdCriteria extends EqualsCriteria {
        private static final String ID_COLUMN_NAME = "id";
        public static final String ID_PARAMETER_NAME = ID_COLUMN_NAME;

        static {
            try {
                BasicEntity.class.getDeclaredField(ID_COLUMN_NAME);
            } catch (NoSuchFieldException exception) {
                throw new IllegalStateException(exception);
            }
        }

        /**
         * 构建一个id判等条件
         */
        public QueryByIdCriteria() {
            this(ID_PARAMETER_NAME);
        }

        /**
         * 构建一个id判等条件
         *
         * @param idParameterName id对应的参数名
         */
        public QueryByIdCriteria(String idParameterName) {
            super(ID_COLUMN_NAME, new ParameterNameValue(idParameterName));
        }

        /**
         * 构建一个id判等条件
         *
         * @param entityParameterName 实体名称
         * @param entityField         实体字段
         */
        public QueryByIdCriteria(String entityParameterName, String entityField) {
            super(ID_COLUMN_NAME, new FieldNameValue(entityParameterName, entityField));
        }
    }
}

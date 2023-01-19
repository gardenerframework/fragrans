package io.gardenerframework.fragrans.api.standard.schema.trait;

import io.gardenerframework.fragrans.data.trait.generic.GenericTraits;
import io.gardenerframework.fragrans.data.trait.structure.DataStructureTraits;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2022/8/22 5:06 下午
 */
public interface ApiStandardDataTraits {
    /**
     * @author zhanghan30
     * @date 2022/6/22 5:56 下午
     */
    interface Contents<C> extends DataStructureTraits.CollectionTraits.Contents<C> {

    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 6:17 下午
     */
    interface Id<T> extends GenericTraits.IdentifierTraits.Id<T> {
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 6:18 下午
     */
    interface Ids<T> {
        /**
         * id清单
         *
         * @return id清单
         */
        Collection<T> getIds();

        /**
         * id清单
         *
         * @param ids 设置id清单
         */
        void setIds(Collection<T> ids);
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 6:47 下午
     */
    interface Keyword {
        /**
         * 搜索关键词
         *
         * @return 关键词
         */
        String getKeyword();

        /**
         * 设置关键词
         *
         * @param keyword 关键词
         */
        void setKeyword(String keyword);
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 5:59 下午
     */
    interface PageMarker {
        /**
         * 页签
         *
         * @return 页签
         */
        @Nullable
        String getMarker();

        /**
         * 设置页签
         *
         * @param marker 页签
         */
        void setMarker(@Nullable String marker);
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 5:48 下午
     */
    interface PageNo {
        /**
         * 页码
         *
         * @return 页码
         */
        Integer getPageNo();

        /**
         * 设置页码
         *
         * @param pageNo 页码
         */
        void setPageNo(Integer pageNo);
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 5:48 下午
     */
    interface PageSize {
        /**
         * 返回页大小
         *
         * @return 页大小
         */
        Integer getPageSize();

        /**
         * 设置页大小
         *
         * @param pageSize 页大小
         */
        void setPageSize(Integer pageSize);
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 6:19 下午
     */
    interface Sort {
        /**
         * 获得排序列
         *
         * @return 排序列
         */
        String getSort();

        /**
         * 设置排序列
         *
         * @param sort 排序列
         */
        void setSort(String sort);

        /**
         * 顺序
         *
         * @return 顺序
         */
        Order getOrder();

        /**
         * 设置顺序
         *
         * @param order 顺序
         */
        void setOrder(Order order);

        /**
         * 排序的类型
         */
        enum Order {
            //升序
            ASC("asc"),
            //降序
            DESC("desc");

            @Getter
            private final String value;

            Order(String value) {
                this.value = value;
            }
        }
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 6:53 下午
     */
    interface Sorts {
        /**
         * 排序列清单
         *
         * @return 列清单
         */
        Collection<Sort> getSorts();

        /**
         * 设置排序列清单
         *
         * @param sorts 列清单
         */
        void setSorts(Collection<Sort> sorts);
    }

    /**
     * @author zhanghan30
     * @date 2022/6/22 6:04 下午
     */
    interface TotalNumber extends DataStructureTraits.CollectionTraits.TotalNumber {
    }
}

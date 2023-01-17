package com.jdcloud.gardener.fragrans.data.trait.structure;

import com.jdcloud.gardener.fragrans.sugar.trait.annotation.Trait;

import java.util.Collection;

/**
 * @author zhanghan30
 * @date 2023/1/5 17:11
 */
public interface DataStructureTraits {
    interface TreeTraits {
        interface IdentifierTraits {
            /**
             * 族系关系
             *
             * @param <T>
             */
            @Trait
            class Patriarch<T> {
                /**
                 * 层级的族长
                 * <p>
                 * 部分情况下，子部门需要一个指向跨多层上级部门的指针
                 * <p>
                 * 这个上级部门意味着子节点所属的族系
                 * <p>
                 * 比如同一个公司下的多个不同层级的部门
                 */
                private T patriarch;
            }

            /**
             * 上下级关系
             *
             * @param <T> 上级类型
             */
            @Trait
            class Parent<T> {
                /**
                 * 上级id
                 */
                private T parent;
            }

            /**
             * 下级类型
             *
             * @param <T>
             */
            @Trait
            class Children<T> {
                /**
                 * 子元素
                 */
                private Collection<T> children;
            }
        }

        @Trait
        class GenericPath {
            /**
             * 路径
             */
            private String path;
        }

        /**
         * 族系路径
         */
        @Trait
        class PatriarchPath {
            /**
             * 族长路径，有区域于父节点的路径
             * <p>
             * 该路径只追踪族长
             */
            private String patriarchPath;
        }

        @Trait
        class NodePath {
            /**
             * 节点路径
             */
            private String nodePath;
        }
    }

    interface CollectionTraits {
        /**
         * 内容
         *
         * @param <C> 内容类型
         */
        @Trait
        class Contents<C> {
            /**
             * 内容类型
             */
            private Collection<C> contents;
        }

        /**
         * 总数
         */
        @Trait
        class TotalNumber {
            /**
             * 总数
             */
            private Long total;
        }
    }
}

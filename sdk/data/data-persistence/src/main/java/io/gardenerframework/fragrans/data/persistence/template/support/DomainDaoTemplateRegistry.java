package io.gardenerframework.fragrans.data.persistence.template.support;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZhangHan
 * @date 2022/11/2 10:32
 */
public class DomainDaoTemplateRegistry {
    private static final Map<Class<?>, Item<?>> registry = new ConcurrentHashMap<>();

    private DomainDaoTemplateRegistry() {

    }

    /**
     * 注册模板与实现
     *
     * @param template       模板
     * @param implementation 实现
     */
    @SuppressWarnings("unchecked")
    public static <T> void addItem(@NonNull Class<?> template, @NonNull Class<?> implementation, boolean active) {
        Assert.isTrue(template.isAssignableFrom(implementation), implementation + " is not a subclass of " + template);
        Item<T> item = (Item<T>) registry.get(template);
        if (item == null) {
            item = new Item<>();
            registry.put(template, item);
        }
        item.getImplementations().add((Class<? extends T>) implementation);
        if (active) {
            //更改激活的实现类型
            item.setActiveImplementation((Class<? extends T>) implementation);
        }
    }

    /**
     * 获取条目
     *
     * @param template 模板类型
     * @return 条目
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Item<T> getItem(@NonNull Class<T> template) {
        return (Item<T>) registry.get(template);
    }

    @Getter
    public static class Item<T> {
        private final Collection<Class<? extends T>> implementations = new HashSet<>();
        @Setter
        private Class<? extends T> activeImplementation;

        public Class<? extends T> getActiveImplementation() {
            if (activeImplementation != null) {
                return activeImplementation;
            }
            if (implementations.size() != 1) {
                throw new IllegalStateException("cannot determine active implementation: no active implementation given and found multiple implementations " + implementations + ", annotate @Primary on which should be active");
            }
            return new LinkedList<>(implementations).get(0);
        }
    }
}

package io.gardenerframework.fragrans.sugar.trait.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * @author ZhangHan
 * @date 2022/11/28 15:14
 */
public abstract class TraitUtils {
    private TraitUtils() {

    }

    /**
     * 判断当前类型是否是个trait
     *
     * @param type 类型
     * @return 是否是trait(只有getter / setter的接口)
     */
    public static boolean isTrait(@NonNull Class<?> type) {
        TraitChecker traitChecker = new TraitChecker();
        visitMethod(type, traitChecker);
        return !traitChecker.isNonGetterSetterFound();
    }

    /**
     * 判断这不是一个field trait，也就是只有一个字段的trait
     *
     * @param type 类型
     * @return 是否是 field trait
     */
    public static boolean isTraitField(@NonNull Class<?> type) {
        Collection<String> traitFieldNames = getTraitFieldNames(type);
        return traitFieldNames.size() == 1;
    }

    /**
     * 如果是getter/setter，则返回字段名称，否则返回null
     *
     * @param method 方法
     * @return 字段名称
     */
    @Nullable
    public static String getGetterSetterFieldName(@NonNull Method method) {
        String methodName = method.getName();
        if ((methodName.startsWith("get") && !void.class.equals(method.getReturnType()))
                //void的set方法
                || (methodName.startsWith("set") && void.class.equals(method.getReturnType()))
                || (methodName.startsWith("is") && boolean.class.equals(method.getReturnType()))) {
            String fieldName = null;
            if (methodName.startsWith("get")) {
                fieldName = methodName.substring("get".length());
            }
            if (methodName.startsWith("set")) {
                fieldName = methodName.substring("set".length());
            }
            if (methodName.startsWith("is")) {
                fieldName = methodName.substring("is".length());
            }
            //不是没有变量名，比如 boolean is() 这种
            if (StringUtils.hasText(fieldName)) {
                //首字母小写
                return Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
            }
        }
        return null;
    }

    /**
     * 获取字段类型清单
     *
     * @param type 类型
     * @return 字段清单
     */
    public static Collection<String> getTraitFieldNames(@NonNull Class<?> type) {
        GetterSetterVisitor getterSetterVisitor = new GetterSetterVisitor();
        if (isTrait(type)) {
            visitMethod(type, getterSetterVisitor);
        }
        return getterSetterVisitor.getFields();
    }


    /**
     * 遍历方法
     *
     * @param type     类型
     * @param consumer 方法消费者
     */
    private static void visitMethod(@NonNull Class<?> type, Consumer<Method> consumer) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException(type + " must be a interface");
        }
        for (Method method : type.getMethods()) {
            consumer.accept(method);
        }
    }


    /**
     * 检查方法是否包含了不是getter/setter的
     */
    private static class TraitChecker implements Consumer<Method> {
        @Getter
        private boolean nonGetterSetterFound = false;

        @Override
        public void accept(Method method) {
            String fieldName = getGetterSetterFieldName(method);
            //当前方法找不到getter/setter的字段名，判断出当前不是个trait
            if (!StringUtils.hasText(fieldName)) {
                nonGetterSetterFound = true;
            }
        }
    }

    @RequiredArgsConstructor
    private static class GetterSetterVisitor implements Consumer<Method> {
        @Getter
        private final Collection<String> fields = new HashSet<>();

        @Override
        public void accept(Method method) {
            //不是void的get方法
            String fieldName = getGetterSetterFieldName(method);
            if (StringUtils.hasText(fieldName)) {
                fields.add(fieldName);
            }
        }
    }
}

package io.gardenerframework.fragrans.api.group.registry;

import io.gardenerframework.fragrans.api.group.ApiGroupProvider;
import io.gardenerframework.fragrans.api.group.configuration.ApiGroupComponent;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicy;
import io.gardenerframework.fragrans.api.group.policy.ApiGroupPolicyProvider;
import io.gardenerframework.fragrans.log.GenericLoggers;
import io.gardenerframework.fragrans.log.GenericOperationLogger;
import io.gardenerframework.fragrans.log.common.schema.state.Done;
import io.gardenerframework.fragrans.log.common.schema.verb.Register;
import io.gardenerframework.fragrans.log.schema.content.GenericOperationLogContent;
import io.gardenerframework.fragrans.log.schema.details.Detail;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * api 分组注册表
 *
 * @author ZhangHan
 * @date 2022/5/10 19:07
 */
@ApiGroupComponent
@RequiredArgsConstructor
@Slf4j
public class ApiGroupRegistry implements InitializingBean, ApplicationContextAware {
    /**
     * 提供成员
     */
    private final Collection<ApiGroupProvider> groupProviders;
    /**
     * 提供组策略
     */
    private final Collection<ApiGroupPolicyProvider> policyProviders;
    /**
     * 记日志的
     */
    private final GenericOperationLogger operationLogger = GenericLoggers.operationLogger();
    /**
     * 支持从注解找到所有组成员
     */
    private Map<Class<? extends Annotation>, Collection<Class<?>>> annotationBasedGroupMembers = new ConcurrentHashMap<>();
    /**
     * 支持从注解找到所有组策略
     */
    private Map<Class<? extends Annotation>, Map<Class<? extends ApiGroupPolicy>, ApiGroupPolicy>> annotationBasedGroupPolicies = new ConcurrentHashMap<>();
    /**
     * 支持从组策略类找到所有组成员
     */
    private Map<Class<? extends ApiGroupPolicy>, Collection<Class<?>>> policyClassBasedGroupMembers = new ConcurrentHashMap<>();
    /**
     * 支持从组成员找到策略
     */
    private Map<Class<?>, Map<Class<? extends ApiGroupPolicy>, ApiGroupPolicy>> groupMemberBasedPolicies = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        //记录一下哪些成员出现过
        Set<Class<?>> presentedMembers = new HashSet<>();
        //先获取编组
        groupProviders.forEach(
                apiGroupProvider -> {
                    Class<? extends Annotation> annotation = apiGroupProvider.getAnnotation();
                    Assert.isTrue(!annotationBasedGroupMembers.containsKey(annotation), "annotation " + annotation + " was already presented");
                    //读取注解对应的所有bean
                    Arrays.asList(applicationContext.getBeanNamesForAnnotation(annotation)).forEach(
                            beanName -> {
                                annotationBasedGroupMembers.putIfAbsent(annotation, new HashSet<>());
                                //将bean对应的类放进去
                                Class<?> member = applicationContext.getType(beanName);
                                Assert.notNull(member, beanName + " had no type");
                                member = ClassUtils.getUserClass(member);
                                //确认这个类型没有出现过
                                Assert.isTrue(!presentedMembers.contains(member), "member " + member + " was already presented");
                                presentedMembers.add(member);
                                annotationBasedGroupMembers.get(annotation).add(member);
                            }
                    );
                    //添加额外成员
                    Collection<Class<?>> additionalMembers = apiGroupProvider.getAdditionalMembers();
                    if (!CollectionUtils.isEmpty(additionalMembers)) {
                        additionalMembers.forEach(
                                additionalMember -> {
                                    //fixed 之前取的不是单个组成员的类，而是整个列表的类
                                    Class<?> userClass = ClassUtils.getUserClass(additionalMember);
                                    //确认这个类型没有出现过
                                    Assert.isTrue(!presentedMembers.contains(userClass), "member " + userClass + " was already presented");
                                    presentedMembers.add(userClass);
                                    annotationBasedGroupMembers.get(annotation).add(userClass);
                                }
                        );
                    }
                    operationLogger.info(
                            log,
                            GenericOperationLogContent.builder()
                                    .what(annotation)
                                    .operation(new Register())
                                    .state(new Done())
                                    .detail(new GroupMemberDetail(annotationBasedGroupMembers.get(annotation)))
                                    .build(),
                            null
                    );
                }
        );
        //获取所有组策略
        policyProviders.forEach(
                apiGroupPolicyProvider -> {
                    Class<? extends Annotation> annotation = apiGroupPolicyProvider.getAnnotation();
                    //这个注解得有组成员
                    Collection<Class<?>> members = annotationBasedGroupMembers.get(annotation);
                    //fix 后续允许组成员为空，为空就不设置各种策略了
                    //Assert.isTrue(!CollectionUtils.isEmpty(members), "no group member for annotation " + annotation);
                    annotationBasedGroupPolicies.putIfAbsent(annotation, new ConcurrentHashMap<>());
                    ApiGroupPolicy policy = apiGroupPolicyProvider.getPolicy();
                    Assert.notNull(policy, "policy must not be null");
                    //将策略添加到注解名下
                    annotationBasedGroupPolicies.get(annotation).put(policy.getClass(), policy);
                    //设置策略对应这些类
                    //fix 这里如果有多个同类型的策略，则后面的注解的策略的成员会覆盖之前设置好的
                    //policyClassBasedGroupMembers.put(policy.getClass(), member);
                    policyClassBasedGroupMembers.putIfAbsent(policy.getClass(), new HashSet<>());
                    //设置组成员对应这些类
                    if (!CollectionUtils.isEmpty(members)) {
                        //fix 之前放到了上面引发了NPE
                        policyClassBasedGroupMembers.get(policy.getClass()).addAll(members);
                        members.forEach(
                                clazz -> {
                                    groupMemberBasedPolicies.putIfAbsent(clazz, new ConcurrentHashMap<>());
                                    groupMemberBasedPolicies.get(clazz).put(policy.getClass(), policy);
                                }
                        );
                    }
                    operationLogger.info(
                            log,
                            GenericOperationLogContent.builder()
                                    .what(policy.getClass())
                                    .operation(new Register())
                                    .state(new Done())
                                    .detail(new GroupMemberDetail(members))
                                    .build(),
                            null
                    );
                }
        );
    }

    /**
     * 获取注解对应的成员
     *
     * @param annotationOrPolicy 注解或策略类
     * @return 成员
     */
    @Nullable
    public Collection<Class<?>> getMember(Class<?> annotationOrPolicy) {
        if (Annotation.class.isAssignableFrom(annotationOrPolicy)) {
            return annotationBasedGroupMembers.get(annotationOrPolicy);
        } else if (ApiGroupPolicy.class.isAssignableFrom(annotationOrPolicy)) {
            return policyClassBasedGroupMembers.get(annotationOrPolicy);
        }
        return null;
    }

    /**
     * 获取给定成员的给定策略
     *
     * @param member 成员
     * @param policy 策略
     * @return 策略
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <P extends ApiGroupPolicy> P getPolicy(Class<?> member, Class<P> policy) {
        Map<Class<? extends ApiGroupPolicy>, ApiGroupPolicy> policies = groupMemberBasedPolicies.get(member);
        return policies == null ? null : (P) policies.get(policy);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @AllArgsConstructor
    private class GroupMemberDetail implements Detail {
        private final Collection<Class<?>> members;
    }
}

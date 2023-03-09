# 接口切面简化

在部分情况下开发人员需要对api接口进行全局的切面来完成一些操作，比如计算每一个接口的调用时间，或者基于参数的注解完成一些全局操作，于是"api-advice-engine"就引入并实现这种需求

## EndpointHandlerMethodAdvice接口

```java
public interface EndpointHandlerMethodAdvice {
    /**
     * 在controller的端点方法执行前
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void before(Object target, MethodSignature methodSignature, Object[] arguments) throws Exception {
    }

    /**
     * 在controller的端点方法执行后
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param returnValue     返回值
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void after(Object target, MethodSignature methodSignature, Object[] arguments, @Nullable Object returnValue) throws Exception {
    }

    /**
     * 在捕捉到异常时
     *
     * @param target          目标对象
     * @param methodSignature 方法签名
     * @param arguments       参数
     * @param exception       捕捉到了什么异常
     * @throws Exception 可能抛出异常中断后续执行
     */
    default void fail(Object target, MethodSignature methodSignature, Object[] arguments, Exception exception) throws Exception {

    }
}
```

实现这个接口并声明为bean，这样当controller的方法触发时就会按照接口的说明分别调用`before`、`after`和`fail`

## EndpointHandlerMethodBeforeAdviceAdapter

这个接口使得开发人员可以实现一个和要切入的controller方法的声明一样的方法，当controller的方法发动时，引擎会匹配被调用的方法在子类中是否存在，如果存在就调用否则忽略

## 用例: 通过切面判断用户权限

* 第一步: 编写controller类

```java
/**
 * 首先定义接口骨架(其实可以用idea的refactor功能从controller类抽取接口完成)
 */
public interface UserManagementEndpointSkeleton {
    /**
     * 作为管理员创建用户
     * @param createUserParameter 参数
     * @return 创建结果，包含用户的id
     */
    CreateUserResponse createUser(@Valid CreateUserParameter createUserParameter);
}

@RequestMapping("/user")
@RestController
@Component
public class UserManagementEndpoint implements UserManagementEndpointSkeleton {
    @Override
    @PostMapping
    public CreateUserResponse createUser(CreateUserParameter createUserParameter) {
        //完成用户创建
        String userId = userService.createUser(modelMapper.map(createUserParameter, User.class));
        //返回结果
        return new CreateUserResponse(userId);
    }
}
```

从上面的接口可以看出，`createUser`方法中没有任何权限判断逻辑

* 第二步，编写切面

```java

@Component
public class UserManagementEndpointSecurityHandler extends EndpointHandlerMethodBeforeAdviceAdapter implements UserManagementEndpointSkeleton {
    @Override
    public CreateUserResponse createUser(CreateUserParameter createUserParameter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Principal principal = authentication.getPrincipal();
        if (!principal.getRole.contains("ADMIN")) {
            throw new ForbiddenException("user " + principal.getUsername() + " is not ADMIN role");
        }
    }
}
```

这样，在调用controller的创建用户前，就会先调用切面的同名方法。这种模式显然有利于提高代码的可维护性。
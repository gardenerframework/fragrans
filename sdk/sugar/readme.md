# 引言

本工程主要负责开发过程中的一些语法糖的发放

# trait

trait包含了注解以及注解处理器，其主要提供将java pojo转为只有getter和setter的接口的能力，如

```java

@Trait
public class SampleTraitClass {
    private String field;
}

@Trait
public interface SampleTraitInterface {
    String field = "";
}
```

将转换为

```java

public interface SampleTraitClass {
    String getField();

    void setField(String field);
}


public interface SampleTraitInterface {
    String getField();

    void setField(String field);
}
```

Trait构成一种能够被编译器检查的属性名称和类型约定，当VO和PO由2个不同的人编写时，可以引用同样的trait jar，并声明实现同样的"
Trait"，从而在编译上就避免了双方因为语言沟通导致的问题，比如

* 甲: "属性叫用户名，类型是字符串"

```java
public class UserVo {
    /**
     * 这里其实
     */
    private String username;
}
```

* 乙: "好"

```java
public class UserPo {
    /**
     * 这里其实
     */
    private String userName;
}
```

然后两个人查空指针查了一天。利用trait，改进为

```java

@Trait
public interface Username {
    String username;
}

public class UserVo implements Username {
    /**
     * 这里对了
     */
    private String username;
}

public class UserPo implements Username {
    /**
     * 这里ide就提示编译报错了
     */
    private String userName;
}
```

最后，推荐的方法是按照属性，每一个属性为一个"ElementalTrait"，如果一个Trait要包含多个属性，则由多个"ElementalTrait"
接口组合为一个聚合接口

* lang

提供一些java语言上的语法糖，目前主要是`RewriteReturnValueType`
。这个语法糖的主要场景是当编写一些类级别的切面时，可能希望按照原方法的signature进行方法路由。比如切入一个"Controller"
类，并希望在每一个接口方法执行前做一些权限判

```java
public class SampleController {
    @GetMapping
    public ReadUserResponse readUser() {

    }

    @PostMapping
    public CreateUserResponse createUser() {

    }

    @PutMapping
    public UpdateUserResponse updateUser() {

    }

    @DeleteMapping
    public DeleteUserResponse deleteUser() {

    }
}
```

既然是权限判断，则以上4个方法肯定各自有各自的逻辑，比如读取的时候可能除去权限外还要检查能够读取哪些数据字段，是否要脱敏等。这样，如果只用一个类级别的切面就需要按照方法名进行判断。开发人员自然希望能够这样

```java
public class SampleControllerAdvice extends SampleController {
    @Override
    public ReadUserResponse readUser() {
        //权限逻辑
        return null;
    }

    @Override
    public CreateUserResponse createUser() {
        //权限逻辑
        return null;
    }

    @Override
    public UpdateUserResponse updateUser() {
        //权限逻辑
        return null;
    }

    @Override
    public DeleteUserResponse deleteUser() {
        //权限逻辑
        return null;
    }
}
```

但是作为一个执行的前置检查，返回值显然是不必要的，这时就可以

```java

@RewriteReturnValueType(void.class)
public class SampleControllerAdvice extends SampleController {
    @Override
    public ReadUserResponse readUser() {
        //权限逻辑
    }

    @Override
    public CreateUserResponse createUser() {
        //权限逻辑
    }

    @Override
    public UpdateUserResponse updateUser() {
        //权限逻辑
    }

    @Override
    public DeleteUserResponse deleteUser() {
        //权限逻辑
    }
}
```

从而使得所有方法的返回值是void，不需要再写"return null"这种也不会编译报错
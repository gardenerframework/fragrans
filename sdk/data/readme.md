# 引言

data组件主要集合和与数据相关的定义和操作

* [common](common): 负责与数据定义，持久化和操作相关的共用代码定义
* [data-schema](data-schema): 负责标准化的数据定义，主要是与数据持久化相关的数据定义
* [data-cache-core](data-cache-core): 负责进行数据缓存的操作
* [data-persistence](data-persistence): 负责数据持久化的一些常见的orm扫描等定义，主要适配的是数据库操作

# 业务域常见组件

"data-domain-common-practice"是一个沉淀数据操作业务的常见实践的组件包

## 常见操作流程沉淀

`CommonOperations`整理了业务领域常见且标准的流程。

### ReadThenCheck

业务领域一个极其常见的操作就是读取数据出来后检查数据的某些字段或存在性是否符合预期

* 比如更新用户数据前检查用户是否存在，是否已经锁定或禁用
* 比如删除用户前查看用户是否还有未完成的订单
* 比如创建商品前查看店铺是否没有被封
* 比如创建订单前查看商品是否已经下架

那是否存在进行检查来说，假设对用户有修改密码，修改账户状态，修改个人资料，修改家庭住址等方法，每一个方法都需要检查用户是否存在，则每一个方法内就需要写类似下面的代码

```java
public class UserService {
    public void changePassword() {
        User user = dao.getUser(id);
        //检查用户是否存在
        if (user != null) {
            //不存在的逻辑
        } else {

        }
    }
}
```

或者是抽象一个单独的用户是否存在的判断方法来进行统一处理。

在此，`CommonOperations`推荐使用`readThenCheck`方法

```java
public class CommonOperations {
    public ReadThenCheck readThenCheck() {
        return new ReadThenCheck();
    }

    public class ReadThenCheck {

        /**
         * 读取后验证
         *
         * @param supplier 复杂提供数据
         * @param checkers 负责消费数据
         * @param <R>      数据类型
         * @return 最终独取出来的数据
         */
        @Nullable
        @SafeVarargs
        public final <R> R single(Supplier<R> supplier, RecordChecker<R>... checkers) {
            R record = supplier.get();
            for (RecordChecker<R> checker : checkers) {
                checker.check(record);
            }
            return record;
        }

        /**
         * 读取后验证
         *
         * @param supplier 复杂提供数据
         * @param checkers 负责消费数据
         * @param <R>      数据类型
         * @return 最终独取出来的数据
         */
        @Nullable
        @SafeVarargs
        public final <R> Collection<R> collection(Supplier<Collection<R>> supplier, RecordCollectionChecker<R>... checkers) {
            Collection<R> records = supplier.get();
            for (RecordCollectionChecker<R> checker : checkers) {
                checker.check(records);
            }
            return records;
        }
    }
}
```

这个方法返回一个检查实例，实例可以检查单一记录是否符合预期，也可以检查一个记录集合是否符合预期(
比如给定一组用户要求发代金券，那么需要检查每一个用户id是否存在)。方法上都是需要输入读取出来的数据，然后附加一系列checker进行检查。

于是作为一个领域对象，它的所有验证器可以被封装为多个类，每一个类负责一种业务逻辑的判断，当需要读取的时候，调用方正常调用读取方法并按需传入验证器即可。验证器如果验证失败，则抛异常中断流程

### BasicChecker

在常见的实践中，要求检查的记录一般都是通过id进行查询，id或者是单独的一个或者是一个列表

```java
public abstract class BasicChecker<I, R> implements RecordChecker<R>, RecordCollectionChecker<R> {
    /**
     * 记录id
     */
    private Collection<I> recordIds;
    /**
     * 日志记录的等级
     */
    private GenericBasicLoggerMethodTemplate basicLogTemplate;

    /**
     * 异常工厂方法
     */
    private Function<Collection<I>, ? extends RuntimeException> exceptionFactory;

    /**
     * 从获取记录id
     *
     * @param record 记录
     * @return id
     */
    protected abstract I extractId(R record);

    /**
     * 给出日志记录中的发生了什么的字段
     *
     * @return 发生了什么的字段
     */
    protected abstract Word getLogHow();

    /**
     * 多个记录不存在的日志详情
     *
     * @param ids 哪些id不符合需求
     * @return 详情
     */
    @Nullable
    protected Detail getLogDetail(Collection<I> ids) {
        return new IdsDetail<>(ids);
    }

    /**
     * 执行单个记录的内部检查逻辑
     *
     * @param record 记录
     * @return 不符合的id清单
     */
    protected abstract boolean doCheck(@Nullable R record);
}
```

基于这样的实践，对基本的检查器进行封装

* 范性I代表id的类型，一般来说都是String
* R代表记录的类型
* basicLogTemplate是当检查结果不满足要求时的日志记录的方法模板，默认采用`GenericBasicLogger.error`，也就是记录错误日志
* getLogHow & getLogDetail, 当检查结果不符合预期时会自动记录日志(使用GenericBasicLogger)
  ，这里要求子类给出日志的how和detail，detail默认是`IdsDetail`
  ，也就是记录不符合的id清单
* doCheck执行检查，返回是否符合预期，不符合预期的记录的id将被送至getLogDetail和exceptionFactory
* exceptionFactory检查失败后抛出什么异常

### BasicExistenceChecker & BasicEntityExistenceChecker

负责检查记录是否存在，一个使用开放的类型，一个要求数据类型实现`BasicEntity`

```java
public abstract class BasicExistenceChecker<I, R> extends BasicChecker<I, R> {
    @Override
    protected Word getLogHow() {
        return new NotFound();
    }

    /**
     * 判断记录是否存在
     *
     * @param record 记录
     * @return 不符合的记录id
     */
    @Override
    @Nullable
    protected Collection<I> checkInternally(R record) {
        return record == null ? getRecordIds() : null;
    }

    @Override
    @Nullable
    protected Collection<I> checkInternally(Collection<R> records) {
        List<I> ids = new ArrayList<>(getRecordIds());
        records.forEach(
                record -> ids.remove(extractId(record))
        );
        return ids;
    }
}
```

逻辑也很简单，一个检查单个记录是否存在，不存在返回要求检查的id清单，一个检查读取出来的记录和要求检查的id清单之间的差距

### 状态检查

常规的记录可能有以下几种状态

* 是否启用, 对应`EnabledTrait`
* 是否激活，对应`ActiveTrait`
* 是否锁定，对应`LockedTrait`

于是检查器同样封装这几个，包含BasicXXXStatusCheck和BasicEntityXXXStatusChecker，自行取用

### 过期检查

常规的记录可能具有过期时间，如营销活动的过期时间，账户的过期时间等，对应`ExpiryTimeTrait`
，对位`BasicEntityExpiryTimeChecker` & `BasicExpiryTimeChecker`。

## 常用扫描回调

`StatementBuilder`
的若干方法都需要一个扫描器回调，而大量的最佳实践其实会发现，插入和进行记录覆盖的时候基本就是跳过那些个注解。因此对这些常用的注解进行总结，整理为`CommonScannerCallbacks`

```java
public class CommonScannerCallbacks {
    /**
     * 跳过注解
     */
    @AllArgsConstructor
    class IgnoreAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {
        private Collection<Class<? extends Annotation>> annotations;

        @Override
        public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
            return fieldScanner.scan(aClass, annotations, false);
        }
    }

    /**
     * 保留注解
     */
    @AllArgsConstructor
    class KeepAnnotations implements BiFunction<FieldScanner, Class<?>, Collection<String>> {
        private Collection<Class<? extends Annotation>> annotations;

        @Override
        public Collection<String> apply(FieldScanner fieldScanner, Class<?> aClass) {
            return fieldScanner.scan(aClass, annotations, true);
        }
    }

    /**
     * 跳过常见的select语句忽略的注解
     */
    class SelectStatementIgnoredAnnotations extends IgnoreAnnotations {

        public SelectStatementIgnoredAnnotations() {
            super(Collections.singletonList(ReadBySpecificOperation.class));
        }
    }

    /**
     * 跳过常见的插入数据忽略的注解
     */
    class InsertStatementIgnoredAnnotations extends IgnoreAnnotations {

        public InsertStatementIgnoredAnnotations() {
            super(Collections.singletonList(DatabaseControlledField.class));
        }
    }

    /**
     * 跳过常见的更新数据忽略的注解
     */
    class UpdateStatementIgnoredAnnotations extends IgnoreAnnotations {

        public UpdateStatementIgnoredAnnotations() {
            super(Arrays.asList(ImmutableField.class, DatabaseControlledField.class, UpdateBySpecificOperation.class));
        }
    }
}
```

于是如果插入语句，更新语句在使用时，可以直接`new CommonScannerCallbacks.InsertStatementIgnoredAnnotations`
或`new CommonScannerCallbacks.UpdateStatementIgnoredAnnotations`

## 常用条件

### QueryByIdCriteria

按id构建查询条件，构造方法要求给一个id在mapper具体方法中的参数名，不填默认就是"id"

# 缓存操作

参考[data-cache-core](data-cache-core/readme.md)

# 唯一id

参考[data-unique-id](/data-unique-id/readme.md)

# 标准化字段与常用编码

* data-common-codes内定义一些常用的国标编码，包含性别，民族等
* data-common-traits-**内定义了一些用户常用的属性字段
    * generic 定义了通用的属性
    * personal 定义了个人相关的属性
    * contact 定义了练习方法相关的属性

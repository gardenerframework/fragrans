# 业务域常见组件

"data-domain-common-practice"是一个沉淀数据操作业务的常见实践的组件包

# ReadThenCheck

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
        public final <R> R single(Supplier<R> supplier, RecordChecker<? super R>... checkers) {
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
        public final <R> Collection<R> collection(Supplier<Collection<R>> supplier, RecordCollectionChecker<? super R>... checkers) {
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

## RecordChecker & RecordCollectionChecker

这两个接口比较粗犷

```java

@FunctionalInterface
public interface RecordChecker<R> {
    /**
     * 检查单个记录
     *
     * @param record 记录
     */
    <T extends R> void check(@Nullable T record);
}

public interface RecordCollectionChecker<R> {
    /**
     * 检查记录集合
     * <p>
     * 默认可以不实现
     *
     * @param records 记录
     */
    <T extends R> void check(Collection<T> records);
}
```

就是把读取出来的数据直接扔进去，通过了什么都不需要做，不通过则抛异常

## 常见检查的沉淀

### 固化逻辑

* 给定一组id，检查id是否全部都存在、都激活、都没有被锁定等，如果有1个id不符合规则，那就算检查失败
* 没有要求给定的id，但是基于读取出来的数据检查是否符合要求，比如记录集不能为空，记录集不能是空，记录集的大小不能超过给定值(
  比如用户能够购买的特价品的订单数上限)
* 对于没有通过的记录或者没有通过的id，写日志后抛异常，日志的等级能够设定，抛出的异常由业务决策并能执行一些沉淀

### BaseChecker

在常见的实践中，要求检查的记录一般都是通过id进行查询，id或者是单独的一个或者是一个列表

```java
public abstract class BaseChecker<I, R> implements RecordChecker<R>, RecordCollectionChecker<R>, RecordIdExtractor<I, R> {
    /**
     * 要求检查的id清单
     * <p>
     * 为空就是只检查记录
     */
    @Singular
    @Nullable
    private Collection<I> ids;

    /**
     * 日志记录的等级
     * <p>
     * 大部分检查的失败可以写为info或debug级别
     */
    @Builder.Default
    @Setter(AccessLevel.PROTECTED)
    private GenericBasicLoggerMethodTemplate basicLogTemplate = GenericLoggerStaticAccessor.basicLogger()::debug;

    /**
     * 检查的记录类型
     */
    @NonNull
    @Setter(AccessLevel.PROTECTED)
    private Class<?> target;


    @Override
    public <T extends R> void check(@Nullable T record) {
        //统一到集合检查方法
        check(record == null ? Collections.emptyList() : Collections.singletonList(record));
    }

    @Override
    public <T extends R> void check(Collection<T> records) {
        //不合法的id清单
        Collection<I> invalidIds = CollectionUtils.isEmpty(ids) ? new HashSet<>() : new HashSet<>(ids);
        //检查集合的长度，大小等特征
        if (!checkCollection(records)) {
            fail(invalidIds, Phase.COLLECTION, records);
        }
        records.forEach(
                record -> {
                    I id = extractId(record);
                    //集合检查通过再检查每一个记录
                    if (!checkEachRecord(record)) {
                        //找到不合法记录的id
                        if (CollectionUtils.isEmpty(ids)) {
                            //向非法记录集中添加id
                            invalidIds.add(id);
                        }
                    } else {
                        if (!CollectionUtils.isEmpty(ids)) {
                            //从非法记录集中去掉id
                            invalidIds.remove(id);
                        }
                    }
                }
        );
        if (!CollectionUtils.isEmpty(invalidIds)) {
            fail(invalidIds, Phase.RECORD, records);
        }
    }

    /**
     * 失败处理
     *
     * @param invalidIds 非法id清单
     * @param phase      在什么阶段结束的
     * @throws RuntimeException 抛出异常
     */
    private <T extends R> void fail(Collection<I> invalidIds, Phase phase, Collection<T> records) throws RuntimeException {
        RuntimeException exception = raiseException(invalidIds, phase);
        basicLogTemplate.log(
                log,
                GenericBasicLogContent.builder()
                        .what(target)
                        .how(getHow())
                        .detail(getDetail(invalidIds, phase, records))
                        .build(),
                exception
        );
        throw exception;
    }

    /**
     * 检查读取出的记录集合的长度，大小是否符合要求
     *
     * @param records 集合
     * @param <T>     记录类型
     * @return 是否符合要求
     */
    protected abstract <T extends R> boolean checkCollection(Collection<T> records);

    /**
     * 检查每一个记录
     *
     * @param record 记录
     * @param <T>    记录类型
     * @return 是否符合预期
     */
    protected abstract <T extends R> boolean checkEachRecord(T record);

    /**
     * 检查失败，抛出异常
     *
     * @param invalidIds 不合法的id清单
     * @param phase      检查阶段
     * @return 浴场
     */
    protected abstract RuntimeException raiseException(Collection<I> invalidIds, Phase phase);

    /**
     * 发生了什么
     *
     * @return 日志的how
     */
    protected abstract Word getHow();

    /**
     * 获取日志的详情，默认就是只有不合法的id清单
     *
     * @param invalidIds id清单
     * @param phase
     * @return
     */
    protected <T extends R> Detail getDetail(Collection<I> invalidIds, Phase phase, Collection<T> records) {
        return new IdsDetail<>(invalidIds);
    }

    public enum Phase {
        /**
         * 检查集合时不合法
         */
        COLLECTION,
        /**
         * 检查记录时不合法
         */
        RECORD;
    }
}
```

基于这样的实践，对基本的检查器进行封装

* 范性I代表id的类型，一般来说都是String
* R代表记录的类型
* basicLogTemplate是当检查结果不满足要求时的日志记录的方法模板，默认采用`GenericBasicLogger.debug`，也就是记录调试日志
* checkCollection & checkEachRecord分别检查集合长度和每个记录是否合法，不合法由引擎调用raiseException抛出异常
* getHow用于日志记录中发生了什么
* getDetail用于日志的详情

### BaseExistenceChecker & BaseEntityExistenceChecker

负责检查记录是否存在，一个使用开放的类型，一个要求数据类型实现`BasicEntity`

```java
public abstract class BaseExistenceChecker<I, R> extends BaseChecker<I, R> {
    @Getter(AccessLevel.PROTECTED)
    private final Word how = new NotFound();

    @Override
    protected <T extends R> boolean checkCollection(Collection<T> records) {
        return !CollectionUtils.isEmpty(records);
    }

    @Override
    protected <T extends R> boolean checkEachRecord(T record) {
        return record != null;
    }
}
```

逻辑也很简单，要求集合不能为空，单个记录也不能为空

### 状态检查

常规的记录可能有以下几种状态

* 是否启用, 对应`EnabledTrait`
* 是否激活，对应`ActiveTrait`
* 是否锁定，对应`LockedTrait`

于是检查器同样封装这几个，包含BasicXXXStatusCheck和BasicEntityXXXStatusChecker，自行取用

# 常用扫描回调

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

# 常用条件

### QueryByIdCriteria

按id构建查询条件，构造方法要求给一个id在mapper具体方法中的参数名，不填默认就是"id"
# 基准数据定义

基准数据定义主要是为了规定和规范化数据库内的记录字段名称以及格式，并通过注解约定是否可以变化，是否可以通过put方法这类的粗旷操作直接覆盖

## BasicRecord

代表一个数据库记录具有基本的

* 创建时间
* 更新时间字段

```java
public abstract class BasicRecord {
    /**
     * 记录的创建时间，一般存储在落地表内，可由数据库产生默认值。
     * 创建时间一旦生成也是不可变的，请编程人员遵守这项设计
     */
    @ImmutableField
    @DatabaseControlledField
    private Date createdTime;
    /**
     * 上一次更新属性的时间，
     * 在落地表中可由数据库的特性来保证
     */
    @DatabaseControlledField
    private Date lastUpdateTime;
}
```

通过注解不难看出，创建时间不可变化，2个时间字段一般都由数据库来控制(设置默认值以及更新等)
。通过这个最基础的定义可见要求了数据库的每一条记录都需要有创建时间和更新时间。

<font color="orange">警告</font>:
更新时间其实表达的是数据记录的更新时间。这个更新可能是接口调用触发的，可能是dba操作数据库直接更新的(
更可能被更新语句直接覆盖)。因此这个时间并不能真实的代表外部操作的准确时间，需要区别这个字段在审计上的含义

## BasicRelation

代表一个数据库关系

```java
public abstract class BasicRelation extends BasicRecord {
    public BasicRelation(Date createdTime, Date lastUpdateTime) {
        super(createdTime, lastUpdateTime);
    }
}
```

关系数据的基本型不包含业务数据和id，因为关系一般都是由外部数据记录的id组成，并附加一些额外的属性

## BasicEntity

表达一个简单的实体，约定了实体的识别符号的规范名称为"id"，类型不定，一般为Long或者String

```java
public abstract class BasicEntity<T> extends BasicRecord {
    /**
     * 实体的识别符号
     */
    @ImmutableField
    private T id;

    protected BasicEntity(Date createdTime, Date lastUpdateTime, T id) {
        super(createdTime, lastUpdateTime);
        this.id = id;
    }
}
```

## BasicTrash

代表已经被删除的记录。对比逻辑删除和真实物理删除，最佳实践推荐将被删除的记录移到回收站。当移动到回收站时，为了避免回收站的表结构总是受到记录表变更的影响，将被删除的记录作为一个json存储在其中

```java
public abstract class BasicTrash<I> extends BasicRecord {
    /**
     * 被删除的，仍在垃圾箱中的物体
     */
    @ImmutableField
    private I item;

    protected BasicTrash(Date createdTime, Date lastUpdateTime, I item) {
        super(createdTime, lastUpdateTime);
        this.item = item;
    }
}
```

## BasicEntityTrash & BasicRelationTrash

实体和关系的回收站记录结构

```java
public abstract class BasicEntityTrash<T, I> extends BasicTrash<I> implements IdTrait<T> {
    /**
     * 被删除的，仍在垃圾箱中的物体
     */
    @ImmutableField
    private T id;

    protected BasicEntityTrash(Date createdTime, Date lastUpdateTime, T id, I item) {
        super(createdTime, lastUpdateTime, item);
        this.id = id;
    }
}
```

```java
public abstract class BasicRelationTrash<I> extends BasicTrash<I> {

    protected BasicRelationTrash(Date createdTime, Date lastUpdateTime, I item) {
        super(createdTime, lastUpdateTime, item);
    }
}
```

<font color=orange>注意</font>: BasicEntityTrash中的id是否和被删除的记录id保持一致没有硬性规，推荐保持一致。

## 小结

以上构成了基本的记录、关系、实体以及归档记录的数据结构

# 操作追踪

当一些记录需要进行操作追踪时，需要对以上基础实体进行扩展，增加谁创建的、谁更新的、谁删除的3个主要操作追踪常用字段。这几个字段使得记录可以按创建人，更新人等进行查询

<font color=orange>注意</font>: 由于数据库记录能够被应用程序外进行更新，因此这个记录的可靠性应当配合审计记录一起查看

## BasicOperationTraceableEntity

在基础实体的基础上增加了创建人和更新人

```java
public abstract class BasicOperationTraceableEntity<T> extends BasicEntity<T> {
    /**
     * 创建人
     */
    @ImmutableField
    private String creator;
    /**
     * 上一次更新人
     */
    @OperationTraceField
    private String updater;

    protected BasicOperationTraceableEntity(Date createdTime, Date lastUpdateTime, T id, String creator, String updater) {
        super(createdTime, lastUpdateTime, id);
        this.creator = creator;
        this.updater = updater;
    }
}
```

## BasicOperationTraceableRelation

和上面一样

```java
public abstract class BasicOperationTraceableRelation extends BasicRelation implements CreatorTrait, UpdaterTrait {
    /**
     * 创建人
     */
    @ImmutableField
    @OperationTraceField
    private String creator;
    /**
     * 上一次更新人
     */
    @OperationTraceField
    private String updater;

    protected BasicOperationTraceableRelation(Date createdTime, Date lastUpdateTime, String creator, String updater) {
        super(createdTime, lastUpdateTime);
        this.creator = creator;
        this.updater = updater;
    }
}
```

## BasicOperationTraceableEntityTrash & BasicOperationTraceableRelationTrash

在基础回收站记录的基础上主要增加了删除人

# 字段注解

字段注解在本组件中主要是配合orm工具来使用的，它的常见用法就是在扫描实体类时用于跳过或者保留带有注解的字段

## ImmutableField & ImmutableRelation

代表字段一旦创建就不应当被修改

## SkipInGenericReadOperation & SkipInGenericUpdateOperation

"Generic Operation"的含义是指通用的操作，比如读取用户的时候会将用户的所有属性进行orm扫描然后返回。比如更新用户的信息时会同时更新多个信息。

但是部分属性显然在这种操作时需要跳过，比如密码。又比如商品的价格是个经常变化的字段，它既不进入缓存，也不推荐在商品读取时连同几千字的详情等一并读出。因此会有一个`getPrice`
方法，在sql语句上

```sql
select price
from sku
where id = #{id}
```

这样的语句来读取，此时就需要标记price字段为`SkipInGenericReadOperation`，这样通用的读取或者查询语句就会跳过这个字段

`SkipInGenericUpdateOperation`同理，意思是有单独的，明确的接口来操作。比如库存(stock)
这个字段，并不会随着商品详情的更新而在update语句中被整体覆盖，因此库存字段应当标记为`SkipInGenericUpdateOperation`

## OperationTraceField

表达这个字段是用来记录创建人，更新人的操作跟踪用的字段

# 查询结果

`GenericQueryResult`是一个通用的查询结果标准化结构，包含了内容和总数，适合分页查询的场景
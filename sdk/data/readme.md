# 引言

data组件主要集合和与数据相关的定义和操作

* [common](common): 负责与数据定义，持久化和操作相关的共用代码定义
* [data-schema](data-schema): 负责标准化的数据定义，主要是与数据持久化相关的数据定义

# 基准数据定义

基准数据定义主要是为了规定和规范化数据库内的记录字段名称以及格式，并通过注解约定是否可以变化，是否可以通过put方法这类的粗旷操作直接覆盖

## BasicRecord

代表一个数据库记录(E-R关系中的E)，具有基本的

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

## BasicRelation

代表一个数据库关系

```java
public abstract class BasicRelation extends BasicRecord {
    public BasicRelation(Date createdTime, Date lastUpdateTime) {
        super(createdTime, lastUpdateTime);
    }
}
```

## BasicEntity

表达一个简单的实体，约定了实体的识别符号的规范名称为"id"，类型不定，一般为Long或者String

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

# 操作追踪

当一些记录需要进行操作追踪时，需要对以上基础实体进行扩展，增加谁创建的、谁更新的、谁删除的3个主要操作追踪常用字段

## BasicOperationTraceableEntity

在基础实体的基础上增加了创建人和更新人

```java
public abstract class BasicOperationTraceableEntity<T> extends BasicEntity<T> implements CreatorTrait, UpdaterTrait {
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

## ReadBySpecificOperation & UpdateBySpecificOperation

意思是有明确的读取方法，而不是在读取记录语句中被一并带出。比如商品的价格是个经常变化的字段，它既不进入缓存，也不推荐在商品读取时连同几千字的详情等一并读出。因此会有一个`getPrice`方法，在sql语句上

```sql
select price
from sku
where id = #{id}
```

这样的语句来读取，此时就需要标记price字段为`ReadBySpecificOperation`

`UpdateBySpecificOperation`同理，意思是有单独的，明确的接口来操作

## OperationTraceField

表达这个字段是用来记录创建人，更新人的操作跟踪用的字段

# 持久化工具集

data-persistence组件定义了常用的orm工具和持久化工具，其兼容的数据为mysql数据库并使用mybatis作为底层框架，目前没有兼容其它数据库语言的计划

此外，本组件的目标不是发展成为一个健全的orm框架，而是通过一些最佳实践来简化开发的过程。

## 推荐的方法

orm工具推荐将实体的类型作为一个操作的基准对象，并在类型上标记上若干注解，如`@TableName`表达类型映射的底层表，来表达表的映射，字段到数据库的列名的转换处理器等。

## 实体类向数据库表结构转换

`FieldScanner`类是一个bean，用于完成实体类向数据库表结构的转换，它的几个方法都是输入类名然后给出需要扫描的数据库列名作为结果。

### 通过Trait接口确认要操作的属性名

```java
public class FieldScanner {
    /**
     * 取出所有属性列
     *
     * @param clazz           实体类
     * @param traitInterfaces 需要基于的特性接口
     * @return 属性列
     */
    public Collection<String> scan(
            Class<?> clazz,
            Collection<Class<?>> traitInterfaces
    ) {

    }

    /**
     * 取出所有属性列
     *
     * @param clazz               实体类
     * @param columnNameConverter 属性名到表列名的转换器
     * @param traitInterfaces     需要基于的特性接口
     * @return 属性列
     */
    public Collection<String> scan(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter,
            Collection<Class<?>> traitInterfaces
    ) {

    }
}
```

以上两个方法输入一组接口，这些接口一般只有get/set方法，用于声明类中应当具备的属性。扫描器要求类必须实现这些接口，并将接口的get/set方法声明的属性名称扫描出来

### 通过注解确认要操作的属性名

```java
public class FieldScanner {


    /**
     * 扫描所有属性
     *
     * @param clazz               实体类
     * @param columnNameConverter 属性名到表列名的转换器
     * @param annotations         注解
     * @param keep                符合注解的是保留还是去掉
     * @return 所有属性名
     */
    public Collection<String> scan(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter,
            Collection<Class<? extends Annotation>> annotations,
            boolean keep
    ) {
    }

    /**
     * 扫描所有属性
     *
     * @param clazz       实体类
     * @param annotations 注解
     * @param keep        符合注解的是保留还是去掉
     * @return 所有属性名
     */
    public Collection<String> scan(
            Class<?> clazz,
            Collection<Class<? extends Annotation>> annotations,
            boolean keep
    ) {
    }
}
```

上面的scan方法要求输入一个给定的类型和一组注解并表达带有注解的字段是保留还是去掉。通过这个方法，能够使得开发人员一次性将本次sql语句需要操作的实体类的属性名进行确认，并通过`ColumnNameConverter`进行转换。

### ColumnNameConverter

在java编程语言中，类的属性一般都是驼峰命名的，而数据库表结构的字段则一般都不是驼峰命名的，而是下划线连接的，比如"dateOfBirth"在数据库中的列名一般为"date_of_birth"。
因此，如果在扫描类型时，不需要对这种驼峰转小写下划线的方式进行干预，则在扫描方法中不需要传入具体的转换器。`FieldScanner`默认使用`CamelToUnderscoreConverter`进行转换。

但不排除有些老的系统或者已经成型的命名方法不是这样的，因此允许开发人员自己实现`ColumnNameConverter`来进行转换

### UsingColumnNameConverter注解

实体类可以标记`@UsingColumnNameConverter`来整体设置使用的转换器，这样不指定`ColumnNameConverter`
作为输入的方法就会查询实体类上是否带有这个注解，如果没有注解则使用`CamelToUnderscoreConverter`作为默认转换器

### 自行定义过滤

使用注解不是`FieldScanner`的唯一途径

```java
public class FieldScanner {
    /**
     * 扫描一个类的所有字段
     *
     * @param clazz               类
     * @param columnNameConverter 属性名到表列名的转换器
     * @param filter              过滤器, true = 保留 / false = 过滤掉
     * @return 符合的字段名
     */
    public Collection<String> scan(
            Class<?> clazz,
            @Nullable ColumnNameConverter columnNameConverter,
            Function<Field, Boolean> filter
    ) {

    }

    /**
     * 扫描一个类的所有字段
     *
     * @param clazz  类
     * @param filter 过滤器, true = 保留 / false = 过滤掉
     * @return 符合的字段名
     */
    public Collection<String> scan(
            Class<?> clazz,
            Function<Field, Boolean> filter
    ) {
    }
}
```

开发人员还能调用这两个接口，并给定一个过滤方法，这个方法输入一个`Field`类型的数据，然后要求回答这个字段是保留(true)还是过滤掉(false)

## 语句拼接与拆解

组件提供`StatementBuilder`类进行sql语句的拼装

### 查询语句

mysql查询语句的基本构成是"select ${queryColumn} from ${table} where ${queryCriteria} group by ${groupByColumns} having
${havingCriteria} order by ${oderByColumn} limit ${offset}, ${size}"

进行拆解后就是

* queryColumn是列名称，是一个字符串清单
* table是表名
* queryCriteria是搜索条件
* groupByColumns是group by的列名
* havingCriteria是having的条件
* $oderByColumn是排序列
* offset和size则是分页的信息¬

这里注意，group by、having、order by、limit是select语句特有的

#### column(s)方法集

`StatementBuilder.select`方法返回一个`SelectStatement`对象用于查询语句的具体构建。这个对象通过column(s)的若干方法来确定查询返回的所有数据库列名

```java
public class SelectStatement extends BasicStatement<SelectStatement> {
    //...

    /**
     * 插入多个select查询结果列，每一个列名将被自动附加``符号
     *
     * @param columns 结果列清单
     * @return 语句
     */
    public SelectStatement columns(Collection<String> columns) {
    }

    /**
     * 插入多个select查询结果列，每一个列名将由gravyAccentFilter决定是否添加重音符号
     *
     * @param columns           结果列清单
     * @param gravyAccentFilter 列名是否添加重音符号的过滤器
     * @return 语句
     */
    public SelectStatement columns(Collection<String> columns, Function<String, Boolean> gravyAccentFilter) {

    }

    /**
     * 添加一个单独的列，列名将被自动附加``符号
     *
     * @param column 列名
     * @return 语句
     */
    public SelectStatement column(String column) {

    }

    /**
     * 添加一个单独的列
     *
     * @param column         列名
     * @param addGravyAccent 是否要添加重音符号
     * @return 语句
     */
    public SelectStatement column(String column, boolean addGravyAccent) {

    }

    /**
     * 添加一个单独的列以及别名，列名和别名都将被自动附加``符号
     *
     * @param column 列名
     * @param alias  别名
     * @return 语句
     */
    public SelectStatement column(String column, String alias) {

    }

    /**
     * 添加一个单独的列以及别名，列名由参数决策，但别名将被自动附加``符号
     *
     * @param column         列名
     * @param addGravyAccent 列名是否要加重音符号
     * @param alias          别名
     * @return 语句
     */
    public SelectStatement column(String column, boolean addGravyAccent, String alias) {

    }
}
```

* columns方法用来插入多个待查询的结果列名
* column方法用来插入单个待查询的结果列名

以上两个方法默认情况下都会对结果列名自动添加重音符号从而避免无意使用了mysql的关键词而报错。一些额外的场景下，比如使用了count、max、avg等聚合函数，则可以使用column方法来指定别名，别名自动加重音符号

column(s)方法多次调用执行的逻辑是向语句中继续添加列

#### where方法

使用where方法输入一个条件，条件可以是`BooleanCriteria`来进行层次化的组合。多次调用where方法的逻辑是最后的条件生效

#### groupBy & having

```java
public class SelectStatement extends BasicStatement<SelectStatement> {
    /**
     * 聚合分组列名，自动加重音符号
     *
     * @param columns 列名
     * @return 语句
     */
    public SelectStatement groupBy(Collection<String> columns) {
    }

    /**
     * 聚合分组列名
     *
     * @param columns           列名
     * @param gravyAccentFilter 由过滤器决定是不是加重音符号
     * @return 语句
     */
    public SelectStatement groupBy(Collection<String> columns, Function<String, Boolean> gravyAccentFilter) {

    }

    /**
     * 给定having的条件
     *
     * @param criteria 条件，使用{@link BooleanCriteria}进行条件组合
     * @return 语句
     */
    public SelectStatement having(BasicCriteria criteria) {
    }
}
```

和查询的逻辑差不多类似，指定group by和having的列以及条件

#### orderBy

```java
public class SelectStatement extends BasicStatement<SelectStatement> {
    /**
     * 指定一个排序列进行升序，对应的列名自动会加重音符号
     *
     * @param column 列名
     * @return 语句
     */
    public SelectStatement orderBy(String column) {

    }

    /**
     * 指定一个排序列进行升序
     *
     * @param column         列名
     * @param addGraveAccent 是否加重音符号
     * @return 语句
     */
    public SelectStatement orderBy(String column, boolean addGraveAccent) {

    }

    /**
     * 指定一个排序列进行升序，对应的列名自动会加重音符号
     *
     * @param column 列名
     * @param order  升降序
     * @return 语句
     */
    public SelectStatement orderBy(String column, @Nullable Order order) {

    }

    /**
     * 指定一个排序列进行升序
     *
     * @param column         列名
     * @param addGraveAccent 是否加重音符号
     * @param order          升降序
     * @return 语句
     */
    public SelectStatement orderBy(String column, boolean addGraveAccent, @Nullable Order order) {

    }
}
```

orderBy可以调用多次，并针对每一个列指定是否加重音符号以及升降序，不指定默认就是升序，orderBy调用的顺序 = 排序列的顺序。

#### limit

本文觉得应该不需要再解释了

#### 基于类型快速构建

`StatementBuild.select(class, callback)`可以基于类型快速给出一个select的语句原型，这个原型由callback给出扫描的列，并要求类型带有@TableName注解来表达表

### 查询条件

在查询语句中出现了查询条件的概念

#### BasicCriteria

在语句中一个比较常见的就是条件(查询、更改、删除语句中均有)，它分为

* 简单条件
* 布尔组合条件
* 批量操作条件

几个

#### RawCriteria

简单条件就是单个条件，比如 x = y、x > y、日期的between，本文的like等

```java
public class RawCriteria extends BasicCriteria {
    private final String criteria;

    @Override
    public String build() {
        return criteria;
    }
}
```

可见并没有进行复杂的抽象，要求输入的条件的字符串

#### BooleanCriteria

布尔条件是条件的组合，包含了

* 条件a
* 条件b
* 布尔链接符

给出的条件语句是 (a) AND/OR (b)

因此可以安全的进行布尔表达式的内嵌

#### BatchCriteria

批量操作条件也是一种常见的需求，其在mybatis中的含义是给定一个列表，然后通过<foreach>指令来进行循环，例如

```xml

<foreach item="id" collection="idList" separator="OR">
    `id` = #{id}
</foreach>
```

因此它就包含了

* collection: 集合参数的名称
* item: 单个元素参数的名称
* separator: 逻辑分隔符
* criteria: 内部的查询条件

### 插入语句

mysql插入语句的基本构成是"insert into ${table} (${columns}) values (${values[0]}), (${values[1]}) ..."

进行拆解后就是

* table是表名
* columns是插入语句影响的记录列
* values是个清单，有多少行数据插入多少个

`StatementBuild.insert`给出一个`InsertStatement`的对象，这个对象用于构建插入语句，构建过程中的关键就是完成列与值的对应

```java
public class InsertStatement {
    /**
     * 准备插入的属性列
     *
     * @param columns 列清单，自动加重音符号
     * @return 语句
     */
    public InsertStatement columns(Collection<String> columns, Function<String, BasicValue> columnValueMapper) {

    }

    /**
     * 准备插入的属性列
     *
     * @param column 列名，自动加重音
     * @return 语句
     */
    public InsertStatement column(String column, Function<String, BasicValue> columnValueMapper) {

    }
}
```

* column支持向插入语句中一个一个设置列和值
* columns简化上面的操作，可以直接插入一批列
* columnValueMapper负责将列转为对位的值

实操上，假如sql语句要长这样

```sql
insert into user (id, name, enabled)
values (#{id}, #{name}, true)
```

则这个类这么调用

```java
new InsertStatement()
        .column("id",s->new ParameterNameValue(s))
        .column("name",new ParameterNameValue(s))
        .column("enabled",s->new RawValue(true))
```

也可以

```java
new InsertStatement()
        .columns(Arrays.asList("id","name","enabled"),s->{
        return s=="enabled"?new RawValue(true):new ParameterNameValue(s)
        });
```

### 值

插入语句和更新语句都涉及到给列赋值，值的类型被分解为以下几种

* RawValue: 就是非字符串类型的原始值，比如数字，bool等
* TextValue: 字符串类型的值, 这里会转义如"、\等特殊字符
* ParameterNameValue: 值是取自mybatis mapper方法的一个具体的参数名，比如

```java
public interface ExampleMapper {
    @Update
    void lockUser(@Param("userId") String userId, @Param("locked") boolean locked);
}
```

userId和locked给了2个参数，这时如果更新语句指定更新这两个列，值的类型就是`ParameterNameValue`

* FieldNameValue: 当更新的或这插入的数据列大部分来自一个实体类型的时候，常见的做法是把整个实体传到mapper中

```java
public interface ExampleMapper {
    @Update
    void addUser(@Param("user") User user);
}
```

这时插入语句和更新语句的列大部分需要组合成"#{参数名.字段名}"的形式，也就是`FieldNameValue`

#### RawValue & TextValue

这两个都需要设置值的字符串，区别在于RawValue不会去处理特殊字符串，也就是假如要拼接下面的sql语句

```sql
insert into `user` (id, enabled)
values (#{id}, true)
```

时，与enabled字段配对的就是个RawValue，他就是把java基本类型的值直接打印到sql语句中，如果值的类型是个字符串，那也不会附加双引号。与之对应的，TextValue就会在打印sql语句时附加双引号，比如

```sql
insert into `user` (id, name)
values (#{id}, "张三")
```

时，与name字段配对的就是个TextValue，它的java类型是个字符串，当输入到sql语句中时，这个值就会被包裹在双引号内，并对字符串自己的内部的双引号等特殊字符进行转义

无论RawValue还是TextValue，其含义都是非来自于参数的值，一般用于非常有限且简单的场景，比如更改一个bool的状态等。

#### ParameterNameValue

这个值要求给定的是参数的名称，会自动将它拼接为"#{参数名称}"的模式，也就是mybatis的预编译语句所需的变量名

#### FieldNameValue

这个值要求给定实体的参数名和属性名，最终拼接为"#{参数名.属性名}"的模式

### 插入语句简化操作

上面给了一个列一个列进行对照设定的例子，而实际过程中插入语句会直接用一个对象或者一个对象的列表进行插入

面对这样的需求，以下的方法可以满足要求

```java
public class InsertStatement {
    /**
     * 准备插入的属性列
     *
     * @param columns             列清单，自动加重音符号
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnNameConverter 数据库列到实体字段的映射关系
     * @return 语句
     */
    public InsertStatement columns(Collection<String> columns, String entityName, @Nullable ColumnNameConverter columnNameConverter) {
    }

    /**
     * 准备插入的属性列
     *
     * @param column              列名，自动加重音
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnNameConverter 数据库列到实体字段的映射关系
     * @return 语句
     */
    public InsertStatement column(String column, String entityName, @Nullable ColumnNameConverter columnNameConverter) {

    }

    /**
     * 准备插入的属性列
     *
     * @param columns             列清单，自动加重音符号
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnToFieldMapper 数据库列到实体字段的映射关系
     * @return 语句
     */
    public InsertStatement columns(Collection<String> columns, String entityName, Function<String, String> columnToFieldMapper) {

    }

    /**
     * 准备插入的属性列
     *
     * @param column              列名，自动加重音
     * @param entityName          实体名称，一般来说就是mybatis的参数名
     * @param columnToFieldMapper 数据库列到实体字段的映射关系
     * @return 语句
     */
    public InsertStatement column(String column, String entityName, Function<String, String> columnToFieldMapper) {

    }
}
```

简单来说就是设定好数据库的列，mapper方法中的实体的参数名，还有一个就是数据库列和实体类型字段之间的名称转换器，这里可以和类型扫描时使用的一样，也可以实现Function<String, String>自行定义

最后，插入语句支持默认转换器，这个转换器由`StatementBuilder.insert`语句，自动联合`FieldScanner`获得默认转换器，参考

`StatementBuilder.insert(class, callback, entityName)`
，这个方法很快捷的通过类型设定了表名，并要求开发人员扫描属性和按照类设定的转换器完成属性名称和数据库名称之间的互转，进一步要求给定mapper方法中的参数的名称来使得生成的语句正确地对应上方法传入的实体对象，比如

```java

@TableName("user")
public class User {
    private String id;
    private String name;
    @DatabaseControlledField
    private Date createdTime;
}

public interface UserDao {
    void addUser(@Param("user") User user);
}
```

在使用的过程中就是

```java
StatementBuilder.insert(User.class,(scanner,clazz)->scanner.scan(clazz,Arrays.asList(DatabaseControlledField.class),false),"user");
```

搞出来的sql语句是

```sql
insert into `user` (`id`, `name`)
values (#{user.id}, #{user.name})
```

其中`DatabaseControlledField`注解的创建时间没有在语句中，因为扫描时要求被去掉

如果有些列需要额外添加，则继续调用.column方法进行添加即可

### 批量插入

大量的操作需要一次性导入多条数据，因此需要插入语句支持`batch`操作

```java
public class InsertStatement {
    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns    属性列
     * @param collection 集合名称
     * @param item       单个元素名称
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item) {
    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns             属性列
     * @param collection          集合名称
     * @param item                单个元素名称
     * @param columnNameConverter 列到值的映射器
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item, @Nullable ColumnNameConverter columnNameConverter) {

    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns             属性列
     * @param collection          集合名称
     * @param item                单个元素名称
     * @param columnToFieldMapper 列到值的映射器
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item, Function<String, String> columnToFieldMapper) {

    }

    /**
     * 给一个批量插入的语句
     * <p>
     * 一旦开启批量模式，就只能重复覆盖列和值清单
     *
     * @param columns           属性列
     * @param collection        集合名称
     * @param item              单个元素名称
     * @param columnValueMapper 数据库列到实体字段的映射关系
     * @return 语句
     */
    public InsertStatement batch(Collection<String> columns, String collection, String item, BiFunction<String, String, BasicValue> columnValueMapper) {
    }
}
```

<font color=orange>警告</font>: 批量操作一旦调用过，就不能再调用columns或者column进行列和值的配对了

简单来讲，批量操作要求给定列名称，集合参数名称和每一个集合元素名称。随后，将列名一一通过`columnValueMapper`去获得值，又或者认为值是一个实体类型的若干字段，则使用`columnNameConverter`
或`columnToFieldMapper`来讲数据库的列名转为字段名

举例来说

```java

@TableName("user")
public class User {
    private String id;
    private String name;
    @DatabaseControlledField
    private Date createdTime;
}

public interface UserDao {
    void batchAdd(@Param("userList") List<User> users);
}
```

配合StatementBuilder就要这么调用

```java
StatementBuilder.batch(User.class,(scanner,clazz)->scanner.scan(clazz,Arrays.asList(DatabaseControlledField.class),false),"userList","user");
```

生成的语句就长这样

```sql
insert into `user` (`id`, `name`)
values <foreach collection="userList" item="user" separator =",">(#{user.id}, #{user.name})</foreach>
```

### 更新语句

mysql修改语句的基本构成是"update ${table} set ${column1}=${value1}, ${column1}=${value1}... where ${queryCriteria}"

进行拆解后就是

* table是表名
* columnX是列名,
* valueX是值
* queryCriteria是查询条件

类似的，`StatementBuilder.update`用来生成一个更新语句`UpdateStatement`，更新语句提供的方法和插入语句基本一致但没有批量设置值这个逻辑，而是where方法能够输入`BatchCriteria`
进行批量条件筛选。

在简化操作上，update支持输入类名，并额外提供了方法要求提供扫描回调和参数名。只给类名是设置表，都给了是设置表+扫描字段并进行值的自动配对，逻辑和插入语句一致

### 删除语句

mysql删除语句的基本构成是"delete from ${table} where ${queryCriteria}"

进行拆解后就是

* table是表名
* queryCriteria是查询条件

## 规范化条件

查询、更新、删除语句都允许输入条件，而目前的条件相对粗旷一些，`RawCriteria`给定的是一个字符串，那么为了简化开发的过程，也为了防止开发人员写出来一些bug，增加一些常用的查询条件

### EqualsCriteria & InequalityCriteria

这两个类主要用来创建等式和不等式的查询条，在构造方法中有几个常用的参数

* column: 条件的列名
* addGravyAccent: 这个列名要不要加重音，因为有些所谓的列名是个mysql函数，比如"length(name) >= 10"里面的length
* value: 值，值的类型分为原始类型(字符串)和`BasicValue`类型两种，原始类型的诉求就是解决比如"`name` = concat(`name`, 1)"，里面的concat函数，其它类型的值就主要使用值的`build`
  方法来转成字符串

从落地来说，假如mapper的方法是这样

```java
public interface UserDao {
    void updateById(@Param("id") String id, @Param("user") User user);
} 
```

方法这么调

```java
builder.update(User.class)
        .where(new EqualsCriteria("id",new ParameterNameValue("id")));
```

根据上面的讲解，`ParameterNameValue`将构建成"#{id}"，而EqualsCriteria构建出来的整体结果为 "\`id\` = #{id}"

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

这个方法返回一个检查实例，实例可以检查单一记录是否符合预期，也可以检查一个记录集合是否符合预期(比如给定一组用户要求发代金券，那么需要检查每一个用户id是否存在)。方法上都是需要输入读取出来的数据，然后附加一系列checker进行检查。

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
* getLogHow & getLogDetail, 当检查结果不符合预期时会自动记录日志(使用GenericBasicLogger)，这里要求子类给出日志的how和detail，detail默认是`IdsDetail`
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

常规的记录可能具有过期时间，如营销活动的过期时间，账户的过期时间等，对应`ExpiryTimeTrait`，对位`BasicEntityExpiryTimeChecker` & `BasicExpiryTimeChecker`。

## 常用扫描回调

`StatementBuilder`的若干方法都需要一个扫描器回调，而大量的最佳实践其实会发现，插入和进行记录覆盖的时候基本就是跳过那些个注解。因此对这些常用的注解进行总结，整理为`CommonScannerCallbacks`

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

参考[data-cache-core](./data-cache-core/readme.md)

# 唯一id

参考[data-unique-id](/data-unique-id/readme.md)

# 标准化字段与常用编码

* data-common-codes内定义一些常用的国标编码，包含性别，民族等
* data-common-traits-**内定义了一些用户常用的属性字段
    * generic 定义了通用的属性
    * personal 定义了个人相关的属性
    * contact 定义了练习方法相关的属性

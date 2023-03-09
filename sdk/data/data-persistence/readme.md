# 持久化工具集

组件定义了常用的orm工具和持久化工具，其兼容的数据为mysql数据库和SqlServer并使用mybatis作为底层框架，目前没有兼容其它数据库语言的计划

此外，本组件的目标不是发展成为一个健全的orm框架，而是通过一些最佳实践来简化开发的过程。

# 属性扫描

orm是将一个实体类的操作与数据库语言进行关联的工具

属性扫描是orm的基础之一，select语句、insert语句、update语句在具体操作某个实体时，通常需要扫描属性以及属性上的注解来生成sql语句

`FieldScanner`类是一个bean，用于完成实体类向数据库表结构的转换，它的几个方法都是输入类名然后给出需要扫描的数据库列名作为结果。

## 通过Trait接口确认要操作的属性名

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

## 通过注解确认要操作的属性名

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

上面的scan方法要求输入一个给定的类型和一组注解并表达带有注解的字段是保留还是去掉。通过这个方法，能够使得开发人员一次性将本次sql语句需要操作的实体类的属性名进行确认，并通过`ColumnNameConverter`
进行转换。

## ColumnNameConverter

在java编程语言中，类的属性一般都是驼峰命名的，而数据库表结构的字段则一般都不是驼峰命名的，而是下划线连接的，比如"
dateOfBirth"在数据库中的列名一般为"date_of_birth"。
因此，如果在扫描类型时，不需要对这种驼峰转小写下划线的方式进行干预，则在扫描方法中不需要传入具体的转换器。`FieldScanner`
默认使用`CamelToUnderscoreConverter`进行转换。

但不排除有些老的系统或者已经成型的命名方法不是这样的，因此允许开发人员自己实现`ColumnNameConverter`来进行转换

## UsingColumnNameConverter注解

实体类可以标记`@UsingColumnNameConverter`来整体设置使用的转换器，这样不指定`ColumnNameConverter`
作为输入的方法就会查询实体类上是否带有这个注解，如果没有注解则使用`CamelToUnderscoreConverter`作为默认转换器

## 自行定义过滤

使用注解或trait不是`FieldScanner`的唯一途径

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

开发人员还能调用这两个接口，并给定一个过滤方法，这个方法输入一个`Field`类型的数据，然后要求回答这个字段是保留(true)
还是过滤掉(false)

# 语句拼接与拆解

组件提供`StatementBuilder`类进行sql语句的拼装

## 查询语句

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

### column(s)方法集

`StatementBuilder.select`方法返回一个`SelectStatement`对象用于查询语句的具体构建。这个对象通过column(s)
的若干方法来确定查询返回的所有数据库列名

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

### where方法

使用where方法输入一个条件，条件可以是`BooleanCriteria`来进行层次化的组合。多次调用where方法的逻辑是最后的条件生效

### groupBy & having

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

### orderBy

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

### limit

本文觉得应该不需要再解释了

### 基于类型快速构建

`StatementBuild.select(class, callback)`可以基于类型快速给出一个select的语句原型，这个原型由callback给出扫描的列，并要求类型带有@TableName注解来表达表

### 查询条件

在查询语句中出现了查询条件的概念

#### DatabaseCriteria

在语句中一个比较常见的就是条件(查询、更改、删除语句中均有)，它分为

* 简单条件
* 布尔组合条件
* 批量操作条件

几个

#### RawCriteria

简单条件就是单个条件，比如 x = y、x > y、日期的between，本文的like等

```java
public class RawCriteria implements DatabaseCriteria {
    private final String criteria;

    @Override
    public String build() {
        return criteria;
    }
}
```

可见并没有进行复杂的抽象，要求输入的条件的字符串

#### BooleanCriteria/MatchAllCriteria/MatchAnyCriteria/NotCriteria

布尔条件是条件的组合，包含了

* 条件a
* 条件b
* 布尔链接符

给出的条件语句是 (a) AND/OR (b)

因此可以安全的进行布尔表达式的内嵌，MatchXCriteria则代表多个条件或者要求全部符合，或者要求符合其中一个

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

## 插入语句

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

## 值

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

### RawValue & TextValue

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

### ParameterNameValue

这个值要求给定的是参数的名称，会自动将它拼接为"#{参数名称}"的模式，也就是mybatis的预编译语句所需的变量名

### FieldNameValue

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

简单来说就是设定好数据库的列，mapper方法中的实体的参数名，还有一个就是数据库列和实体类型字段之间的名称转换器，这里可以和类型扫描时使用的一样，也可以实现Function<
String, String>自行定义

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

简单来讲，批量操作要求给定列名称，集合参数名称和每一个集合元素名称。随后，将列名一一通过`columnValueMapper`
去获得值，又或者认为值是一个实体类型的若干字段，则使用`columnNameConverter`
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

## 更新语句

mysql修改语句的基本构成是"update ${table} set ${column1}=${value1}, ${column1}=${value1}... where ${queryCriteria}"

进行拆解后就是

* table是表名
* columnX是列名,
* valueX是值
* queryCriteria是查询条件

类似的，`StatementBuilder.update`用来生成一个更新语句`UpdateStatement`
，更新语句提供的方法和插入语句基本一致但没有批量设置值这个逻辑，而是where方法能够输入`BatchCriteria`
进行批量条件筛选。

在简化操作上，update支持输入类名，并额外提供了方法要求提供扫描回调和参数名。只给类名是设置表，都给了是设置表+扫描字段并进行值的自动配对，逻辑和插入语句一致

## 删除语句

mysql删除语句的基本构成是"delete from ${table} where ${queryCriteria}"

进行拆解后就是

* table是表名
* queryCriteria是查询条件

# 单一条件规范化

查询、更新、删除语句都允许输入条件，而目前的条件相对粗旷一些，`RawCriteria`
给定的是一个字符串，那么为了简化开发的过程，也为了防止开发人员写出来一些bug，增加一些常用的查询条件

## EqualsCriteria & InequalityCriteria

这两个类主要用来创建等式和不等式的查询条，在构造方法中有几个常用的参数

* column: 条件的列名
* addGravyAccent: 这个列名要不要加重音，因为有些所谓的列名是个mysql函数，比如"length(name) >= 10"里面的length
* value: 值，值的类型分为原始类型(字符串)和`BasicValue`类型两种，原始类型的诉求就是解决比如"`name` = concat(`name`, 1)"
  ，里面的concat函数，其它类型的值就主要使用值的`build`
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

## LikeCriteria

顾名思义，执行like查询

## BasicBinaryOperatorCriteria

二元操作符的查询条件基准类，判等，判不等都是从这个类派生的

# 通过注解生成查询条件

部分情况下属性如何查询是在编程时就设计好的，那么可以通过[criteria](src%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fdata%2Fpersistence%2Fcriteria)
目录中提供的若干注解和[CriteriaBuilder](src%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fdata%2Fpersistence%2Fcriteria%2Fsupport%2FCriteriaBuilder.java)
完成在定义时就约定查询过程

## 字段如何变为查询条件

首先假设的是有一个对象包含了所有要查询的属性，例如

```java
public class UserCriteria {
    private String username;
    private int level;
}
```

分别查询用户名(判等)和等级(>=)。这个参数是dao，也就是mapper中查询用户的输入参数

```java
public interface UserDao {
    List<User> query(@Param("criteria") UserCriteria criteria);
}
```

为了方便逻辑的组合，dao额外接受2个参数，一个参数说明查询条件哪些必须满足，另一个说明查询哪些有一个满足就行了

```java
import java.util.Collection;

public interface UserDao {
    List<User> query(@Param("criteria") UserCriteria criteria, Collection<String> must, Collection<String> should);
}
```

当"must"和"should"
都有内容时，则逻辑是must的要求满足并且should的也要求满足，这种搜索被当前组件作为一种范式使用在各个业务模块中。"must"和"
should"中包含的是`UserCriteria`的属性名。

现在开始编写程序

```java
import java.util.HashMap;

public class UserDaoSqlProvider {
    public String query(@Param("criteria") UserCriteria criteria, Collection<String> must, Collection<String> should) {
        Map<String, DatabaseCriteria> criteriaFieldCriteriaMapping = new HashMap<>();
        if (StringUtils.hasText(criteria.getUsername())) {
            criteriaFieldCriteriaMapping.put("username", new EqualsCriteria(/*列*/"username", /*参数*/"criteria", /*哪个属性*/"username"));
        }
        //...
        must.foreach(
                field -> {
                    if (criteriaFieldCriteriaMapping.get(field) != null) {
                        DatabaseCriteria criteria = criteriaFieldCriteriaMapping.get(field);
                        //执行后续逻辑
                    }
                }
        );
    }
}
```

简单来说就是判断查询条件的每一个字段是否有查询的意义(比如不为空字符串)，如果有意义则转为`DatabaseCriteria`
查询条件，将条件放入一个属性名称<->条件的映射中。 将must和should的属性在映射中进行遍历，如果找到了，就再进行BooleanCriteria的组合。

这种方法比较模式和固定化，仅此可以进行统一抽象

## 将查询与实体进行对应

第一步是声明查询对象时，对象内的每一个属性都是实体声明实现的Trait，从而查询条件与实体对象之间的属性通过java类型关联了起来，防止查询条件与实体之间的属性命名与类型的不一致

## CriteriaProvider

第二步可以定义一个注解，这个注解说明了实体类型的字段如何匹配，比如[Equals](src%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fdata%2Fpersistence%2Fcriteria%2Fannotation%2FEquals.java)
的意思是判等。声明的注解需要一个[CriteriaFactory](src%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fdata%2Fpersistence%2Fcriteria%2Fannotation%2Ffactory%2FCriteriaFactory.java)
与之对应，比如[EqualsFactory](src%2Fmain%2Fjava%2Fio%2Fgardenerframework%2Ffragrans%2Fdata%2Fpersistence%2Fcriteria%2Fannotation%2Ffactory%2FEqualsFactory.java)

```java
public interface CriteriaFactory {
    /**
     * 创建搜索条件
     *
     * @param entityType            实体类型
     * @param criteria              条件参数
     * @param criteriaParameterName 条件参数名
     * @param column                引擎创建出来的lie
     * @param value                 引擎默认给定的值
     * @return 搜索条件
     */
    DatabaseCriteria createCriteria(
            Class<?> entityType,
            Object criteria,
            String criteriaParameterName,
            Column column, BasicValue value
    );
}
```

大部分情况下只有"column"和"value"
会被用到，它们是向工厂传达当前要查询的数据列名称叫什么，以及经过引擎的计算，要查询的值是什么(基本都是#{criteria.fieldName})
这种类型的`FieldNameValue`，工厂类更多的时生产一个上文提到的DatabaseCriteria的实例，比如

```java
public class EqualsFactory implements CriteriaFactory {

    @Override
    public DatabaseCriteria createCriteria(Class<?> entityType, Object criteria, String criteriaParameterName, Column column, BasicValue value) {
        return new EqualsCriteria(column, value);
    }
}
```

引擎拿着这个生产后的实例，将实体属性对应的trait类与查询条件对应起来，一个完整的实例如下

```java

@Trait
public interface Username {
    String username = "";
}

public class UserPo implements Username {
    /**
     * 声明查询方法为判等
     */
    @Equals
    private String username;
    //其他属性
}

//声明同样的trait实现
public class UserCriteria implements Username {
    private String username;
}

public class UserDaoSqlProvider {

    public String query(@Param("criteria") UserCriteria criteria, Collection<Class<?>> must, Collection<Class<?>> should) {
        CriteriaBuilder builder;
        MatchAllCriteria queryCriteria = builder.createCriteria("user", UserPo.class, criteria, "criteria", must, should);
        return StatementBuilder.select(UserPo.class).table("user").where(queryCriteria).build();
    }
}
```

在上面的实例下，`CriteriaBuilder`

* 首先扫描`UserPo`和`UserCriteria`的所有同名属性从而获取到Trait的清单
* 基于criteria查询条件的每一个属性，查看属性是否为空(字符串为空字符串，对象为空指针)，如果不为空则执行下面的逻辑
* 查看属性在实体上的查询方法注解，将实体类型，属性对应的列名，查询条件自身等参数交给注解声明的`CriteriaFactory`生产出来条件实例
* 将条件实例与属性的trait放到一个map中，构建为TraitClass <-> DatabaseCriteria
* 遍历must和should的所有要求的Trait class，组合为最终的查询条件
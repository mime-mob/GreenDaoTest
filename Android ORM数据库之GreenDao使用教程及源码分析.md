<font size=4>**一、简介**</font>
**1.Android ORM介绍**
&emsp;在平时的开发过程中，大家一定会或多或少地接触到 SQLite。然而在使用它时，我们往往需要做许多额外的工作，像编写 SQL 语句与解析查询结果等。所以，适用于 Android 的ORM 框架也就孕育而生了，现在市面上主流的框架有 OrmLite、SugarORM、Active Android、Realm 与 GreenDAO。下面先介绍下当前流行的5种ORM数据库框架：
**1）OrmLite**
&emsp;OrmLite不是Android 平台专用的ORM框架，它是Java ORM。支持JDBC连接，Spring以及Android平台。语法中广泛使用了注解（Annotation）。

**2）SugarORM**
&emsp;SugarORM是Android 平台专用ORM。提供简单易学的APIs。可以很容易的处理1对1和1对多的关系型数据，并通过3个函数save(), delete() 和 find() (或者 findById()) 来简化CRUD基本操作。

**3）Active Android**
&emsp;Active Record（活动目录）是Yii、Rails等框架中对ORM实现的典型命名方式。Active Android 帮助你以面向对象的方式来操作SQLite。

**4）Realm**
&emsp;Realm 是一个将可以使用的Android ORM，基于C++编写，直接运行在你的设备硬件上（不需要被解释），因此运行很快。它同时是开源跨平台的，iOS的代码可以在GitHub找到。

**5）GreenDAO**
&emsp;GreenDAO 是一个将对象映射到 SQLite 数据库中的轻量且快速的 ORM 解决方案。

**2.GreenDao介绍及优点**

**介绍：**
&emsp;GreenDAO就是实现Java对象和SQLite Datebase的一个媒介人，简化了SQLite的操作，而且他的数据库操作效率非常高。可以帮助Android开发者快速将Java对象映射到SQLite数据库的表单中的ORM解决方案，通过使用一个简单的面向对象API，开发者可以对Java对象进行存储、更新、删除和查询。
官网网址：[http://greenrobot.org/greendao/features//](http://greenrobot.org/greendao/features//)
 
**优点：**
1）最大性能（最快的Android ORM），GreenDAO 性能远远高于同类的 ORMLite（可见下图）；
2）简易API，通过Java工程生成的类中方法全都自动写好了，可以直接调用；
3）高度优化，GreenDAO 支持 protocol buffer(protobuf) 协议数据的直接存储，如果你通过 protobuf 协议与服务器交互，将不需要任何的映射；与ORMLite等使用注解方式的ORM框架不同，greenDAO使用Code generation的方式，这也是其性能能大幅提升的原因。
4）最小内存消耗。
![GreenDAO与ORMLite比较](http://img.blog.csdn.net/20160707170516979)

<font size=4>**二、配置**</font>

**配置Java工程**
1）在Android Studio中选择File -> New -> New Module -> Java Library建立GreenDAO Generate工程；并且在该工程build.gradlew里添加以下依赖：
```
compile 'org.greenrobot:greendao-generator:2.2.0'
```
2）在新建的Java工程中新建一个Java类，该Java类用于生成项目所需的Bean、DAO等文件，以下是该类需要写的代码：
```
public class ExampleDaoGenerator {

    public static void main(String[] args) throws Exception {
        // 创建了一个用于添加实体（Bean）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "com.example.jianglei.greendaotestdemo.db.bean");
        // 也可以分别指定生成的 Bean 与 DAO 类所在的目录
        schema.setDefaultJavaPackageDao("com.example.jianglei.greendaotestdemo.db.dao");
        //通过次Schema对象添加的所有实体都不会覆盖自定义的代码
        // schema.enableKeepSectionsByDefault();
        // 创建Schema对象后，就可以使用它添加实体（Bean）了。
        addUser(schema);

        // 最后使用 DAOGenerator 类的 generateAll()方法自动生成代码，根据自己的情况更改输出目录
        new DaoGenerator().generateAll(schema,                   
                         "D:\\WorkSpace_01\\GreenDaoTestDemo\\app\\src\\main\\java");
    }

    /**
     * @param schema
     */
    private static void addUser(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「User」
        Entity note = schema.addEntity("User");
        // 也可以重新给表命名
        // note.setTableName("NODE");
        //单独让某个实体不覆盖自定义的代码
        // note.setHasKeepSections(true);
        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        note.addIdProperty().notNull().primaryKey();
        note.addStringProperty("name").notNull();
        // 与在 Java 中使用驼峰命名法不同，默认数据库中的命名是使用大写和下划线来分割单词的。
        note.addIntProperty("age").notNull();
    }
}
```
完成该类的编写后，点击Run来执行这个类可以看到：
![自动生成所需要的类](http://img.blog.csdn.net/20160707175849613)
![生成目录](http://img.blog.csdn.net/20160707180131383)

<font size=4>**三、工作原理**</font>

四个核心类的功能体系及工作原理如下图所示： 
&emsp;![这里写图片描述](http://img.blog.csdn.net/20160718095024209)&emsp;&emsp;![这里写图片描述](http://img.blog.csdn.net/20160719101743749)

**DaoMaster：**
&emsp;一看名字就知道它是Dao中的最大的官了。它保存了sqlitedatebase对象以及操作DAO classes（注意：不是对象）。其提供了一些创建和删除table的静态方法，其内部类OpenHelper和DevOpenHelper实现了SQLiteOpenHelper并创建数据库的框架。
```
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        UserDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        UserDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UserDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
```
&emsp;从DaoMaster中我们可以发现，DaoMaster除了具有创建表和删除表的两个功能外，还有两个内部类，分别为OpenHelper和DevOpenHelper，而DevOpenHelper继承自OpenHelper，而OpenHelper继承自SQLiteOpenHelper，而重写的onCreate()方法中调用了createAllTables(db,false)；方法来创建数据表，而createAllTables()方法中是通过调用UserDao静态方法来创建表的UserDao.createTable(db, ifNotExists)；我们点进这个方法中去看个究竟：
```
/** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"AGE\" INTEGER NOT NULL );"); // 2: age
    }
```
&emsp;发现它内部就是通过sql语句来创建表的，只不过GreenDAO帮我们封装好了，而且你会发现删除表其实也一样：
```
/** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }
```
&emsp;在DevOpenHelper类中实现了onUpgrade()方法，就是更新数据库的方法，它在更新数据表的时候会把以前的数据表删除后再重新创建，所以这个你必须注意，当我们在利用GreenDAO更新数据表的时候，如果你想以前表中的数据保存下来的话，我们必须自己封装一个方法。接下来就是newSession()方法了，这个当然就是得到DaoSession实例了，关于DaoSession实例，GreenDAO官方建议不要重新创建新的实例，保持一个单例的引用即可。
&emsp;接下来就是看DaoMaster的父类AbstractDaoMaster的源码了，它的源码如下：
```
public abstract class AbstractDaoMaster {
    protected final SQLiteDatabase db;
    protected final int schemaVersion;
    protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap;

    public AbstractDaoMaster(SQLiteDatabase db, int schemaVersion) {
        this.db = db;
        this.schemaVersion = schemaVersion;

        daoConfigMap = new HashMap<Class<? extends AbstractDao<?, ?>>, DaoConfig>();
    }

    protected void registerDaoClass(Class<? extends AbstractDao<?, ?>> daoClass) {
        DaoConfig daoConfig = new DaoConfig(db, daoClass);
        daoConfigMap.put(daoClass, daoConfig);
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    /** Gets the SQLiteDatabase for custom database access. Not needed for greenDAO entities. */
    public SQLiteDatabase getDatabase() {
        return db;
    }

    public abstract AbstractDaoSession newSession();

    public abstract AbstractDaoSession newSession(IdentityScopeType type);
}
```
&emsp;看这个类的代码，最重要的就是这一行了：
```
protected final Map<Class<? extends AbstractDao<?, ?>>, DaoConfig> daoConfigMap;
```
&emsp;这里定义了一个Map集合，Key是继承自AbstractDao类的字节码对象，Value则为DaoConfig对象，而往这个Map集合中put数据是通过这个方法registerDaoClass()，代码在上面可以看到。Map的功能就是为每一个EntityDao字节码对象建立与之对应的db数据库的映射关系，从而管理所有的EntityDao类。
再回到DaoMaster，可以看到DaoMaster类的构造方法中调用了registerDaoClass()，把EntityDao类和相应的数据库db建立关联。

**DaoSession：**
&emsp;操作具体的DAO对象（注意：是对象）。
&emsp;从上面可知DaoSession对象是通过master.newSession();创建的。我们看看DaoSession源码，发现它也有一个抽象的父类AbstractDaoSession，我们来看看DaoSession的源码：
```
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;

    private final UserDao userDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);

        registerDao(User.class, userDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

}
```
&emsp;最主要的一个方法就是通过getUserDao()来得到UserDao实例，而创建一个UserDao对象正是在DaoSession的构造方法中：
```
userDao = new UserDao(userDaoConfig, this);
```
&emsp;这个正是从在DaoMaster创建的Map集合中取出key为UserDao.class的DaoConfig对象，刚刚就说了Map集合中保存了UserDao类对应的数据库db的关系映射，而这个DaoConfig对象正是管理了对应的db对象。然后把这个DaoConfig传给UserDao(userDaoConfig, this)，所以这就说明了我们使用UserDao对象来进行数据库上的CRUD操作而对应的数据库也会变化的原因，这个过程实际上就是在间接操作数据库。
&emsp;下面来就是看看它的父类AbstractDaoSession：
```
public class AbstractDaoSession {
    private final SQLiteDatabase db;
    private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

    public AbstractDaoSession(SQLiteDatabase db) {
        this.db = db;
        this.entityToDao = new HashMap<Class<?>, AbstractDao<?, ?>>();
    }

    protected <T> void registerDao(Class<T> entityClass, AbstractDao<T, ?> dao) {
        entityToDao.put(entityClass, dao);
    }

    /** Convenient call for {@link AbstractDao#insert(Object)}. */
    public <T> long insert(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        return dao.insert(entity);
    }

    /** Convenient call for {@link AbstractDao#insertOrReplace(Object)}. */
    public <T> long insertOrReplace(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        return dao.insertOrReplace(entity);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. */
    public <T> void refresh(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        dao.refresh(entity);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. */
    public <T> void update(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        dao.update(entity);
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. */
    public <T> void delete(T entity) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entity.getClass());
        dao.delete(entity);
    }

    /** Convenient call for {@link AbstractDao#deleteAll()}. */
    public <T> void deleteAll(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entityClass);
        dao.deleteAll();
    }

    /** Convenient call for {@link AbstractDao#load(Object)}. */
    public <T, K> T load(Class<T> entityClass, K key) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
        return dao.load(key);
    }

    /** Convenient call for {@link AbstractDao#loadAll()}. */
    public <T, K> List<T> loadAll(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
        return dao.loadAll();
    }

    /** Convenient call for {@link AbstractDao#queryRaw(String, String...)}. */
    public <T, K> List<T> queryRaw(Class<T> entityClass, String where, String... selectionArgs) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, K> dao = (AbstractDao<T, K>) getDao(entityClass);
        return dao.queryRaw(where, selectionArgs);
    }

    /** Convenient call for {@link AbstractDao#queryBuilder()}. */
    public <T> QueryBuilder<T> queryBuilder(Class<T> entityClass) {
        @SuppressWarnings("unchecked")
        AbstractDao<T, ?> dao = (AbstractDao<T, ?>) getDao(entityClass);
        return dao.queryBuilder();
    }

    public AbstractDao<?, ?> getDao(Class<? extends Object> entityClass) {
        AbstractDao<?, ?> dao = entityToDao.get(entityClass);
        if (dao == null) {
            throw new DaoException("No DAO registered for " + entityClass);
        }
        return dao;
    }

    /**
     * Run the given Runnable inside a database transaction. If you except a result, consider callInTx.
     */
    public void runInTx(Runnable runnable) {
        db.beginTransaction();
        try {
            runnable.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Calls the given Callable inside a database transaction and returns the result of the Callable. If you don't
     * except a result, consider runInTx.
     */
    public <V> V callInTx(Callable<V> callable) throws Exception {
        db.beginTransaction();
        try {
            V result = callable.call();
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Like {@link #callInTx(Callable)} but does not require Exception handling (rethrows an Exception as a runtime
     * DaoException).
     */
    public <V> V callInTxNoException(Callable<V> callable) {
        db.beginTransaction();
        try {
            V result;
            try {
                result = callable.call();
            } catch (Exception e) {
                throw new DaoException("Callable failed", e);
            }
            db.setTransactionSuccessful();
            return result;
        } finally {
            db.endTransaction();
        }
    }

    /** Gets the SQLiteDatabase for custom database access. Not needed for greenDAO entities. */
    public SQLiteDatabase getDatabase() {
        return db;
    }

    /** Allows to inspect the meta model using DAOs (e.g. querying table names or properties). */
    public Collection<AbstractDao<?, ?>> getAllDaos() {
        return Collections.unmodifiableCollection(entityToDao.values());
    }

    /**
     * Creates a new {@link AsyncSession} to issue asynchronous entity operations. See {@link AsyncSession} for details.
     */
    public AsyncSession startAsyncSession() {
        return new AsyncSession(this);
    }

}
```
&emsp;可以看到它的父类中，大部分方法都是进行CRUD操作的，而事实上我们在进行CRUD操作都是通过UserDao对象来进行的，实际上这两种做法没有区别，因为它内部本身就是通过dao对象来进行CRUD操作的，大家看看这些方法的返回值就知道了。 
&emsp;在DaoSession和UserDao调用CRUD操作中，查询操作比较特殊，原因是GreenDao在查询这块加了缓存 ，GreenDao在查询时使用了弱引用WeakReference，对已经查询过的数据，先查询这个引用而不是查询数据库（前提是没有GC），速度更快。
&emsp;这个缓存的代码是在AbstractQueryData类中，如下：
```
Q forCurrentThread() {
        int threadId = Process.myTid();
        if (threadId == 0) {
            // Workaround for Robolectric, always returns 0
            long id = Thread.currentThread().getId();
            if (id < 0 || id > Integer.MAX_VALUE) {
                throw new RuntimeException("Cannot handle thread ID: " + id);
            }
            threadId = (int) id;
        }
        synchronized (queriesForThreads) {
            WeakReference<Q> queryRef = queriesForThreads.get(threadId);
            Q query = queryRef != null ? queryRef.get() : null;
            if (query == null) {
                gc();
                query = createQuery();
                queriesForThreads.put(threadId, new WeakReference<Q>(query));
            } else {
                System.arraycopy(initialValues, 0, query.parameters, 0, initialValues.length);
            }
            return query;
        }
    }
```

**UserDao：**
&emsp;实际生成的UserDao类，通常对应具体的java类，其有更多的权限和方法来操作数据库元素。
先看看UserDao类的源码：
```
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "USER";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Age = new Property(2, int.class, "age", false, "AGE");
    }


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"AGE\" INTEGER NOT NULL );"); // 2: age
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
        stmt.bindLong(3, entity.getAge());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.getInt(offset + 2) // age
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setAge(cursor.getInt(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
```
&emsp;除了之前讲的createTable和dropTable方法以外，比较重要的就是bindValues()这个方法，它是用来绑定实体的属性名和表中的字段名的；Property是用来得到这个属性对应表中的列名、是否为主键等值，这个为查询、更新等提供了条件。UserDao的父类AbstractDao源码中主要是一些CRUD方法和其它的一些方法，这里就不再多作介绍了。

**User：**
&emsp;对于实体类，这没什么可讲的，就是一个Bean，一个实体类对应一张表，实体类里面有对应各个字段的getter和setter方法。通常代表了一个数据库row的标准java properties。

<font size=4>**四、项目使用**</font>

&emsp;前面很大篇幅讲了些工作原理的分析，下面就到了项目使用阶段，相信只要理解上面的工作原理，大家使用起来就非常顺手了，而且GreenDao封装了很多简易的CRDU方法。

**基本使用**
&emsp;我们首先来看看使用GreenDAO的基本步骤：
```
// 生成数据库文件，名为user-db
DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "students-db", null);
SQLiteDatabase db = helper.getWritableDatabase();
// 建立特定模式下的所有的DAO对象和数据库db对象的映射
DaoMaster master = new DaoMaster(db);
// 管理特定模式下的所有DAO对象，并提供一些通用的CRUD持久化方法
DaoSession session = master.newSession();
// 得到指定的UserDao对象
UserDao dao = session.getUserDao();
dao.insert(student);
//...
```
&emsp;GreenDao不仅数据库创建帮我们写好，也提供了很多CRDU方法，大家可以获取dao对象后通过"."这个看到有很多方法供使用，这些都是基本用法，下面介绍一些常用的：

```
/*
增加：
dao.insert(Student entity);//添加一个
dao.insertInTx(Student...entity);//批量添加

删除：
dao.deleteByKey(Long key);//根据主键删除
dao.deleteByKeyInTx(Long...keys);//批量删除
dao.delete(Student entity);//根据实体删除
dao.deleteInTx(Student... entities);//

批量删除
dao.deleteAll();//全部删除

修改：
dao.update(Student entity);//根据实体更新
dao.updateInTx(Student...entities);//批量更新

查找：
Query query = dao.queryBuilder().where(StudentDao.Properties.Name.eq(content)).build();
List list = query.list();//或者利用sql语言查询
Query query = dao.queryBuilder().where( new StringCondition("_ID IN "+"(SELECT _ID FROM USER WHERE AGE = 20)").build()
*/
```

**封装使用**
&emsp;好了，上面讲了基本用法，但是想想，我们项目里，不可能用到数据库的地方就初始化这么一大堆吧，而且咱封装的数据库方法也不想暴露给使用者，那就要要自己封装了，下面是我自己研究封装了一个Helper类，大家可以看看：
```
/**
 * Created by jianglei on 2016/6/30.
 */
public class GreenDaoHelper<T> {

    private static final String NB_NAME = "demo";

    private static GreenDaoHelper mGreenDaoInstance;

    private static Context mApplicationContext;

    private DaoMaster.DevOpenHelper mDaoHelper;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;

    private GreenDaoHelper(Context context) {
        mDaoHelper = new DaoMaster.DevOpenHelper(context, NB_NAME, null);
        mDaoMaster = new DaoMaster(mDaoHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public static void initGreenDao(Context context) {
        mApplicationContext = context;
        getInstance();
    }

    public static GreenDaoHelper getInstance() {
        if (mGreenDaoInstance == null) {
            synInit(mApplicationContext);
        }
        return mGreenDaoInstance;
    }

    private synchronized static void synInit(Context context) {
        if (mGreenDaoInstance == null) {
            mGreenDaoInstance = new GreenDaoHelper(context);
        }
    }

    /**
     * 单条插入表
     *
     * @param value 传入bean
     */
    @SuppressWarnings("unchecked")
    public void singleInsert(T value) {
        AbstractDao dao = null;
        try {
            mDaoSession.insert(value);
            dao = mDaoSession.getDao(value.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.insert(value);
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 多条插入表
     *
     * @param values 传入bean的list
     */
    @SuppressWarnings("unchecked")
    public void multiInsert(T values) {
        List<Object> list = null;
        AbstractDao dao = null;
        try {
            list = (List<Object>) values;
            dao = mDaoSession.getDao(list.get(0).getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null && list.size() > 0) {
            dao.insertInTx(list);
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 单条更新
     *
     * @param value 传入bean
     */
    @SuppressWarnings("unchecked")
    public void singleUpdate(T value) {
        AbstractDao dao = null;
        try {
            dao = mDaoSession.getDao(value.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.update(value);
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 多条更新
     *
     * @param values 传入bean的list
     */
    @SuppressWarnings("unchecked")
    public void multiUpdate(T values) {
        List<Object> list = null;
        AbstractDao dao = null;
        try {
            list = (List<Object>) values;
            dao = mDaoSession.getDao(list.get(0).getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null && list.size() > 0) {
            dao.updateInTx(list);
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 查询所有
     *
     * @param clazz 传入所需查询bean的class
     */
    @SuppressWarnings("unchecked")
    public List queryAll(Class clazz) {
        AbstractDao dao = null;
        try {
            dao = mDaoSession.getDao(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            return dao.queryBuilder().list();
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 条件查询
     * @param clazz 传入所需查询bean的class
     * @param condition 传入查询条件
     */
    @SuppressWarnings("unchecked")
    public List queryWithFilter(Class clazz, WhereCondition condition) {
        AbstractDao dao = null;
        try {
            dao = mDaoSession.getDao(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            return dao.queryBuilder().where(condition).list();
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 删除所有
     *
     * @param clazz 传入所需删除bean的class
     */
    @SuppressWarnings("unchecked")
    public void deleteAll(Class clazz) {
        AbstractDao dao = null;
        try {
            dao = mDaoSession.getDao(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dao != null) {
            dao.deleteAll();
        } else {
            throw new IllegalArgumentException("The argument you pass is incorrect!!!");
        }
    }

    /**
     * 释放数据库
     */
    public void closeGreenDao() {
        if (mDaoHelper != null) mDaoHelper.close();
        if (mDaoSession != null) mDaoSession.clear();
    }
}
```
&emsp;大家可以看到，我在封装的时候，只需要传入实体类就行了，那这里又是怎么知道它自己的Dao呢，其实这个就要回到前面讲AbstractDaoSession的时候，大家可以看到里面定义了一个Map和一个registerDao方法：
```
/*AbstractDaoSession*/
private final Map<Class<?>, AbstractDao<?, ?>> entityToDao;

protected <T> void registerDao(Class<T> entityClass, AbstractDao<T, ?> dao) {
        entityToDao.put(entityClass, dao);
}
    
/*DaoMaster*/
public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(UserDao.class);
}
```
&emsp;而在DaoMaster的构造函数的时候注册了User的Dao，我就可以通过bean.getClass()获取到它是哪个实体类clazz，然后通过DaoSession的mDaoSession.getDao(clazz)获取它的Dao，这样获取Dao之后就通过它去CRDU操作，其他的方法我都写了注释，大家可以自己看咯。
&emsp;在项目中使用：
&emsp;1）Application中初始化：
```
GreenDaoHelper.initGreenDao(this);
```
&emsp;2）需要使用数据库的地方调用：
```
GreenDaoHelper.getInstance().(CRDU方法);
```
&emsp;好了，到这里，GreenDao从java项目配置、四大核心类的分析以及项目中使用就这样介绍完毕了，Demo地址：http://download.csdn.net/detail/qq_19711823/9580009，欢迎收看，下期节目再见。
![这里写图片描述](http://img.blog.csdn.net/20160719105116806)



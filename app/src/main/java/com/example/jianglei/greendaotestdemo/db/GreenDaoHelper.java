package com.example.jianglei.greendaotestdemo.db;

import android.content.Context;

import com.example.jianglei.greendaotestdemo.db.dao.DaoMaster;
import com.example.jianglei.greendaotestdemo.db.dao.DaoSession;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.WhereCondition;

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
Query query = dao.queryBuilder().where( new StringCondition("_ID IN "+"(SELECT _ID FROM STUDENT WHERE AGE = 20)").build()

*/
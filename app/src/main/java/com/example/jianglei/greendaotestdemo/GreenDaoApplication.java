package com.example.jianglei.greendaotestdemo;

import android.app.Application;

import com.example.jianglei.greendaotestdemo.db.GreenDaoHelper;

/**
 * Created by jianglei on 2016/6/30.
 */
public class GreenDaoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GreenDaoHelper.initGreenDao(this);
    }

}

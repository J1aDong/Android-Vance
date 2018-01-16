package com.j1adong.lrlv;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by J1aDong on 2018/1/10.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
    }
}
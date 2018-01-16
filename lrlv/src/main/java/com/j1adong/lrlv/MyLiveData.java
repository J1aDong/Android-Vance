package com.j1adong.lrlv;

import android.arch.lifecycle.LiveData;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by J1aDong on 2018/1/11.
 */

public class MyLiveData extends LiveData<String> {

    @Override
    protected void onActive() {
        super.onActive();
        LogUtils.w("onActive()");


    }

    @Override
    protected void onInactive() {
        super.onInactive();
        LogUtils.w("onInactive()");
    }
}

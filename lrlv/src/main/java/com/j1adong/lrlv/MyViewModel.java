package com.j1adong.lrlv;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

/**
 * Created by J1aDong on 2018/1/11.
 */

public class MyViewModel extends ViewModel {
    private MutableLiveData<MyBean> myBean = new MutableLiveData<>();

    public LiveData<MyBean> getMyBean() {
        return myBean;
    }

    public void setMyBean(MyBean myBean) {
        this.myBean.setValue(myBean);
    }
}

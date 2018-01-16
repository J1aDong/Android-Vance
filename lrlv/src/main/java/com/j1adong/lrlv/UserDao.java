package com.j1adong.lrlv;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import io.reactivex.Flowable;

/**
 * Created by J1aDong on 2018/1/15.
 */

@Dao
public interface UserDao {

    @Insert
    void save(MyBean myBean);

    @Query("SELECT * FROM user WHERE id = :userId")
    LiveData<MyBean> load(String userId);

    @Query("SELECT * FROM user WHERE name = :userName")
    MyBean queryName(String userName);

    @Query("SELECT * FROM user")
    Flowable<MyBean[]> queryAll();
}

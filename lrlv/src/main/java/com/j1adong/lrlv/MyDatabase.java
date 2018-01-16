package com.j1adong.lrlv;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by J1aDong on 2018/1/15.
 */

@Database(entities = {MyBean.class}, version = 2)
public abstract class MyDatabase extends RoomDatabase {

    public static final String DB_NAME = "my_user_demo";
    private static MyDatabase sInstance;

    public abstract UserDao userDao();

    public static MyDatabase getDatabase(Context context) {
        if (sInstance == null) {
            synchronized (MyDatabase.class) {
                if (null == sInstance) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class,
                            DB_NAME).build();
                }
            }

        }
        return sInstance;
    }
}

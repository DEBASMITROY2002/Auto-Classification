package com.example.internai_gallery;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MyImg.class}, version = 1, exportSchema = false)

public abstract class MyImgDatabase extends RoomDatabase {

    public abstract MyImgDao imgDao();

    private static volatile MyImgDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MyImgDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyImgDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyImgDatabase.class, "imgDatabase")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

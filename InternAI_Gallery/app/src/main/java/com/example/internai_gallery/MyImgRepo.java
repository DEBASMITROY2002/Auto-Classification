package com.example.internai_gallery;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MyImgRepo {
    private MyImgDao mImgDao;
    private LiveData<List<MyImg>> mAllImgs;


    MyImgRepo(Application application) {
        MyImgDatabase db = MyImgDatabase.getDatabase(application);
        mImgDao = db.imgDao();
        mAllImgs = mImgDao.getAllImgs();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<List<MyImg>> getAllNotes() {
        return mAllImgs;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(MyImg myImg) {
        MyImgDatabase.databaseWriteExecutor.execute(() -> {
            mImgDao.insert(myImg);
        });
    }

    void delete(MyImg myImg) {
        MyImgDatabase.databaseWriteExecutor.execute(() -> {
            mImgDao.delete(myImg);
        });
    }

}

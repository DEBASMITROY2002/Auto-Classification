package com.example.internai_gallery;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MyImgViewModel extends AndroidViewModel {

    private MyImgRepo mRepository;
    private final LiveData<List<MyImg>> mAllImgs;

    public MyImgViewModel (Application application) {
        super(application);
        mRepository = new MyImgRepo(application);
        mAllImgs = mRepository.getAllNotes();
    }

    LiveData<List<MyImg>> getAllImgs() { return mAllImgs; }

    public void insert(MyImg img) { mRepository.insert(img); }
    public void delete(MyImg img) { mRepository.delete(img); }
}

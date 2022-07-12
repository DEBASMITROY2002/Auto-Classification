package com.example.internai_gallery;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyImgDao
{
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insert(MyImg nt);

    //  @Query("DELETE FROM word_table")  same thing
    @Delete
    public void delete(MyImg nt);

    @Query("Select * from imgTable order by path ASC")
    public LiveData<List<MyImg>> getAllImgs();
}

package com.example.internai_gallery;

import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "imgTable")
public class MyImg {
    @NonNull
    @ColumnInfo(name = "label")   // table e nam ki hisebe thakbe
    public String mLabel;

    @NonNull
    @ColumnInfo(name = "prob")   // table e nam ki hisebe thakbe
    public String mProb;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name="path")
    public String mPath;

    public MyImg(@NonNull String label,String prob, String path)
    {
        this.mProb = prob;
        this.mLabel = label;
        this.mPath = path;
    }

    @Override
    public String toString() {
        return "MyImg{" +
                "mLabel='" + mLabel + '\'' +
                ", mProb='" + mProb + '\'' +
                ", mPath='" + mPath + '\'' +
                '}';
    }
}

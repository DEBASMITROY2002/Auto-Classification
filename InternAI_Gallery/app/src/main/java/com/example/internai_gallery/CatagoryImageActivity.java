package com.example.internai_gallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class CatagoryImageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CategoryAdapter imageRVAdapter;
    private ArrayList<String> imagePaths;
    private TextView title;
    public static ArrayList<String> allLabels=new ArrayList<String>(6){
        {
            add("Animals");
            add("Faces");
            add("Persons");
            add("Documents/IDs");
            add("Landscape");
            add("Screenshots And Others");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_catagory_image);

        String token_str = getIntent().getStringExtra("Category");
        int token = Integer.parseInt(token_str);
        title=findViewById(R.id.Catg_title);
        title.setText(allLabels.get(token));
        imagePaths = MainActivity.catg_Paths.get(token);
        prepareRecyclerView();
    }

    private void prepareRecyclerView() {
        recyclerView = findViewById(R.id.Catg_RVImage);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CatagoryImageActivity.this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageRVAdapter = new CategoryAdapter(CatagoryImageActivity.this, imagePaths);
        recyclerView.setAdapter(imageRVAdapter);
    }
}
package com.example.internai_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.StringTokenizer;

public class ImageDetailActivity extends AppCompatActivity {
    String prob;
    String label;
    String imgPath;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_image_detail);
        try{
            String token = getIntent().getStringExtra("conf_label_path");
            StringTokenizer st = new StringTokenizer(token, "^");

            System.out.println("----------------+++++++++++++++++___________________________"+token);

            prob = st.nextToken();
            label = st.nextToken();
            imgPath = st.nextToken();

            imageView = findViewById(R.id.imageDetailView);
            textView = findViewById(R.id.DetailTextCaption);

            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(imageView);
                textView.setText(label + prob);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Please Wait",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
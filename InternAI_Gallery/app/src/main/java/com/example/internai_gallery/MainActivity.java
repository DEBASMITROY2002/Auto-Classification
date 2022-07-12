package com.example.internai_gallery;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.internai_gallery.ml.AnHfHsIdLs;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    RecyclerView recyclerView;
    RecyclerViewAdapter imageRVAdapter;
    private ArrayList<String> imagePaths;

    private ArrayList<String>predited_labels;
    private ArrayList<String> best_probs;
    private Map<String, Integer>path_to_pos;

    public  ArrayList<String>labels;

    public static ArrayList<ArrayList<String>>catg_Paths;
    public   ArrayList<ImageView>catg_Buttons;

    //Databse
    public static MyImgViewModel myImgViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        imagePaths = new ArrayList<>();
        predited_labels = new ArrayList<String>();
        best_probs = new ArrayList<String>();
        path_to_pos = new HashMap<String,Integer>();
        myImgViewModel = new ViewModelProvider(this).get(MyImgViewModel.class);

        labels = new ArrayList<String>(){
            {
                add("Animal");
                add("Face");
                add("Person");
                add("Private");
                add("Landscape");
            }
        };

        catg_Paths = new ArrayList<ArrayList<String> >(6);
        for(int i = 0; i<6;i++) {
            ArrayList<String> t=new ArrayList<String>();
            catg_Paths.add(t);
        }

        catg_Buttons = new ArrayList<ImageView>(6);
        catg_Buttons.add(findViewById(R.id.imageIcon00));
        catg_Buttons.add(findViewById(R.id.imageIcon01));
        catg_Buttons.add(findViewById(R.id.imageIcon02));
        catg_Buttons.add(findViewById(R.id.imageIcon12));
        catg_Buttons.add(findViewById(R.id.imageIcon10));
        catg_Buttons.add(findViewById(R.id.imageIcon11));


        for(int i = 0 ; i<6; i++){
            final int ind = i;
            catg_Buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ind==3) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_lock_lock)
                                .setTitle("Protected Content")
                                .setMessage("Do you definitely want to open? ")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int in)  /// when yes
                                    {
                                        Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Context context = getApplicationContext();
                                        try {
                                            i.putExtra("Category", Integer.toString(ind));
                                            context.startActivity(i);
                                        } catch (Exception e) {
                                            Toast.makeText(context, "Catg Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .setNegativeButton("No",null) /// when no  you may add here toast
                                .show();
                    }
                    else {
                        Intent i = new Intent(getApplicationContext(), CatagoryImageActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Context context = getApplicationContext();
                        try {
                            i.putExtra("Category", Integer.toString(ind));
                            context.startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(context, "Catg Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        requestPermissions();
        prepareRecyclerView();
    }

    private void requestPermissions() {
        requestPermission();
    }

    private void requestPermission() {
        //on below line we are requesting the rea external storage permissions.
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }


    private void prepareRecyclerView(){
        recyclerView = findViewById(R.id.idRVImages);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageRVAdapter = new RecyclerViewAdapter(MainActivity.this, imagePaths,predited_labels, best_probs, path_to_pos);
        recyclerView.setAdapter(imageRVAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // we are checking the permission code.
            case PERMISSION_REQUEST_CODE:
                // in this case we are checking if the permissions are accepted or not.
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        // if the permissions are accepted we are displaying a toast message
                        // and calling a method to get image path.
                        //Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show();
                        getImagePath();
                    } else {
                        // if permissions are denied we are closing the app and displaying the toast message.
                        Toast.makeText(this, "Permissions denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void getImagePath() {
        // in this method we are adding all our image paths
        // in our arraylist which we have created.
        // on below line we are checking if the device is having an sd card or not.
        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            // if the sd card is present we are creating a new list in
            // which we are getting our images data with their ids.
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

            // on below line we are creating a new
            // string to order our images by string.
            final String orderBy = MediaStore.Images.Media.DATE_TAKEN;

            // this method will stores all the images
            // from the gallery in Cursor
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

            // below line is to get total number of images
            int count = cursor.getCount();

            // on below line we are running a loop to add
            // the image file path in our array list.
            for (int i = 0; i < count; i++) {

                // on below line we are moving our cursor position
                cursor.moveToPosition(i);

                // on below line we are getting image file path
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                // after that we are getting the image file path
                // and adding that path in our array list.
                imagePaths.add(cursor.getString(dataColumnIndex));
            }
            cursor.close();

            imagePaths = reverseArrayList(imagePaths);
            for(int i = 0 ;i<imagePaths.size();i++){
                predited_labels.add("");
                best_probs.add("");
                path_to_pos.put(imagePaths.get(i),i);
                System.out.println("~~~~~~~~~~~~~~~~~~~~>>>>>>>"+path_to_pos.get(imagePaths.get(i)));;
            }

            myImgViewModel.getAllImgs().observe(this, imgs -> {
                // Update the cached copy of the words in the adapter.
                imageRVAdapter.updateImgs(imgs);
            });

            imageRVAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<String> reverseArrayList(ArrayList<String> alist)
    {
        for (int i = 0; i < alist.size() / 2; i++) {
            String temp = alist.get(i);
            alist.set(i, alist.get(alist.size() - i - 1));
            alist.set(alist.size() - i - 1, temp);
        }
        return alist;
    }
}
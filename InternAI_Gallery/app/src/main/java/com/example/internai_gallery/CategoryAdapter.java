package com.example.internai_gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internai_gallery.ml.AnHfHsIdLs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<MyCatgViewHolder> {
    private final Context context;
    private final ArrayList<String> imagePathArrayList;

    public CategoryAdapter(Context context, ArrayList<String> imagePathArrayList) {
        this.context = context;
        this.imagePathArrayList = removeDuplicates(imagePathArrayList);
    }

    @NonNull
    @Override
    public MyCatgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_card, parent, false);
        return new MyCatgViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyCatgViewHolder holder, int pos) {
        final int position = pos;
        File imgFile = new File(imagePathArrayList.get(position));

        if (imgFile.exists()){
            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.imageView_primary);
            RequestCreator rc = Picasso.get().load(imgFile);

            holder.imageView_primary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ImageDetailActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("conf_label_path", " "+"^"+" "+"^"+imagePathArrayList.get(position));
                    context.startActivity(i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imagePathArrayList.size();
    }

    private  <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
        Set<T> set = new LinkedHashSet<>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }
}



class MyCatgViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView_primary;
    public TextView textView_caption;

    public MyCatgViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_primary = itemView.findViewById(R.id.idIVImage);
        textView_caption = itemView.findViewById(R.id.imgCaption);
    }

}

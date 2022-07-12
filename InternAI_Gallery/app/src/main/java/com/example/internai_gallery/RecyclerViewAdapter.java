package com.example.internai_gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Context context;
    private final ArrayList<String> imagePathArrayList;
    private final Map<String, Integer>path_to_pos;
    private  Map<String, Integer>label_to_indx;

    public  final ArrayList<String>labels;

    public  final ArrayList<String> predeicted_Labels;
    public  final ArrayList<String> best_Probs;

    public RecyclerViewAdapter(Context context, ArrayList<String> imagePathArrayList,ArrayList<String> pLabels, ArrayList<String> bProbs, Map<String, Integer>path_to_pos) {
        this.context = context;
        this.imagePathArrayList = imagePathArrayList;
        this.predeicted_Labels = pLabels;
        this.best_Probs = bProbs;
        this.path_to_pos = path_to_pos;

        this.labels =new ArrayList<String>();
        this.label_to_indx = new HashMap<>();

        labels.add("Animal");
        labels.add("Face");
        labels.add("Person");
        labels.add("Private");
        labels.add("Landscape");

        label_to_indx.put("Animal", 0);
        label_to_indx.put("Face", 1);
        label_to_indx.put("Person", 2);
        label_to_indx.put("Private", 3);
        label_to_indx.put("Landscape", 4);
        label_to_indx.put("Screenshot", 5);
        label_to_indx.put("Others", 5);

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Callledd");
        System.out.println(imagePathArrayList.size());
        System.out.println(best_Probs.size());
        System.out.println(predeicted_Labels.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.img_card, parent, false);
        return new MyViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        final int position = pos;
        File imgFile = new File(imagePathArrayList.get(position));

        if (imgFile.exists()){
            Picasso.get().load(imgFile).placeholder(R.drawable.ic_launcher_background).into(holder.imageView_primary);
            RequestCreator rc = Picasso.get().load(imgFile);
            if(imagePathArrayList.get(pos).contains("Screenshot")){
                predeicted_Labels.set(pos,"Screenshot");
                best_Probs.set(pos,"100.00 %");
                holder.textView_caption.setText("Screenshot");
                MainActivity.catg_Paths.get(5).add(imagePathArrayList.get(pos));
                MainActivity.myImgViewModel.insert(new MyImg("Screenshot", Float.toString(100)+ " %", imagePathArrayList.get(pos)));
            }
            else if(predeicted_Labels.get(pos)=="") {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(5000);
                                Activity activity = (Activity) context;
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                            Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
                                            float[] probs = getProbabilties(img, 224, 224);
                                            int best_ind = getBest(probs);
                                            String labelText="Others";

                                            if(best_ind==3)
                                                holder.imageView_primary.setImageResource(R.drawable.lock_view);
                                            if(probs[best_ind]>=0.80) {
                                                labelText = labels.get(best_ind);
                                                MainActivity.catg_Paths.get(best_ind).add(imagePathArrayList.get(pos));
                                                MainActivity.myImgViewModel.insert(new MyImg(labelText, Float.toString(probs[best_ind] * 100)+ " %", imagePathArrayList.get(pos)));
                                            }else {
                                                MainActivity.catg_Paths.get(5).add(imagePathArrayList.get(pos));
                                                MainActivity.myImgViewModel.insert(new MyImg("Others", Float.toString(probs[best_ind] * 100)+ " %", imagePathArrayList.get(pos)));
                                            }

                                            holder.textView_caption.setText(labelText);
                                            predeicted_Labels.set(position, labelText);
                                            best_Probs.set(position, Float.toString(probs[best_ind] * 100) + " %");

                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, "Index Error ", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, "Caption Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Big Error ", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                };
                thread.start();
            }else{
                    try {
                        holder.textView_caption.setText(predeicted_Labels.get(position));
                        if(predeicted_Labels.get(position).equals("Private"))
                            holder.imageView_primary.setImageResource(R.drawable.lock_view);
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(context, "Not In List "+predeicted_Labels.get(pos),Toast.LENGTH_SHORT).show();
                    }

            }

            holder.imageView_primary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(predeicted_Labels.get(pos).equals("Private")||predeicted_Labels.get(pos).equals(""))) {
                        Intent i = new Intent(context, ImageDetailActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            i.putExtra("conf_label_path", "\nConfidence: " + best_Probs.get(pos) + "^" + holder.textView_caption.getText() + "^" + imagePathArrayList.get(position));
                            context.startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(context, "Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }else if(predeicted_Labels.get(pos).equals("Private")){
                        Intent i = new Intent(context, LogInActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        try {
                            i.putExtra("Category", Integer.toString(3));
                            context.startActivity(i);
                        } catch (Exception e) {
                            Toast.makeText(context, "Catg Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    int getBest(@NonNull float[] probs){
        int indx = 0;
        double prob = 0.0;
        for(int i=0;i<probs.length;i++){
            if(prob<probs[i]){
                indx = i;
                prob = probs[i];
            }
        }
        return indx;
    }

    float[] getProbabilties(Bitmap _img, int w, int h) throws IOException {
        Bitmap img_ = Bitmap.createScaledBitmap(_img, w, h, true);
        AnHfHsIdLs model = AnHfHsIdLs.newInstance(context);
        TensorImage tensorImage = new TensorImage(DataType.UINT8);
        tensorImage.load(img_);
        ByteBuffer byteBuffer = tensorImage.getBuffer();

        // Creates inputs for reference.
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, w, h, 3},DataType.UINT8);
        inputFeature0.loadBuffer(byteBuffer);

        // Runs model inference and gets result.
        AnHfHsIdLs.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        model.close();
        return outputFeature0.getFloatArray();
    }


    @Override
    public int getItemCount() {
        return imagePathArrayList.size();
    }

    public void updateImgs(List<MyImg> imgs) {

//        for(int i = 0; i<predeicted_Labels.size();i++) {
//            predeicted_Labels.set(i,"");
//            best_Probs.set(i,"");
//        }

            System.out.println("-------------------------------------------------------->" + imgs.size());
            for (int i = 0; i < imgs.size(); i++) {
                try {
                    MyImg myImg = imgs.get(i);
                    System.out.println("-------------------------------------------------------->" + myImg);
                    int pos = path_to_pos.get(myImg.mPath);
                    String label = myImg.mLabel;
                    String prob = myImg.mProb;

                    predeicted_Labels.set(pos, label);
                    best_Probs.set(pos, prob);
                    if(!MainActivity.catg_Paths.contains(label_to_indx.get(label)))
                        MainActivity.catg_Paths.get(label_to_indx.get(label)).add(imagePathArrayList.get(pos));
                }catch (Exception e){
                    //Toast.makeText(context, "New Image Found!!",Toast.LENGTH_SHORT).show();
                }
            }
            notifyDataSetChanged();
    }
}



class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView_primary;
    public TextView textView_caption;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_primary = itemView.findViewById(R.id.idIVImage);
        textView_caption = itemView.findViewById(R.id.imgCaption);
    }

}

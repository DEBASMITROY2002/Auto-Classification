package com.example.internai_gallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class LogInActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPreferences  = this.getSharedPreferences("com.example.internai_gallery", Context.MODE_PRIVATE);

        final String[] saved_pwd = new String[1];
        saved_pwd[0] = "0000";
        try {
            saved_pwd[0] =(String) ObjectSeriallizer.deserialize(sharedPreferences.getString("pwd",ObjectSeriallizer.serialize(new String())));
        } catch (Exception e) {
            Toast.makeText(this,"NULL SP",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        System.out.println("saved_pwd ------------------------------ >"+ saved_pwd[0]);
        //saved_pwd = "0000";

        String token_str = getIntent().getStringExtra("Category");
        EditText editText = findViewById(R.id.editTextNumberPassword);
        Button cont_but = findViewById(R.id.cont_button);
        Button chng_but = findViewById(R.id.chng_button);
        String finalSaved_pwd = saved_pwd[0];


        cont_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String s = editText.getText().toString();
                if(s.equals(finalSaved_pwd)){
                    Intent i = new Intent(getApplicationContext(), CatagoryImageActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Context context = getApplicationContext();
                    try {
                        i.putExtra("Category", token_str);
                        context.startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(context, "Catg Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                    }finally {
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Wrong Pin",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        chng_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button conf_but_0 = findViewById(R.id.chng_button);
                conf_but_0.setText("Confirm Previous Pin");
                editText.setText("");
                editText.setHint("Enter Previous Pin");
                String s = editText.getText().toString();
                System.out.println("---pppppppppppp----->"+finalSaved_pwd);
                if(s.equals(finalSaved_pwd)){
                    editText.setHint("Enter New Pin");
                    Button conf_but = findViewById(R.id.chng_button);
                    conf_but.setText("Confirm New Pin");
                    conf_but.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String ns = editText.getText().toString();
                            saved_pwd[0] = ns;

                            try {
                                sharedPreferences.edit().putString("pwd",ObjectSeriallizer.serialize(ns)).apply();
                                Toast.makeText(getApplicationContext(),"Pin Changed",Toast.LENGTH_SHORT).show();
                                Button conf_but_0 = findViewById(R.id.chng_button);
                                conf_but_0.setText("Change Pin");
                                finish();
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),"Oopppss!!",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                finish();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Enter Previous Pin Properly",Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        });
    }
}
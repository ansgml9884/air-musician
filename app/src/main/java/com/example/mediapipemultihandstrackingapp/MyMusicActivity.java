package com.example.mediapipemultihandstrackingapp;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MyMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mymusic);

        TextView textView = (TextView)findViewById(R.id.test_textview);
        ImageView imageView = (ImageView)findViewById(R.id.test_imageview);

        String text = "";
        for(int i=0; i<100; i++)
            text += i + "\n";
        textView.setText(text);

    }
}
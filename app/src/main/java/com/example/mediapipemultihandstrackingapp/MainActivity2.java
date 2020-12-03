package com.example.mediapipemultihandstrackingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity2  extends AppCompatActivity {

    private final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
    public void record_video(View view){
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilepath();
        Uri uri = Uri.fromFile(video_file);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(camera_intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "비디오 저장성공", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "비디오 저장실패", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public File getFilepath(){
        File folder = new File("sdcard/video_app");
        if(!folder.exists()){
            folder.mkdir();
        }

        File video_file = new File(folder, "sample_video.mp4");

        return video_file;
    }
}
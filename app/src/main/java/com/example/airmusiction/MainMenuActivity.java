package com.example.airmusiction;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.airmusiction.ui.gallery.GalleryFragment;
import com.example.airmusiction.ui.slideshow.SlideshowFragment;

public class MainMenuActivity extends AppCompatActivity {
    private final Handler mHidenHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main_menu);

//        setFullScreen();

        Button playBtn = (Button)findViewById(R.id.playBtn);
        Button mymusicBtn = (Button)findViewById(R.id.mymusicBtn);
        Button helpBtn = (Button)findViewById(R.id.helpBtn);
        ImageButton setting = (ImageButton)findViewById(R.id.setting);;

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });

        mymusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MyMusicActivity.class);
                startActivity(intent);
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        HelpActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        SettingActivity.class);
                startActivity(intent);
            }
        });

    }
//    private void setFullScreen(){
//        View view;
//        view =  findViewById(R.id.activity_main_menu);
//        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//    }
//    public void onClick_Main(View view){
//        setFullScreen();
//    }

}

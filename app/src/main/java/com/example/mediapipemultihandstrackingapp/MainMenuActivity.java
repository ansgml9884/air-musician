package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.scwang.wave.MultiWaveHeader;

public class MainMenuActivity extends AppCompatActivity {
    private final Handler mHidenHandler = new Handler();
    MultiWaveHeader waveHeader, waveFooter;

    public static LottieAnimationView satrAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);
        //음표 lottie
        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("music.json");
        animationView.loop(true);
        animationView.playAnimation();


        //별 lottie
        satrAnimationView = (LottieAnimationView)findViewById(R.id.star_animation_view);
        satrAnimationView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        DeveloperActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        //웨이브 이펙트
        waveHeader = findViewById(R.id.wave_header);
        waveFooter = findViewById(R.id.wave_footer);

        waveHeader.setVelocity(10);
        waveHeader.setProgress(1);
        waveHeader.isRunning();
        waveHeader.setGradientAngle(45);
        waveHeader.setWaveHeight(40);
        waveHeader.setStartColor(Color.rgb(250, 67, 67));
        waveHeader.setCloseColor(Color.rgb(83, 122, 230));

        waveFooter.setVelocity(10);
        waveFooter.setProgress(1);
        waveFooter.isRunning();
        waveFooter.setGradientAngle(45);
        waveFooter.setWaveHeight(40);
        waveFooter.setStartColor(Color.rgb(250, 80, 148));
        waveFooter.setCloseColor(Color.rgb(241, 250, 67));

        Button playBtn = (Button)findViewById(R.id.playBtn);
        Button mymusicBtn = (Button)findViewById(R.id.mymusicBtn);
        Button helpBtn = (Button)findViewById(R.id.helpBtn);
        ImageButton setting = (ImageButton)findViewById(R.id.setting);

        //메뉴 버튼을 누르면 화면이동
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        mymusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MyMusicActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        HelpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    //버튼을 누를시 바운스 효과
//    public void tapToAnimate(View view){
//        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
//        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
//        animation.setInterpolator(interpolator);
//        playBtn.startAnimation(animation);
//    }


}

package com.example.mediapipemultihandstrackingapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.scwang.wave.MultiWaveHeader;

import java.io.File;

public class MainMenuActivity extends AppCompatActivity {
    private final Handler mHidenHandler = new Handler();
    MultiWaveHeader waveHeader, waveFooter;
    final int PERMISSIONS_REQUEST_CODE = 1;
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

        requestPermission();

    }

    //버튼을 누를시 바운스 효과
//    public void tapToAnimate(View view){
//        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);
//        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2,20);
//        animation.setInterpolator(interpolator);
//        playBtn.startAnimation(animation);
//    }

    // 권한 요청 메서드
    private boolean requestPermission() {
        boolean shouldProviceRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//사용자가 이전에 거절한적이 있어도 true 반환
        if (shouldProviceRationale) {
            //앱에 필요한 권한이 없다면 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        } else {
            //있어도 권한 요청 ?? 왠지모름
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        }
        return true;
    }


    // ActivityCompat.requestPermissions가 호출된 후 호출되는 callback 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // 퍼미션을 거절했다면 permession[]의 길이가 0
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeDir();
                } else {
                    //사용자가 권한 거절시 권환하면으로 넘어감
                    denialDialog();
                }
                return;
            }
        }
    }

    public void denialDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("저장소 권한이 필요합니다. 환경 설정에서 저장소 권한을 허가해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent); //확인버튼누르면 바로 어플리케이션 권한 설정 창으로 이동하도록
                    }
                })
                .create()
                .show();
    }

    //어플시작할때 폴더만들기
    public void makeDir() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath(); //내장에 만든다
        String directoryName = "AirMusician";
        final File myDir = new File(root + "/" + directoryName);
        if (!myDir.exists()) {
            boolean wasSuccessful = myDir.mkdir();
            if (!wasSuccessful) {
                System.out.println("file: was not successful.");
            } else {
                System.out.println("file: 최초로 앨범파일만듬." + root + "/" + directoryName);
            }
        } else {
            System.out.println("file: " + root + "/" + directoryName +"already exists");
        }
    }

}

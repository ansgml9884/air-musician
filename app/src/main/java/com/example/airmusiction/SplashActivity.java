package com.example.airmusiction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //가로고정

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);

    }

    private class splashhandler implements  Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MainMenuActivity.class));
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }

    public void onBackPressed() {
        //스플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }

}

package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);

    }



    private class splashhandler implements Runnable {
        public void run(){
            startActivity(new Intent(getApplication(), MainMenuActivity.class));
            SplashActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
            //(나중에불려지는 액티비티, 현재액티비티)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


        }
    }

    public void onBackPressed() {
        //스플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }



}

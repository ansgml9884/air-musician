package com.example.mediapipemultihandstrackingapp.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mediapipemultihandstrackingapp.R;

public class DeveloperActivity extends AppCompatActivity {
    VideoView videoView;
    private View decorView;
    private int	uiOption;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_developer);
        //하단 바(소프트키) 없애기
        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );


        //배경 애니메이션
        LottieAnimationView backAnimationView = (LottieAnimationView) findViewById(R.id.dev_background);
        backAnimationView.setAnimation("background1.json");
        backAnimationView.loop(true);
        backAnimationView.playAnimation();


        ImageButton backImageBtn = (ImageButton)findViewById(R.id.back_img_btn);
        backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        //비디오 뷰
        videoView = findViewById(R.id.junsoo);
        Uri videoUri;
        videoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sing_junsoo_cutting);
        //비디오뷰 재생,일시정지 등 할수있는 컨트롤바
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(videoUri);
        //동영상 읽어오는 시간이 좀 걸리므로
        //비디오 로딩준비 끝났을때 실행하도록 리스너 설정
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });

    }
    //화면이 안보일때
    protected void onPause(){
        super.onPause();

        if(videoView != null && videoView.isPlaying())videoView.pause();
    }
    //엑티비티가 메모리에서 사라질때
    protected void Destroy(){
        super.onDestroy();
        if(videoView != null)videoView.stopPlayback();
    }
}

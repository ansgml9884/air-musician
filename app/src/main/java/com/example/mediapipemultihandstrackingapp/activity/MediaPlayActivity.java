package com.example.mediapipemultihandstrackingapp.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mediapipemultihandstrackingapp.R;


public class MediaPlayActivity extends AppCompatActivity {

    private VideoView videoView;
    private Uri mediaUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player);
        Intent intent = getIntent() ;
        // No 값을 int 타입에서 String 타입으로 변환하여 표시.
        videoView = findViewById(R.id.videoView) ;
        String url = intent.getStringExtra("videoUrl") ;
        mediaUri = Uri.parse(url);
        //비디오뷰 재생,일시정지 등 할수있는 컨트롤바
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(mediaUri);
        //동영상 읽어오는 시간이 좀 걸리므로
        //비디오 로딩준비 끝났을때 실행하도록 리스너 설정
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
    }

    public void onBackButttonClicked(View view){
        finish();
    }
}

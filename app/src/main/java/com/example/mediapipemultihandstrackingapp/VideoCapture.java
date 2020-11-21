package com.example.mediapipemultihandstrackingapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoCapture extends AppCompatActivity {

    private static String EXTERNAL_STORAGE_PATH = "";
    private static String RECORDED_FILE = "video_recorded";
    private static int fileIndex = 0;
    private static String filename = "";

    MediaPlayer player;
    MediaRecorder recorder;

    // 카메라 상태를 저장하고 있는 객체
    private Camera camera = null;

    SurfaceView surfaceView;
    SurfaceHolder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 외장메모리가 있는지 확인한다.
        // Environment.getExternalStorageState() 를 통해서 현재 외장메모리를 상태를 알수있다.
        String state = Environment.getExternalStorageState();
        // Environment.MEDIA_MOUNTED 외장메모리가 마운트 flog
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "외장 메모리가 마운트 되지않았습니다.", Toast.LENGTH_LONG).show();
        } else {
            EXTERNAL_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }


        // SurfaceView 클래스 객체를 이용해서 카메라에 받은 녹화하고 재생하는데 쓰일것이다.
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        // SurfaceView 클래스를 컨트롤하기위한 SurfaceHolder 생성
        holder = surfaceView.getHolder();
        // 버퍼없음
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button recordBtn = (Button) findViewById(R.id.recordBtn);
        Button recordStopBtn = (Button) findViewById(R.id.recordStopBtn);
        Button playBtn = (Button) findViewById(R.id.playBtn);
        Button playStopBtn = (Button) findViewById(R.id.playStopBtn);

        // 녹화 시작 버튼
        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    // 녹화 시작을 위해  MediaRecorder 객체 recorder를 생성한다.
                    if (recorder == null) {
                        recorder = new MediaRecorder();
                    }
                    // 오디오와영상 입력 형식 설정
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                    // 오디오와영상 인코더 설정
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

                    // 저장될 파일 지정
                    filename = createFilename();
                    recorder.setOutputFile(filename);

                    // 녹화도중에 녹화화면을 뷰에다가 출력하게 해주는 설정
                    recorder.setPreviewDisplay(holder.getSurface());

                    // 녹화 준비,시작
                    recorder.prepare();
                    recorder.start();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    recorder.release();
                    recorder = null;
                }
            }
        });

        recordStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (recorder == null)
                    return;
                // 녹화 중지
                recorder.stop();

                // 영상 재생에 필요한 메모리를 해제한다.
                recorder.release();
                recorder = null;

                ContentValues values = new ContentValues(10);

                values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
                values.put(MediaStore.Audio.Media.ALBUM, "Video Album");
                values.put(MediaStore.Audio.Media.ARTIST, "Mike");
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Video");
                values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Audio.Media.DATA, filename);

                Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                if (videoUri == null) {
                    Log.d("SampleVideoRecorder", "Video insert failed.");
                    return;
                }

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));

            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 영상 재생 방법
                if (player == null) {
                    // 영상 플레이를 위해 MediaPlayer 클래스 객체를 생성한다
                    player = new MediaPlayer();
                }

                try {
                    // 플레이할 파일 설정
                    player.setDataSource(filename);

                    // 플레이할 뷰 설정
                    player.setDisplay(holder);

                    // 플레이 준비,시작
                    player.prepare();
                    player.start();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "영상이 재생 도중 예외가 발생했습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });


        playStopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // 영상 중지 방법
                if (player == null)
                    return;
                // 영상 중지
                player.stop();

                // 메모리 해제
                player.release();
                player = null;
            }
        });


    }


    private String createFilename() {
        fileIndex++;

        String newFilename = "";
        if (EXTERNAL_STORAGE_PATH == null || EXTERNAL_STORAGE_PATH.equals("")) {
            // 내장 메모리를 사용합니다.
            newFilename = RECORDED_FILE + fileIndex + ".mp4";
        } else {
            // 외장 메모리를 사용합니다.
            newFilename = EXTERNAL_STORAGE_PATH + "/" + RECORDED_FILE + fileIndex + ".mp4";
        }

        return newFilename;
    }


    // 액티비티가 onPause 상태일때 녹화,재생에 필요한 모든 객체들의 메모리를 해제한다
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }

        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
}
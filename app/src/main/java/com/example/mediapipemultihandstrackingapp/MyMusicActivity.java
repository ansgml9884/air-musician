package com.example.mediapipemultihandstrackingapp;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediapipemultihandstrackingapp.adapter.MyMusicListAdapter;
import com.example.mediapipemultihandstrackingapp.model.RecordVideoModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyMusicActivity extends AppCompatActivity {
    private Cursor videocursor;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mymusic);

        //데이터를 recyclerView에 넘겨줄 List
        List<RecordVideoModel> videoList = new ArrayList<RecordVideoModel>();

        //미디어 정보를 받아올 배열
        String[] projection = new String[] {
                MediaStore.Video.Media._ID, // primary Key
                MediaStore.Video.Media.DISPLAY_NAME, // 파일읾
                MediaStore.Video.Media.DURATION, // 영상길이
                MediaStore.Video.Media.SIZE, // 영상크기
                MediaStore.Video.Media.DATE_ADDED // 영상 추가 날짜 !!!추가 가공의 문제가 있음!!!
        };

        String selection=MediaStore.Video.Media.DATA +" like?"; //전체 가져오기
        String[] selectionArgs=new String[]{"%AirMusician%"}; //AirMusician 폴더내에
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int createDate = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn)/1000;
                int size = cursor.getInt(sizeColumn);
                int date = cursor.getInt(createDate);

                // 영상 길이 재가공
                // 시 : 분 : 초 로 작업하기
                int min = duration / 60;
                int hour = min / 60;
                duration= duration % 60;
                min = min % 60;
                String videoDuration = "";
                if(hour > 0 && hour > 9) {
                    videoDuration += String.valueOf(hour)+" : ";
                }else if(hour > 0 && hour <= 9){
                    videoDuration += "0"+String.valueOf(hour)+" : ";
                }else{
                    videoDuration += "00 : ";
                }
                if(min > 0 && min > 9) {
                    videoDuration += String.valueOf(min)+" : ";
                }else if(min > 0 && min <= 9){
                    videoDuration += "0"+String.valueOf(min)+" : ";
                }else{
                    videoDuration += "00 : ";
                }
                if(duration > 9){
                    videoDuration += String.valueOf(duration);
                }else{
                    videoDuration += "0"+String.valueOf(duration);
                }

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // 영상 썸네일 작업
                String videoPath = "/storage/emulated/0/AirMusician/" + name;
                Bitmap thumbnail = null;

                System.out.println(videoPath);

                try {
                    // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 560, 480);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                videoList.add(new RecordVideoModel(contentUri, name, videoDuration, size, date, thumbnail));
            }
        }



        // 리싸이클러뷰 연결
        recyclerView = (RecyclerView) findViewById(R.id.my_music_recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyMusicListAdapter(videoList);
        recyclerView.setAdapter(mAdapter);


    }
}

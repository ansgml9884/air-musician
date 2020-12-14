package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediapipemultihandstrackingapp.activity.MediaPlayActivity;
import com.example.mediapipemultihandstrackingapp.adapter.MyMusicListAdapter;
import com.example.mediapipemultihandstrackingapp.model.RecordMediaModel;
import com.example.mediapipemultihandstrackingapp.util.MediaStoreUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyMusicActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyMusicListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View view;
    private VideoView videoView;
    private ArrayList<RecordMediaModel> videoList;

    private View decorView;
    private int	uiOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_mymusic);

        ImageButton homeBtn = (ImageButton)findViewById(R.id.home_img_btn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
<<<<<<< HEAD

=======
>>>>>>> 9f7ca18c838b11e4e5c13b0f2dbd2bebc9749b0c

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


        //데이터를 recyclerView에 넘겨줄 List
        MediaStoreUtil myMediaStore =  MediaStoreUtil.getInstance(getApplicationContext());
        videoList = myMediaStore.getAll(); // 비디오 전체 리스트 가져오기

        Spinner listSpinner  = findViewById(R.id.list_spinner);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.mylist_sort, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(monthAdapter);
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    videoList = myMediaStore.getAll();
                    mAdapter.changeItem(videoList);
                }else if (position == 1){
                    videoList = myMediaStore.getVideos();
                    mAdapter.changeItem(videoList);
                }else{
                    videoList = myMediaStore.getAudios();
                    mAdapter.changeItem(videoList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 리싸이클러뷰 연결
        recyclerView = (RecyclerView) findViewById(R.id.my_music_recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyMusicListAdapter(videoList);
        recyclerView.setAdapter(mAdapter);

        // RecyclerView의 버튼 이벤트 처리
        mAdapter.setOnItemClickListener(new MyMusicListAdapter.OnItemClickListener() {

            @Override // Media Play 버튼
            public void onPlayClick(View v, int pos) {
                Log.d("aaaa", "버튼을 누른 아이템의 위치는 " + pos + " 의 플레이버튼");
                Intent intent = new Intent(MyMusicActivity.this, MediaPlayActivity.class) ;
                intent.putExtra("videoUrl", "/storage/emulated/0/AirMusician/"+videoList.get(pos).getName());
                startActivity(intent);
            }

            @Override // Media Delete 버튼
            public void onDeleteClick(View v, int pos) {
                Log.d("aaaa", "버튼을 누른 아이템의 위치는 " + pos + " 의 삭제버튼");
                String mediaPath = "/storage/emulated/0/AirMusician/"+videoList.get(pos).getName();
                File file = new File(mediaPath);
                if(file.exists()){
                    if(videoList.size()!=0) {
                        file.delete();

                        // Data 삭제시 MediaStore DB에 있는 데이터 까지 삭제.
                        try {
                            // path를 통해 파일 스캔 후 삭제
                            MediaScannerConnection.scanFile(getApplicationContext(), new String[] { mediaPath },
                                    null, new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            getApplicationContext().getContentResolver()
                                                    .delete(uri, null, null);
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(MyMusicActivity.this, videoList.get(pos).getName()+" 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MyMusicActivity.this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MyMusicActivity.this, "파일이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override // Media 공유 버튼
            public void onShareClick(View v, int pos) {
                //미디어 경로
                String mediaPath = "/storage/emulated/0/AirMusician/"+videoList.get(pos).getName();
                Log.d("aaaa", "버튼을 누른 아이템의 위치는 " + pos + " 의 공유버튼");
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(mediaPath);	// android image path
                sharingIntent.setType("video/*"); // 비디오 타입 공유
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            }
        });
    }

}

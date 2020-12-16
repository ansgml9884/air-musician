package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import me.itangqi.waveloadingview.WaveLoadingView;

public class SettingActivity extends AppCompatActivity {

    private SharedPreferences appData;
    private View decorView;
    private int	uiOption;

    private SeekBar seekbar;
    private WaveLoadingView waveLoadingView;
    private ArrayAdapter<CharSequence> instrumentsSpinAdapt, metronomeSpinAdapt;
    private RadioGroup recordType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

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


        appData = getSharedPreferences("appData", MODE_PRIVATE);
        //load();

        // RadioGroup
        recordType = (RadioGroup) findViewById(R.id.settingRadioGroup);
        recordType.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        // Spinner
        final Spinner instrumentsSpinner = (Spinner)findViewById(R.id.instruments);
        final Spinner metronomeSpinner = (Spinner)findViewById(R.id.metronome);
        instrumentsSpinAdapt = ArrayAdapter.createFromResource(this, R.array.instruments_menu, android.R.layout.simple_spinner_dropdown_item);
        instrumentsSpinAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instrumentsSpinner.setAdapter(instrumentsSpinAdapt);
        instrumentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        metronomeSpinAdapt = ArrayAdapter.createFromResource(SettingActivity.this, R.array.metronome_menu,
                android.R.layout.simple_spinner_dropdown_item);
        metronomeSpinAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metronomeSpinner.setAdapter(metronomeSpinAdapt);

        // AudioManager
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        // SeekBar
        seekbar = (SeekBar)findViewById(R.id.sound);
        seekbar.setMax(nMax);
        seekbar.setProgress(nCurrentVolumn);
        waveLoadingView = (WaveLoadingView)findViewById(R.id.waveLoadingView);
        waveLoadingView.setProgressValue((int)(nCurrentVolumn*6.6));
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            public void onProgressChanged(SeekBar seekBar, int Progress, boolean fromUser){
                //시크바 상태가 변경되었을때  progress = seekbar상태값
                waveLoadingView.setProgressValue((int)(Progress*6.6));
                waveLoadingView.setBottomTitle(String.format("%d%%",(int)(Progress*6.6)));
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, Progress, 0);
            }
        });
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i == R.id.soundBtn){
            }else if(i == R.id.videoBtn){
            }
        }
    };

    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
//        editor.putBoolean("SAVE_LOGIN_DATA", checkBox.isChecked());
//        editor.putString("ID", idText.getText().toString().trim());
//        editor.putString("PWD", pwdText.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    // 설정값을 불러오는 함수
//    private void load() {
//        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
//        // 저장된 이름이 존재하지 않을 시 기본값
//        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
//        id = appData.getString("ID", "");
//        pwd = appData.getString("PWD", "");
//    }

}





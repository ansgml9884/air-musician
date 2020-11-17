package com.example.mediapipemultihandstrackingapp;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import me.itangqi.waveloadingview.WaveLoadingView;

public class SettingActivity extends AppCompatActivity {

    //스피너
    ArrayAdapter<CharSequence> adspin1, adspin2;
    String instruments_menu, metronome_menu;

    //시크바
    SeekBar seekbar;
    WaveLoadingView waveLoadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        final Spinner spin1 = (Spinner)findViewById(R.id.instruments);
        final Spinner spin2 = (Spinner)findViewById(R.id.metronome);

        adspin1 = ArrayAdapter.createFromResource(this, R.array.instruments_menu, android.R.layout.simple_spinner_dropdown_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin1.setAdapter(adspin1);


        adspin2 = ArrayAdapter.createFromResource(SettingActivity.this, R.array.metronome_menu, android.R.layout.simple_spinner_dropdown_item);
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adspin2);



        seekbar = (SeekBar)findViewById(R.id.sound);
        waveLoadingView = (WaveLoadingView)findViewById(R.id.waveLoadingView);
        waveLoadingView.setProgressValue(0);
//        outcome = (TextView)findViewById(R.id.soundPresentage);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            public void onStartTrackingTouch(SeekBar seekBar){
                //시크바 움직임이 시작될때
//                number = seekbar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //시크바 움직임이 멈춘다면
//                number = seekBar.getProgress();
            }

            public  void onProgressChanged(SeekBar seekBar, int Progress,
                                           boolean fromUser){
                //시크바 상태가 변경되었을때  progress = seekbar상태값
                waveLoadingView.setProgressValue(Progress);
                if(Progress < 50){
                    waveLoadingView.setBottomTitle(String.format("%d%%",Progress));
                    waveLoadingView.setCenterTitle("");
                    waveLoadingView.setTopTitle("");

                }else if(Progress < 80){
                    waveLoadingView.setBottomTitle("");
                    waveLoadingView.setCenterTitle(String.format("%d%%",Progress));
                    waveLoadingView.setTopTitle("");

                }else{
                    waveLoadingView.setBottomTitle("");
                    waveLoadingView.setCenterTitle("");
                    waveLoadingView.setTopTitle(String.format("%d%%",Progress));
                }

            }
        });

    }
}





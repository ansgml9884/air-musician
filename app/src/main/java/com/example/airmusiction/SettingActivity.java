package com.example.airmusiction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    private Spinner mSpinner;
    private Spinner mSpinner2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mSpinner = (Spinner)findViewById(R.id.instruments);
        mSpinner2 = (Spinner)findViewById(R.id.metronome);
//        mSpinner.setOnItemClickListener(this);

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,R.array.instruments_menu,
                                                             android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        
    }
//    //만약 스피너에서 악기를 골랐다면 실행
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
//        String message = adapterView.getItemAtPosition(i).toString();
//
//    }
//    //빈곳을 골랐다면 실행
//    public void onNothingSelected(AdapterView<?> adapterView){
//
//    }
}

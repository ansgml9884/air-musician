package com.example.mediapipemultihandstrackingapp.help;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediapipemultihandstrackingapp.MainMenuActivity;
import com.example.mediapipemultihandstrackingapp.R;

public class HelpActivity6  extends AppCompatActivity {
    private View decorView;
    private ImageView chordView;
    private ImageView chordRealView;
    private int	uiOption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_help6);
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

        ImageButton lbtn = (ImageButton)findViewById(R.id.leftBtn);
        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        HelpActivity2.class);
                startActivity(intent);
            }
        });

        ImageButton rbtn = (ImageButton)findViewById(R.id.rightBtn);
        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        HelpActivity3.class);
                startActivity(intent);
            }
        });

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


        chordView = findViewById(R.id.chord_image);
        chordRealView = findViewById(R.id.chord_real_image);
        Spinner listSpinner  = findViewById(R.id.chord_list_spinner);

        ArrayAdapter chordAdapter = ArrayAdapter.createFromResource(this, R.array.chord_array, android.R.layout.simple_spinner_dropdown_item);
        chordAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(chordAdapter);
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    chordView.setImageResource(R.drawable.chord_a);
                    chordRealView.setImageResource(R.drawable.real_a);
                }else if (position == 1){
                    chordView.setImageResource(R.drawable.chord_en);
                    chordRealView.setImageResource(R.drawable.real_em);
                }else if (position == 2){
                    chordView.setImageResource(R.drawable.chord_d);
                    chordRealView.setImageResource(R.drawable.real_d);
                }else if (position == 3){
                    chordView.setImageResource(R.drawable.chord_c);
                    chordRealView.setImageResource(R.drawable.real_d);
                }else if (position == 4){
                    chordView.setImageResource(R.drawable.chord_f);
                    chordRealView.setImageResource(R.drawable.real_f);
                }else if (position == 5){
                }else if (position == 6){
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


}

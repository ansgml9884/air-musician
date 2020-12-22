package com.example.mediapipemultihandstrackingapp.model;

import android.view.animation.Interpolator;

//자연스러운 바운스 효과관련 class
public class MyBounceInterpolator implements Interpolator {

    private double myAmplitude = 1;
    private double myFrequency = 10;

    MyBounceInterpolator(double amplitude, double frequency){
        myAmplitude = amplitude;
        myFrequency = frequency;
    }

    public float getInterpolation(float time){
        return (float)(-1* Math.pow(Math.E,-time/myAmplitude)* Math.cos(myFrequency*time)+1);
    }
}
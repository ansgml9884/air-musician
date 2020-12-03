package com.example.mediapipemultihandstrackingapp.model;


import android.graphics.Bitmap;
import android.net.Uri;

import java.text.SimpleDateFormat;

// My Video Model
public class RecordVideoModel {
    private final Uri uri;
    private final String name;
    private final String duration;
    private final int size;
    private final int date;
    private  Bitmap thumbnail = null;

    public RecordVideoModel(Uri uri, String name, String duration, int size, int date, Bitmap thumbnail) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.date = date;
        this.thumbnail = thumbnail;
    }

    public Uri getUri() {
        return uri;
    }
    public String getName() {
        return name;
    }
    public String getDuration() {
        return duration;
    }
    public int getSize() {
        return size;
    }
    public int getDate() {
        return date;
    }
    public Bitmap getThumbnail(){ return thumbnail;}
}

package com.example.mediapipemultihandstrackingapp.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.mediapipemultihandstrackingapp.R;
import com.example.mediapipemultihandstrackingapp.model.RecordMediaModel;

import java.util.ArrayList;

public class MediaStoreUtil {
    private static MediaStoreUtil mediaStoreInstance;
    private Context appContext;

    private MediaStoreUtil(Context appContext){
        this.appContext = appContext;
    }

    // singleton 패턴 적용
    public static MediaStoreUtil getInstance(Context appContext){
        if(mediaStoreInstance == null){
            mediaStoreInstance = new MediaStoreUtil(appContext);
        }
        return mediaStoreInstance;
    }

    //미디어 전체 리스트 가져오기
    public ArrayList<RecordMediaModel> getAll(){
        ArrayList<RecordMediaModel> videoList = new ArrayList<>();
        ArrayList<RecordMediaModel> audeoList = new ArrayList<>();
        ArrayList<RecordMediaModel> mediaList = new ArrayList<>();

        videoList = getVideos();
        audeoList = getAudios();

        mediaList.addAll(videoList);
        mediaList.addAll(audeoList);

        return mediaList;
    }

    //비디오 전체 리스트 가져오기
    public ArrayList<RecordMediaModel> getVideos(){
        ArrayList<RecordMediaModel> mediaList = new ArrayList<>();

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
        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " ASC";

        try (Cursor cursor = appContext.getContentResolver().query(
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

                try {
                    // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 560, 480);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaList.add(new RecordMediaModel(contentUri, name, videoDuration, size, date, thumbnail));
            }
        }


        return mediaList;
    }

    // 오디오 전체 리스트 가져오기
    public ArrayList<RecordMediaModel> getAudios(){
        ArrayList<RecordMediaModel> mediaList = new ArrayList<>();

        //미디어 정보를 받아올 배열
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID, // primary Key
                MediaStore.Audio.Media.DISPLAY_NAME, // 파일읾
                MediaStore.Audio.Media.DURATION, // 영상길이
                MediaStore.Audio.Media.SIZE, // 영상크기
                MediaStore.Audio.Media.DATE_ADDED // 영상 추가 날짜 !!!추가 가공의 문제가 있음!!!
        };

        String selection=MediaStore.Audio.Media.DATA +" like?"; //전체 가져오기
        String[] selectionArgs=new String[]{"%AirMusician%"}; //AirMusician 폴더내에
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " ASC";

        try (Cursor cursor = appContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        )) {

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int createDate = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED);

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

                try {
                    // 썸네일 추출후 리사이즈해서 다시 비트맵 생성
                    Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.soundmediaico);
                    thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 560, 480);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaList.add(new RecordMediaModel(contentUri, name, videoDuration, size, date, thumbnail));
            }
        }


        return mediaList;

    }

    private void delete(){

    }
}

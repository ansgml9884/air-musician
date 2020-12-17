package com.example.mediapipemultihandstrackingapp;

import android.util.Log;

import com.google.mediapipe.formats.proto.LandmarkProto;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class HttpConnectionManager {

    private static final String TAG = "aaaaaaaaaaaaaaaaaaaaa";

    public String postRequest(List<LandmarkProto.NormalizedLandmark> LandmarkList) {

        String myResult = ""; //좌표값 csv로 저장할 변수
        for(int i=0;i<LandmarkList.size();i++){//좌표값 뽑아내기
            if (LandmarkList.size() == (i+1)){
                myResult += LandmarkList.get(i).getX() +","+ LandmarkList.get(i).getY() +","+ LandmarkList.get(i).getZ();
            }else {
                myResult += LandmarkList.get(i).getX() + "," + LandmarkList.get(i).getY() + "," + LandmarkList.get(i).getZ() + ",";
            }
//            Log.d(TAG,myResult);
        }
        String pURL = "https://xq2ihzoz2h.execute-api.us-east-2.amazonaws.com/c/";       //URL


        try {

            URL url = new URL(pURL); // URL 설정

            HttpURLConnection http = (HttpURLConnection)url.openConnection(); // 접속
            http.setDefaultUseCaches(false);
            http.setDoInput(true); // 서버에서 읽기 모드 지정
            http.setDoOutput(true); // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST"); // 전송 방식은 POST

            http.setRequestProperty("content-type", "application/json");
            http.setRequestProperty("Accept-Charset", "UTF-8");

            //StringBuffer buffer = new StringBuffer();
            JSONObject obj = new JSONObject();
            String jsonsrt = "";
            obj.put("data",myResult);
            jsonsrt = obj.toString();
//            String data = "{\"data\": {\"a\":\"MyName\"}}";//처음 테스트 데이터
            Log.d(TAG,jsonsrt); //실제 보내는 json데이터
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(jsonsrt);
            writer.flush();


            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
            return myResult;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myResult;
    }
}

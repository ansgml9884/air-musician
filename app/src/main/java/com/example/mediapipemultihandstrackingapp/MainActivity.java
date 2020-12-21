package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediapipemultihandstrackingapp.util.HttpConnectionManager;
import com.example.mediapipemultihandstrackingapp.util.SoundManager;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/** Main activity of MediaPipe example apps. */
public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool;
    private int[] chordSound = new int[6];
    boolean chk = false;
    private int	uiOption;
    private View decorView;
    private ImageButton btn_record;
    private boolean recording = false;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder mMediaRecorder;
    private TextView chord;
    HttpConnectionManager h;
    private  int chordIndex = 0;
    private boolean datacol = false;

    private String fileFath = "/storage/emulated/0/AirMusician/";

    // Guiter Chords
    private static final String CHORD_C = "1";
    private static final String CHORD_Dm = "21";
    private static final String CHORD_E = "3";
    private static final String CHORD_F = "4";
    private static final String CHORD_G7 = "5";
    private static final String CHORD_A = "6";
    private static final String CHORD_B = "7";


    private static final String TAG = "MainActivity";
    private static final String BINARY_GRAPH_NAME = "multi_hand_tracking_mobile_gpu.binarypb";
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";
    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks";
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
    // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
    // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
    // corner, whereas MediaPipe in general assumes the image origin is at top-left.
    private static final boolean FLIP_FRAMES_VERTICALLY = true;


    static {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni");
        System.loadLibrary("opencv_java3");
    }

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private SurfaceTexture previewFrameTexture;
    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private SurfaceView previewDisplayView;
    // Creates and manages an {@link EGLContext}.
    private EglManager eglManager;
    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private FrameProcessor processor;
    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private ExternalTextureConverter converter;
    // Handles camera access via the {@link CameraX} Jetpack support library.
    private CameraXPreviewHelper cameraHelper;

    public MainActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        try {
            setUpMediaRecorder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        previewDisplayView = new SurfaceView(this);
       // mCamera = getCameraInstance();
        h = new HttpConnectionManager();
        setupPreviewDisplayView();
        SoundManager.initSounds(getApplicationContext());
        ImageButton backImageBtn = (ImageButton)findViewById(R.id.back_img_btn);
        chord = findViewById(R.id.chord_view);

        btn_record = findViewById(R.id.rec_img_btn);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
//                if(datacol){
//                    Toast.makeText(MainActivity.this, "데이터 수집 종료.", Toast.LENGTH_SHORT).show();
//                    datacol = false;
//                }else{
//                    Toast.makeText(MainActivity.this, "데이터 수집 시작.", Toast.LENGTH_SHORT).show();
//                    datacol = true;
//                }
                trigger();

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
        backImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        AndroidAssetUtil.initializeNativeAssetManager(this);
        eglManager = new EglManager(null);
        processor =
                new FrameProcessor(
                        this,
                        eglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        processor.getVideoSurfaceOutput().setFlipY(FLIP_FRAMES_VERTICALLY);
        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,
                (packet) -> {
                    Log.d(TAG, "Received multi-hand landmarks packet.");
                    List<NormalizedLandmarkList> multiHandLandmarks =
                            PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser());
                    if (multiHandLandmarks.size() == 2) {
                        if (multiHandLandmarks.get(0).getLandmarkList().get(8).getX() > 0.46){
                            chk=true;
                        }else if (multiHandLandmarks.get(0).getLandmarkList().get(8).getX() <= 0.4 && chk==true){
                            chk = false;;
                            soundPool.play(chordSound[chordIndex],1,1,1,0,1);
                        }
                    }
                    Log.d(
                            TAG,
                            "[TS:"
                                    + packet.getTimestamp()
                                    + "] "
                                    + getMultiHandLandmarksDebugString(multiHandLandmarks));
                });
        PermissionHelper.checkAndRequestCameraPermissions(this);
    }

    //SoundPool
    @Override
    protected void onStart() {
        super.onStart();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(6)
                .build();

        chordSound[0] = soundPool.load(getApplicationContext(), R.raw.chord_c, 1);
        chordSound[1] = soundPool.load(getApplicationContext(), R.raw.chord_f, 1);
        chordSound[2] = soundPool.load(getApplicationContext(), R.raw.chord_dm, 1);
        chordSound[3] = soundPool.load(getApplicationContext(), R.raw.chord_a, 1);
        chordSound[4] = soundPool.load(getApplicationContext(), R.raw.chord_d, 1);
        chordSound[5] = soundPool.load(getApplicationContext(), R.raw.chord_em, 1);

    }


    @Override
    protected void onResume() {
        super.onResume();
        converter = new ExternalTextureConverter(eglManager.getContext());
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        converter.close();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
        viewGroup.addView(previewDisplayView);
        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());

                            }

                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                // (Re-)Compute the ideal size of the camera-preview display (the area that the
                                // camera-preview frames get rendered onto, potentially with scaling and rotation)
                                // based on the size of the SurfaceView that contains the display.
                                Size viewSize = new Size(width, height);
                                Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
                                // Connect the converter to the camera-preview frames as its input (via
                                // previewFrameTexture), and configure the output width and height as the computed
                                // display size.
                                converter.setSurfaceTextureAndAttachToGLContext(
                                        previewFrameTexture, displaySize.getWidth(), displaySize.getHeight());
                            }

                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                            }
                        });
       // surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    previewFrameTexture = surfaceTexture;
                    // Make the display view visible to start showing the preview. This triggers the
                    // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
                    previewDisplayView.setVisibility(View.VISIBLE);
                });
        cameraHelper.startCamera(this, CAMERA_FACING, /*surfaceTexture=*/ null);
    }
    static String abc = "";

    private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size() + "\n";
        String outputDateStr = "";
        int handIndex = 0;
//        if(multiHandLandmarks.size() == 1) {
            for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
                multiHandLandmarksStr +=
                        "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.getLandmarkCount() + "\n";


                int landmarkIndex = 0;
                for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
                    if (landmarkIndex != 0) {
                        outputDateStr += ",";
                    }
                    String xyzStr = landmark.getX()
                            + ", "
                            + landmark.getY()
                            + ", "
                            + landmark.getZ();
                    outputDateStr += xyzStr;
                    multiHandLandmarksStr +=
                            "\t\tLandmark ["
                                    + landmarkIndex
                                    + "]: ("
                                    + xyzStr
                                    + ")\n";
                    ++landmarkIndex;
                }



            }


            String restr = abc.replaceAll("[^0-9]","");
            Log.d(TAG,"Chord11111 "+ restr);
//            if(datacol) {
////                WriteCsv(outputDateStr); //21개의 좌표 전달
////            }
//        }
//        soundPool.play(chordSound[1],1,1,1,0,1);

            switch(restr){

                case CHORD_C:
                    Log.d(TAG,"Detection Chord_________C "+ abc);
                    chordIndex = 0;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________C ");
                        }
                    });
                    break;
                case CHORD_Dm:
                    chordIndex = 2;
                    Log.d(TAG,"Detection Chord_________Dm "+ abc);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________Dm ");
                        }
                    });
                    break;
                case CHORD_E:
                    chordIndex = 5;
                    Log.d(TAG,"Detection Chord_________E "+ abc);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________E ");
                        }
                    });
                    break;
                case CHORD_F:
                    chordIndex = 1;
                    Log.d(TAG,"Detection Chord_________F "+ abc);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________F ");
                        }
                    });
                    break;
                case CHORD_G7:
                    chordIndex = 0;
                    Log.d(TAG,"Detection Chord_________G7 "+ abc);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________G7 ");
                        }
                    });
                    break;
                case CHORD_A:
                    chordIndex = 3;
                    Log.d(TAG,"Detection Chord_________A "+ abc);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________A ");
                        }
                    });
                    break;
                case CHORD_B:
                    chordIndex = 4;
                    Log.d(TAG,"Detection Chord_________B "+ abc);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            chord.setText("Detection Chord_________B ");
                        }
                    });

//                    //스트로크 재생
            }
            ++handIndex;

            return multiHandLandmarksStr;
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.release();
    }


    public void trigger() {

        if(recording){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            recording = false;
            Toast.makeText(MainActivity.this, "녹화가 종료되었습니다.", Toast.LENGTH_SHORT).show();
        }else{
            mMediaRecorder.start();
            recording = true;
            Toast.makeText(MainActivity.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpMediaRecorder() throws IOException {

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mMediaRecorder.setOutputFile(getVideoFile());
        try {
            mMediaRecorder.prepare();
            System.out.println("준비완료");
        } catch (IOException e) {
            Log.e("start", "prepare() failed");
        }
    }

    public static void WriteCsv(String str) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath(); //내장에 만든다
        String directoryName = "AirMusician"; // 저장 경로 폴더 생성 - 메인 저장소 최상위 디렉토리
        final File myDir = new File(root + "/" + directoryName + "/dataSet");
        if (!myDir.exists()) { // 폴더 없을 경우
            myDir.mkdir(); // 폴더 생성
        }
        try {
            BufferedWriter buf =
                    new BufferedWriter(new FileWriter(myDir + "/Em_chord.csv", true)); // 데이터 파일 이름 ex)A_chord.csv
            buf.append(str); // 파일 쓰기
            buf.write(", 3\n"); //코드 분류값
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getVideoFile() {
        // Create a video file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File videoFile;
        videoFile = new File(fileFath + "REC_" + timeStamp + ".mp3");
        return videoFile;
    }

}

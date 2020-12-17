package com.example.mediapipemultihandstrackingapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.mediapipemultihandstrackingapp.util.SoundManager;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Main activity of MediaPipe example apps. */
public class MainActivity extends AppCompatActivity {
    private SoundPool soundPool;
    private int[] chordSound = new int[6];
    boolean chk = false;

    private int	uiOption;
    private View decorView;

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
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();
        SoundManager.initSounds(getApplicationContext());
        ImageButton backImageBtn = (ImageButton) findViewById(R.id.back_img_btn);

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

    public static void WriteCsv(String str) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath(); //내장에 만든다
        Log.d(TAG, "11111111111111111111111" + root);
        String directoryName = "AirMusician"; // 저장 경로 폴더 생성 - 메인 저장소 최상위 디렉토리
        final File myDir = new File(root + "/" + directoryName + "/dataSet");
        if (!myDir.exists()) { // 폴더 없을 경우
            myDir.mkdir(); // 폴더 생성
        }
        try {
            BufferedWriter buf =
                    new BufferedWriter(new FileWriter(myDir + "/Mute.csv", true)); // 데이터 파일 이름 ex)A_chord.csv
            buf.append(str); // 파일 쓰기
            buf.write(", 0\n"); //코드 분류값
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    double[] denseArray = {0.4023429,0.6276824000000001,0.0,0.41993284,0.60309863,0.0,0.40992182,0.5734683,0.0,0.39859325,0.5291843,0.0,0.39081866,0.47737035,0.0,0.37766942,0.5610768,0.0,0.4408137,0.5635455,0.0,0.46573249,0.56591505,0.0,0.47462204,0.56582975,0.0,0.36409047,0.59305197,0.0,0.45248768,0.59354,0.0,0.47987312,0.59267265,0.0,0.49496764,0.5982906,0.0,0.36553264,0.6260260999999999,0.0,0.44301292,0.6228308000000001,0.0,0.47176942,0.61697865,0.0,0.4909321,0.6225253000000001,0.0,0.37400115,0.658155,0.0,0.43817282,0.6441031,0.0,0.46843532,0.6350836999999999,0.0,0.48044056,0.6410376,0.0};
//    FVec fVecDense = FVec.Transformer.fromArray(denseArray,false);
//
//    double[] prediction = predictor.predict(fVecDense);
//    int[] leafIndexes = predictor.predictLeaf(fVecDense);


    private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size() + "\n";
        String outputDateStr = "";
        int handIndex = 0;
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

                //스트로크 재생
                if (multiHandLandmarks.size() >= 1) {
                    if (multiHandLandmarks.get(0).getLandmarkList().get(8).getX() > 0.6){
                        chk=true;
                    }else if (multiHandLandmarks.get(0).getLandmarkList().get(8).getX() <= 0.4 && chk==true){
                        chk = false;;
                        soundPool.play(chordSound[1],1,1,1,0,1);
                    }
                }

            }
            String abc = "";
            HttpConnectionManager h = new HttpConnectionManager();
            abc = h.postRequest(landmarks.getLandmarkList());
            Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);

            //WriteCsv(outputDateStr); //21개의 좌표 전달

            int chord_number = 0;
            switch (abc) {
                case "0\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "1\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "21\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "3\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "4\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "57\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "6\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
                case "7\n":
                    chord_number = 0;
                    Log.d(TAG, "yyyyyyyyyyyyyyyy" + abc);
                    break;
            }

//            float test = landmarks.getLandmarkList().get(0).getX();
            Log.d(TAG, "11111111111111111111111" + outputDateStr); //데이터 확인
            ++handIndex;
        }
        return multiHandLandmarksStr;
    }
}
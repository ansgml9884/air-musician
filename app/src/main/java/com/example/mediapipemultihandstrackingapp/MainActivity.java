package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.List;
/** Main activity of MediaPipe example apps. */
public class MainActivity extends AppCompatActivity  {
    private SoundPool soundPool;
    private int[] chordSound = new int[3];
    private Button[] chordButton = new Button[3];

    private View decorView;
    private int	uiOption;

    public static final int REQUEST_CAMERA = 1;
    private SurfaceView surfaceView;
    private Camera camera;
    private MediaRecorder mMediaRecorder;
    private ImageButton btn_record;
    private boolean recording = false;
    private SurfaceHolder surfaceHolder;
    private String fileFath = "/storage/emulated/0/AirMusician/Test.mp4";

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
    private Size displaySize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();
        SoundManager.initSounds(getApplicationContext());
        ImageButton backImageBtn = (ImageButton)findViewById(R.id.back_img_btn);
        chordButton[0] = findViewById(R.id.chord_c);
        chordButton[1] = findViewById(R.id.chord_f);
        chordButton[2] = findViewById(R.id.chord_dm);
        btn_record = findViewById(R.id.rec_img_btn);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trigger(v);
            }
        });

        for (Button buttonId : chordButton) {
            buttonId.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    Button result = findViewById(view.getId());
                    Toast.makeText(MainActivity.this, "클릭 : " + result.getText().toString() , Toast.LENGTH_SHORT).show();
                    if(buttonId.getText().equals("F")){
                        soundPool.play(chordSound[1],1,1,1,0,1);
                    }else if(buttonId.getText().equals("C")){
                        soundPool.play(chordSound[0],1,1,1,0,1);
                    }else {
                        soundPool.play(chordSound[2], 1, 1, 1, 0, 1);
                    }
                }
            });
        }

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


        //뒤로가기 버튼
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
                        INPUT_VIDEO_STREAM_NAME);
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
        PermissionHelper.checkAndRequestAudioPermissions(this);

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


    }

    @Override
    protected void onResume() {
        super.onResume();
        converter = new ExternalTextureConverter(eglManager.getContext());
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
            camera=Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);


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
        surfaceHolder = previewDisplayView.getHolder();
        previewDisplayView.getHolder().addCallback(
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
                        displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
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

                    mMediaRecorder = new MediaRecorder();
                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    //DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
//                    CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
//                    profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
//                    profile.videoCodec = MediaRecorder.VideoEncoder.MPEG_4_SP;
//                    profile.videoFrameHeight = cameraHelper.getFrameSize().getHeight();
//                    profile.videoFrameWidth = cameraHelper.getFrameSize().getWidth();
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                    // profile.videoBitRate = 15;

                    // Apply to MediaRecorder
                    //mMediaRecorder.setProfile(profile);
                    mMediaRecorder.setOutputFile(fileFath);

                    //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    //DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
                    //mMediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
                    //mMediaRecorder.setVideoEncodingBitRate(1000000);
                    //mMediaRecorder.setVideoFrameRate(30);


//                            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//                            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                            mMediaRecorder.setOutputFile(fileFath);
//                            mMediaRecorder.setVideoEncodingBitRate(1000000);
//                            mMediaRecorder.setVideoFrameRate(30);
//
//                            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
//                            mMediaRecorder.setVideoSize(displayMetrics.widthPixels, displayMetrics.heightPixels);
//\]]
//                            //mMediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
//                            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//                            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//
//                            //mMediaRecorder.setVideoSize(displaySize.getWidth(), displaySize.getHeight());
//                            //mMediaRecorder.setVideoFrameRate(30);

                    try {
                        Toast.makeText(MainActivity.this, "준비중."+ recording, Toast.LENGTH_SHORT).show();
                        mMediaRecorder.prepare();

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    Toast.makeText(MainActivity.this, "완료."+ recording, Toast.LENGTH_SHORT).show();



                    previewDisplayView.setVisibility(View.VISIBLE);
                });
        cameraHelper.startCamera(this, CAMERA_FACING, /*surfaceTexture=*/ null);
        ;
    }
    private String getMultiHandLandmarksDebugString(List<NormalizedLandmarkList> multiHandLandmarks) {
        if (multiHandLandmarks.isEmpty()) {
            return "No hand landmarks";
        }
        String multiHandLandmarksStr = "Number of hands detected: " + multiHandLandmarks.size() + "\n";
        int handIndex = 0;
        for (NormalizedLandmarkList landmarks : multiHandLandmarks) {
            multiHandLandmarksStr +=
                    "\t#Hand landmarks for hand[" + handIndex + "]: " + landmarks.getLandmarkCount() + "\n";
            int landmarkIndex = 0;
            for (NormalizedLandmark landmark : landmarks.getLandmarkList()) {
                multiHandLandmarksStr +=
                        "\t\tLandmark ["
                                + landmarkIndex
                                + "]: ("
                                + landmark.getX()
                                + ", "
                                + landmark.getY()
                                + ", "
                                + landmark.getZ()
                                + ")\n";
                ++landmarkIndex;

                ///////////////////////////////////////
                //x 자표가 0.5 이상일때 소리나기

                //////////////////////////////////////
            }
            ++handIndex;
        }
        return multiHandLandmarksStr;
    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.release();
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) {}
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height- targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void trigger(View v) {

        if(recording){
            mMediaRecorder.stop();
            recording = false;
            camera.lock();
            Toast.makeText(MainActivity.this, "녹화가 종료되었습니다."+ recording, Toast.LENGTH_SHORT).show();
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMediaRecorder.start();
                    recording = true;
                    Toast.makeText(MainActivity.this, "녹화가 시작되었습니다."+ recording, Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

}
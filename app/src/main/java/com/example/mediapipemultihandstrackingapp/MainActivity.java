package com.example.mediapipemultihandstrackingapp;


import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediapipemultihandstrackingapp.help.PermissionSupport;
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


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/** Main activity of MediaPipe example apps. */
public class MainActivity extends AppCompatActivity  {
    private SoundPool soundPool;
    private int[] chordSound = new int[6];
    private Button[] chordButton = new Button[6];
    boolean chk = false;
    private int	uiOption;
    private View decorView;
    private Camera camera;
    private ImageButton btn_record;
    private SurfaceView surfaceView;
    private boolean recording = false;
    private SurfaceHolder surfaceHolder;
    private MediaRecorder mMediaRecorder;
    public static final int REQUEST_CAMERA = 1;
    private PermissionSupport permission;


    private String fileFath = "/storage/emulated/0/AirMusician/";

    private static Handler mCameraHandler;
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
//        permissionCheck();
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
    }


//    private void permissionCheck(){
//
//        // SDK 23버전 이하 버전에서는 Permission이 필요하지 않습니다.
//        if(Build.VERSION.SDK_INT >= 23){
//            // 방금 전 만들었던 클래스 객체 생성
//            permission = new PermissionSupport(this, this);
//            // 권한 체크한 후에 리턴이 false로 들어온다면
//            if (!permission.checkPermission()){
//                // 권한 요청을 해줍니다.
//                permission.requestPermission();
//            }
//            startCamera();
//            try {
//                setUpMediaRecorder();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
            try {
                setUpMediaRecorder();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                        converter.setSurfaceTextureAndAttachToGLContext(previewFrameTexture, displaySize.getWidth(), displaySize.getHeight());
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
                    previewDisplayView.setVisibility(View.VISIBLE);
                });
        cameraHelper.startCamera(this, CAMERA_FACING, /*surfaceTexture=*/ null);

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        //mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//        CamcorderProfile profile = CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT,CamcorderProfile.QUALITY_HIGH);
//        profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
//        profile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;
//        profile.audioCodec = MediaRecorder.AudioEncoder.DEFAULT;

        // Apply to MediaRecorder
//        mMediaRecorder.setOrientationHint(90);
//        mMediaRecorder.setProfile(profile);
        mMediaRecorder.setOutputFile(getVideoFile());
//        mMediaRecorder.setVideoSize(mVideoSize.getWidth(), mVideoSize.getHeight());
//        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("start", "prepare() failed");
        }


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

                int hand_count = multiHandLandmarks.size();
                int chord_number = 0;

                //코드 판별
                //soundPool.play(chordSound[1],1,1,1,0,1);
                //스트로크 재생

                if (multiHandLandmarks.get(hand_count-1).getLandmarkList().get(8).getX() < multiHandLandmarks.get(hand_count-1).getLandmarkList().get(12).getX()){
                    Log.d(TAG,"EmEmEmEmEmEmEmEmEmEmEmEmEm");
                    chord_number = 5;
                    //soundPool.play(chordSound[5],1,1,1,0,1);
                }else if (multiHandLandmarks.get(hand_count-1).getLandmarkList().get(12).getX() < multiHandLandmarks.get(hand_count-1).getLandmarkList().get(16).getX()){
                    Log.d(TAG,"DDDDDDDDDDDDDDDDDDDDDDDDDD");
                    chord_number = 4;
                    //soundPool.play(chordSound[4],1,1,1,0,1);
                }else{
                    Log.d(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAA");
                    chord_number = 3;
                    //soundPool.play(chordSound[3],1,1,1,0,1);
                }


                if (multiHandLandmarks.size()==2) {
                    Log.d(TAG, "AAAAAAAAA" + multiHandLandmarks.get(0).getLandmarkList().get(0).getY() + "AAAAAAAAAAAAAAAAA" + multiHandLandmarks.get(1).getLandmarkList().get(0).getY());
                    if (multiHandLandmarks.get(0).getLandmarkList().get(8).getX() > 0.6){
                        chk=true;
                    }else if (multiHandLandmarks.get(0).getLandmarkList().get(8).getX() < 0.3 && chk==true){
                        chk = false;
                        Toast.makeText(MainActivity.this, ""+chk, Toast.LENGTH_SHORT).show();
                    }
                }
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


    public void trigger() {

        if(recording){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            recording = false;
            Toast.makeText(MainActivity.this, "녹화가 종료되었습니다."+ recording, Toast.LENGTH_SHORT).show();
        }else{
            mMediaRecorder.start();
            recording = true;
            Toast.makeText(MainActivity.this, "녹화가 시작되었습니다."+ recording, Toast.LENGTH_SHORT).show();
        }
    }

    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();
    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }


    private void setUpMediaRecorder() throws IOException {

    }

//    private void startRecordingVideo() {
//
//        try {
//            closePreviewSession();
//            setUpMediaRecorder();
//            SurfaceTexture texture = binding.preview.getSurfaceTexture();
//            assert texture != null;
//            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
//
//            List<Surface> surfaces = new ArrayList<>();
//
//            Surface previewSurface = new Surface(texture);
//            surfaces.add(previewSurface);
//            mCaptureRequestBuilder.addTarget(previewSurface);
//
//            Surface recordSurface = mMediaRecorder.getSurface();
//            surfaces.add(recordSurface);
//            mCaptureRequestBuilder.addTarget(recordSurface);
//
//            mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
//                @Override
//                public void onConfigured(@NonNull CameraCaptureSession session) {
//                    mCameraCaptureSession = session;
//                    updatePreview();
//                    getActivity().runOnUiThread(() -> {
//                        binding.pictureBtn.setText(R.string.stop);
//                        mIsRecordingVideo = true;
//
//                        mMediaRecorder.start();
//                    });
//                }
//
//                @Override
//                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                    Activity activity = getActivity();
//                    if (null != activity) {
//                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }, mBackgroundHandler);
//            timer();
//        } catch (CameraAccessException | IOException e) {
//            e.printStackTrace();
//        }
//    }

    private File getVideoFile() {
        // Create a video file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File videoFile;
        videoFile = new File(fileFath + "REC_" + timeStamp + ".mp3");
        return videoFile;
    }



}

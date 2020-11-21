package com.example.mediapipemultihandstrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;




public class RecordAtivity extends AppCompatActivity {
//    private static final int MEDIA_RECORDER_REQUEST = 0;
//
//    private Camera mCamera;
//    private TextureView mPreview;
//    private MediaRecorder mMediaRecorder;
//    private File mOutputFile;
//
//    private boolean isRecording = false;
//    private static final String TAG = "Recorder";
//    private ImageButton captureButton;
//
//    private final String[] requiredPermissions = {
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.CAMERA,
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_record);

        ImageButton backBtn = (ImageButton) findViewById(R.id.back);
        //뒤로가기 버튼
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });
//        mPreview = (TextureView)findViewById(R.id.surfaceView);
//        captureButton = (ImageButton) findViewById(R.id.recordStart);

    }
//    public void onCaptureClick(View view) {
//
//        if (areCameraPermissionGranted()){
//            startCapture();
//        } else {
//            requestCameraPermissions();
//        }
//    }
//
//    private void startCapture(){
//
//        if (isRecording) {
//            // BEGIN_INCLUDE(stop_release_media_recorder)
//
//            // stop recording and release camera
//            try {
//                mMediaRecorder.stop();  // stop the recording
//            } catch (RuntimeException e) {
//                // RuntimeException is thrown when stop() is called immediately after start().
//                // In this case the output file is not properly constructed ans should be deleted.
//                Log.d(TAG, "RuntimeException: stop() is called immediately after start()");
//                //noinspection ResultOfMethodCallIgnored
//                mOutputFile.delete();
//            }
//            releaseMediaRecorder(); // release the MediaRecorder object
//            mCamera.lock();         // take camera access back from MediaRecorder
//
//            // inform the user that recording has stopped
//            isRecording = false;
//            releaseCamera();
//            // END_INCLUDE(stop_release_media_recorder)
//
//        } else {
//
//            // BEGIN_INCLUDE(prepare_start_media_recorder)
//
//            new MediaPrepareTask().execute(null, null, null);
//
//            // END_INCLUDE(prepare_start_media_recorder)
//
//        }
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // if we are using MediaRecorder, release it first
//        releaseMediaRecorder();
//        // release the camera immediately on pause event
//        releaseCamera();
//    }
//
//    private void releaseMediaRecorder(){
//        if (mMediaRecorder != null) {
//            // clear recorder configuration
//            mMediaRecorder.reset();
//            // release the recorder object
//            mMediaRecorder.release();
//            mMediaRecorder = null;
//            // Lock camera for later use i.e taking it back from MediaRecorder.
//            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
//            mCamera.lock();
//        }
//    }
//
//    private void releaseCamera(){
//        if (mCamera != null){
//            // release the camera for other applications
//            mCamera.release();
//            mCamera = null;
//        }
//    }
//
//    private boolean prepareVideoRecorder(){
//
//        // BEGIN_INCLUDE (configure_preview)
//        mCamera = CameraHelper.getDefaultCameraInstance();
//
//        // We need to make sure that our preview and recording video size are supported by the
//        // camera. Query camera to find all the sizes and choose the optimal size given the
//        // dimensions of our preview surface.
//        Camera.Parameters parameters = mCamera.getParameters();
//        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
//        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
//        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
//                mSupportedPreviewSizes, mPreview.getWidth(), mPreview.getHeight());
//
//        // Use the same size for recording profile.
//        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        profile.videoFrameWidth = optimalSize.width;
//        profile.videoFrameHeight = optimalSize.height;
//
//        // likewise for the camera object itself.
//        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
//        mCamera.setParameters(parameters);
//        try {
//            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
//            // with {@link SurfaceView}
//            mCamera.setPreviewTexture(mPreview.getSurfaceTexture());
//        } catch (IOException e) {
//            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
//            return false;
//        }
//        // END_INCLUDE (configure_preview)
//
//
//        // BEGIN_INCLUDE (configure_media_recorder)
//        mMediaRecorder = new MediaRecorder();
//
//        // Step 1: Unlock and set camera to MediaRecorder
//        mCamera.unlock();
//        mMediaRecorder.setCamera(mCamera);
//
//        // Step 2: Set sources
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT );
//        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//
//        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
//        mMediaRecorder.setProfile(profile);
//
//        // Step 4: Set output file
//        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_VIDEO);
//        if (mOutputFile == null) {
//            return false;
//        }
//        mMediaRecorder.setOutputFile(mOutputFile.getPath());
//        // END_INCLUDE (configure_media_recorder)
//
//        // Step 5: Prepare configured MediaRecorder
//        try {
//            mMediaRecorder.prepare();
//        } catch (IllegalStateException e) {
////            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
//            releaseMediaRecorder();
//            return false;
//        } catch (IOException e) {
////            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
//            releaseMediaRecorder();
//            return false;
//        }
//        return true;
//    }
//
//    private boolean areCameraPermissionGranted() {
//
//        for (String permission : requiredPermissions){
//            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
//                    PackageManager.PERMISSION_GRANTED)){
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private void requestCameraPermissions(){
//        ActivityCompat.requestPermissions(
//                this,
//                requiredPermissions,
//                MEDIA_RECORDER_REQUEST);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//
//        if (MEDIA_RECORDER_REQUEST != requestCode) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            return;
//        }
//
//        boolean areAllPermissionsGranted = true;
//        for (int result : grantResults){
//            if (result != PackageManager.PERMISSION_GRANTED){
//                areAllPermissionsGranted = false;
//                break;
//            }
//        }
//
//        if (areAllPermissionsGranted){
//            startCapture();
//        } else {
//            // User denied one or more of the permissions, without these we cannot record
//            // Show a toast to inform the user.
//            Toast.makeText(getApplicationContext(),
//                    getString(R.string.no_camera_access),
//                    Toast.LENGTH_SHORT)
//                    .show();
//        }
//    }
//
//    /**
//     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
//     * operation.
//     */
//    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {
//
//        protected Boolean doInBackground(Void... voids) {
//            // initialize video camera
//            if (prepareVideoRecorder()) {
//                // Camera is available and unlocked, MediaRecorder is prepared,
//                // now you can start recording
//                mMediaRecorder.start();
//
//                isRecording = true;
//            } else {
//                // prepare didn't work, release the camera
//                releaseMediaRecorder();
//                return false;
//            }
//            return true;
//        }
//
//        protected void onPostExecute(Boolean result) {
//            if (!result) {
//                RecordAtivity.this.finish();
//            }
//
//        }
//    }
//
//





}
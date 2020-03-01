package com.example.gaz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gaz.camera.CameraService;
import com.example.gaz.util.async.DownloadImageTask;
import com.example.gaz.util.HttpResult;
import com.example.gaz.util.ILogCallback;


public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, ILogCallback {
    private Toast mToast;
    private CameraService myCamera;
    private TextView mPreview;
    private final int REQUEST_CODE_PERMISSION = 1;
    private int count = 0;

    private final int CAMERA = 0;
    private TextureView mImageView = null;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler = null;
    private ImageView mResult;
    private View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.textureView);
        if(isGranted()) {
            mImageView.setSurfaceTextureListener(this);
        }
        mPreview = findViewById(R.id.preview);
        mResult = findViewById(R.id.result);
        mView = findViewById(R.id.border);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(Constants.LOG_TAG, "Запрашиваем разрешение");

        if (!isGranted()) {
            requestPermissions(new String[] { Manifest.permission.CAMERA }, REQUEST_CODE_PERMISSION);
        } else {
            startBackgroundThread();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length == 1) {
                boolean allGrant = true;
                for(int grant : grantResults) {
                    if(grant != PackageManager.PERMISSION_GRANTED) {
                        allGrant = false;
                        break;
                    }
                }

                if(mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }

                if(!allGrant) {
                    mToast = Toast.makeText(this, getText(R.string.not_permissions), Toast.LENGTH_LONG);
                } else {
                    mToast = Toast.makeText(this, getText(R.string.permissions_grant), Toast.LENGTH_LONG);

                    startBackgroundThread();
                    onSurfaceTextureAvailable(null, 0 ,0);
                }
            } else {
                mToast = Toast.makeText(this, getText(R.string.not_permissions), Toast.LENGTH_LONG);
            }

            if(mToast != null) {
                mToast.show();
            }
        }
    }

    @Override
    public void onPause() {
        if(myCamera != null && myCamera.isOpen()) {
            myCamera.closeCamera();
        }
        if(isGranted()) {
            stopBackgroundThread();
        }
        super.onPause();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if(isGranted()) {
            myCamera = new CameraService(this, mImageView, mBackgroundHandler, String.valueOf(CAMERA));
            myCamera.openCamera();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private boolean isGranted() {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onLogPreUpload(long frameNumber) {
        mView.setBackground(getResources().getDrawable(R.drawable.border_red, null));
    }

    @Override
    public void onLogUploaded(HttpResult httpResult, long frameNumber, byte[] bytes) {
        mView.setBackground(getResources().getDrawable(R.drawable.border_green, null));

        if(bytes != null) {
            count++;
            Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            mPreview.setText(frameNumber + " (" + count + ") " + httpResult.data);
            mPreview.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            new DownloadImageTask(mResult).execute(Constants.IMAGE_URL + httpResult.data);
        }
    }
}
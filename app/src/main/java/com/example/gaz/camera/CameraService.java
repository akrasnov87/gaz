package com.example.gaz.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;

import com.example.gaz.Constants;

public class CameraService {

    private String mCameraID;
    private CameraDevice mCameraDevice = null;
    private Handler mBackgroundHandler;
    private Context mContext;
    private CameraManager mCameraManager;
    private CameraDevice.StateCallback mCameraCallback;

    public CameraService(Context context, TextureView imageView, Handler backgroundHandler, String cameraID) {
        mBackgroundHandler = backgroundHandler;
        mCameraID = cameraID;
        mContext = context;
        mCameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        mCameraCallback = new CameraDeviceState(context, imageView, backgroundHandler);
    }

    public boolean isOpen() {
        if (mCameraDevice == null) {
            return false;
        } else {
            return true;
        }
    }

    public void openCamera() {
        try {
            if (mContext.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                mCameraManager.openCamera(mCameraID, mCameraCallback, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            Log.i(Constants.LOG_TAG, e.getMessage());
        }
    }

    public void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }
}

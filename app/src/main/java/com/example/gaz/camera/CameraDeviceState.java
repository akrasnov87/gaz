package com.example.gaz.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;

import com.example.gaz.Constants;
import com.example.gaz.util.HttpResult;
import com.example.gaz.util.ILogCallback;
import com.example.gaz.util.IUploadCallback;
import com.example.gaz.util.async.UploadImageTask;

import java.util.Arrays;

public class CameraDeviceState extends CameraDevice.StateCallback {
    private CameraDevice mCameraDevice;
    private TextureView mImageView;
    private CameraCaptureSession mCaptureSession;
    private UploadImageTask mTask;
    private boolean mIsUploaded = true;
    private Handler mBackgroundHandler;
    private Context mContext;
    private ILogCallback mCallback;

    public CameraDeviceState(Context context, TextureView imageView, Handler backgroundHandler) {
        mImageView = imageView;
        mBackgroundHandler = backgroundHandler;
        mContext = context;
        if(context instanceof  ILogCallback) {
            mCallback = (ILogCallback) context;
        }
    }

    @Override
    public void onOpened(CameraDevice camera) {
        mCameraDevice = camera;
        Log.i(Constants.LOG_TAG, "Open camera  with id:" + mCameraDevice.getId());
        createCameraPreviewSession(mImageView);
    }

    @Override
    public void onDisconnected(CameraDevice camera) {
        mCameraDevice.close();

        Log.i(Constants.LOG_TAG, "disconnect camera  with id:"+mCameraDevice.getId());
        mCameraDevice = null;
    }

    @Override
    public void onError(CameraDevice camera, int error) {
        Log.i(Constants.LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
    }

    private void createCameraPreviewSession(final TextureView mImageView) {
        SurfaceTexture texture = mImageView.getSurfaceTexture();

        //texture.setDefaultBufferSize(1920,1080);
        Surface surface = new Surface(texture);

        try {
            final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);

            builder.addTarget(surface);
            mCameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mCaptureSession = session;
                    try {
                        mCaptureSession.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                                super.onCaptureStarted(session, request, timestamp, frameNumber);
                                if(frameNumber != 0 && frameNumber % Constants.FRAME_NUMBER == 0 && isUploaded()) {
                                    Log.d(Constants.LOG_TAG, "processing " + frameNumber + " frame");
                                    uploadToServer(mImageView, frameNumber);
                                }
                            }
                        }, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }}, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * Загрузить информацию на сервер
     */
    private void uploadToServer(TextureView imageView, final long frameNumber) {
        final Bitmap bitmap = imageView.getBitmap();

        if(mTask != null) {
            mTask = null;
            mIsUploaded = false;
        }

        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                mTask = new UploadImageTask(bitmap, frameNumber);
                mTask.execute(new IUploadCallback() {
                    @Override
                    public void onUploadCallback(byte[] bytes, HttpResult httpResult) {
                        Log.d(Constants.LOG_TAG, "Frame " + frameNumber + " is " + httpResult.meta.success);
                        mIsUploaded = true;
                        if(mCallback != null) {
                            mCallback.onLogUpload(httpResult, frameNumber, bytes);
                        }
                    }
                });
            }
        });
    }

    private boolean isUploaded() {
        return mIsUploaded;
    }
}

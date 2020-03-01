package com.example.gaz.util.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.gaz.Constants;
import com.example.gaz.MultipartUtility;
import com.example.gaz.util.BitmapUtil;
import com.example.gaz.util.HttpResult;
import com.example.gaz.util.IUploadCallback;

public class UploadImageTask extends AsyncTask<IUploadCallback, Void, HttpResult> {
    private byte[] mBytes;
    private MultipartUtility multipartUtility;
    private IUploadCallback mCallback;
    private long mFrameNumber;
    private byte[] small;

    public UploadImageTask(Bitmap bitmap, long frameNumber) {
        mBytes = BitmapUtil.convertToBytes(bitmap);
        mFrameNumber = frameNumber;
    }

    public UploadImageTask(byte[] bytes) {
        mBytes = bytes;
    }

    protected HttpResult doInBackground(IUploadCallback... callbacks) {
        if(callbacks != null && callbacks.length > 0) {
            mCallback = callbacks[0];
        }

        try {
            small = BitmapUtil.cacheBitmap(mBytes, BitmapUtil.IMAGE_QUALITY, BitmapUtil.QUALITY_120p);

            multipartUtility = new MultipartUtility(Constants.UPLOAD_URL);
            multipartUtility.addFilePart(Constants.INPUT_PARAMS, BitmapUtil.cacheBitmap(mBytes, BitmapUtil.IMAGE_QUALITY, BitmapUtil.QUALITY_720p));
            multipartUtility.addFormField("frameNumber", String.valueOf(mFrameNumber));
            return new HttpResult(multipartUtility.finish());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new HttpResult((String) null);
    }

    protected void onPostExecute(HttpResult httpResult) {
        if(mCallback != null) {
            mCallback.onUploadCallback(small, httpResult);
        }
    }
}

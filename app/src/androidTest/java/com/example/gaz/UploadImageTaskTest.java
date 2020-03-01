package com.example.gaz;

import com.example.gaz.util.HttpResult;
import com.example.gaz.util.IUploadCallback;
import com.example.gaz.util.async.UploadImageTask;

import org.junit.Test;

import static org.junit.Assert.*;

public class UploadImageTaskTest {
    private UploadImageTask mTask;

    @Test
    public void uploadTest() {
        mTask = new UploadImageTask("Hello".getBytes());
        mTask.execute(new IUploadCallback() {
            @Override
            public void onUploadCallback(byte[] bytes, HttpResult httpResult) {
                assertFalse(httpResult.meta.success);
            }
        });
    }
}
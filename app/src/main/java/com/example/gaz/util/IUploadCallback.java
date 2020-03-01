package com.example.gaz.util;

import com.example.gaz.util.HttpResult;

public interface IUploadCallback {
    void onUploadCallback(byte[] bytes, HttpResult httpResult);
}

package com.example.gaz.util;

import com.example.gaz.util.HttpResult;

public interface ILogCallback {
    void onLogUpload(HttpResult httpResult, long frameNumber, byte[] bytes);
}

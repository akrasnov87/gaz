package com.example.gaz.util;

import com.example.gaz.util.HttpResult;

public interface ILogCallback {
    void onLogPreUpload(long frameNumber);
    void onLogStatus(String text);
    void onLogUploaded(HttpResult httpResult, long frameNumber, byte[] bytes);
}

package com.example.gaz;

public interface Constants {
    String ROOT_URL = "http://192.168.150.79:8000";
    String UPLOAD_URL = ROOT_URL + "/upload";
    String TEST_URL = ROOT_URL + "/test";
    String IMAGE_URL = ROOT_URL + "/file?id=";
    String INPUT_PARAMS = "input";
    /**
     * обработка каждего n кдра
     */
    int FRAME_NUMBER = 5;
    String LOG_TAG = "myLogs";
}

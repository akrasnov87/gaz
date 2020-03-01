package com.example.gaz.util;

import android.util.Log;

import com.example.gaz.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpResult {

    public HttpResult(byte[] bytes) {
        this(new String(bytes));
    }

    public HttpResult(String data) {
        meta = new Meta();
        meta.success = data != null;
        if(data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                this.data = jsonObject.getString("data");
            } catch (JSONException e) {
                meta.success = false;
                Log.d(Constants.LOG_TAG, "Ошибка преобразования");
            }
        }
    }

    public String data;
    public Meta meta;

    public class Meta {
        public boolean success;
    }
}

package com.example.gaz;

import com.example.gaz.util.HttpResult;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResultUtilTest {

    private String mBody;

    @Before
    public void setUp() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", "1");
        JSONObject jsonMeta = new JSONObject();
        jsonMeta.put("success", true);
        jsonObject.put("meta", jsonMeta);
        mBody = jsonObject.toString();
    }

    @Test
    public void getResult() {
        HttpResult httpResult = new HttpResult(mBody);
        assertEquals(httpResult.data, "1");
        assertTrue(httpResult.meta.success);
    }
}
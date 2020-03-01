package com.example.gaz;

import com.example.gaz.util.HttpResult;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MultipartUtilityTest {
    private MultipartUtility multipartUtility;

    @Before
    public void setUp() throws Exception {
        multipartUtility = new MultipartUtility(Constants.UPLOAD_URL);
    }

    @Test
    public void addFilePart() throws Exception {
        multipartUtility.addFilePart(Constants.INPUT_PARAMS, "Hello".getBytes());
        byte[] output = multipartUtility.finish();
        assertTrue(new HttpResult(output).meta.success);
    }
}
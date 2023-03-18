package com.supermap.imobile.streamingservice;

import android.util.Log;

class HttpLogger implements HttpLoggingInterceptor.Logger {
    private static final String TAG = "HttpLogger";

    @Override
    public void log(String message) {
        Log.d(TAG, message);
    }

}
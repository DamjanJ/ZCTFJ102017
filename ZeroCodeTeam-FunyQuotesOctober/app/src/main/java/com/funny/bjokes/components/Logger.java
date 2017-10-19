package com.funny.bjokes.components;

import android.util.Log;

/**
 * Created by Rade on 5/5/2015.
 */
public class Logger {

    private String mTag;
    private boolean mDebug;

    public Logger(String tag, boolean debug) {
        this.mTag = tag;
        this.mDebug = debug;
    }

    public void d(String message) {
        if (mDebug) {
            Log.d(mTag, message);
        }
    }

    public void e(String message) {
        if (mDebug) {
            Log.e(mTag, message);
        }
    }

    public void i(String message) {
        if (mDebug) {
            Log.i(mTag, message);
        }
    }
}

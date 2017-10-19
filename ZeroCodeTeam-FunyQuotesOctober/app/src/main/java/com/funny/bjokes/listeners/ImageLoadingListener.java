package com.funny.bjokes.listeners;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by CJH on 2016.01.14.
 */
public class ImageLoadingListener extends SimpleImageLoadingListener {

    Context mContext;
    ProgressBar mLoading;

    public ImageLoadingListener(Context context, ProgressBar progressBar) {
        mContext = context;
        mLoading = progressBar;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        String error = "";
        switch (failReason.getType()) {
            case IO_ERROR:
//                error = "Input/Output error";
                break;

            case DECODING_ERROR:
                error = "Image can't be decoded";
                break;

            case NETWORK_DENIED:
                error = "Downloads are denied";
                break;

            case OUT_OF_MEMORY:
                error = "Out Of Memory error";
                break;

            case UNKNOWN:
//                error = "Unknown error";
                break;
        }

        if (error.length()>0)
            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
        mLoading.setVisibility(View.GONE);
    }
}

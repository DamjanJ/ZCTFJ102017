package com.funny.bjokes;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import com.funny.bjokes.components.Logger;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by CJH on 2016.01.14.
 */
public class FunnyQuoteApplication extends Application {
    public static final Logger sLogger = new Logger(FunnyQuoteApplication.class.getSimpleName(), BuildConfig.DEBUG);
    private FirebaseAnalytics mFirebaseAnalytics;

    public static void initImageLoader(Context paramContext) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(paramContext).threadPriority(2).memoryCache(new WeakMemoryCache()).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(configuration);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(this.getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                clearSentFolder();
            }
        } else {
            clearSentFolder();
        }

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC);
    }

    private void clearSentFolder() {
        String path = String.valueOf(Environment.getExternalStorageDirectory().toString()) + "/" + this.getString(R.string.app_name) + "/sent";

        final File file = new File(path);
        if (file.exists()) {
            if (file.listFiles().length > 100) {
                for (File tempFile : file.listFiles()) {
                    tempFile.delete();
                }
                file.delete();
            }
        }
    }

    /**
     * Gets the default {@link FirebaseAnalytics} for this {@link Application}.
     *
     * @return tracker
     */

    synchronized public FirebaseAnalytics getFirebaseAnalytics() {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        return mFirebaseAnalytics;
    }
}

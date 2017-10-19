package com.funny.bjokes;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.funny.bjokes.adapters.FlowingAdapter;
import com.funny.bjokes.components.HackyViewPager;
import com.funny.bjokes.listeners.PageChangeListener;
import com.funny.bjokes.tasks.TaskRating;
import com.funny.bjokes.utils.DeviceUtils;
import com.funny.bjokes.utils.ImageUtils;
import com.funny.bjokes.utils.JsonUtils;
import com.funny.bjokes.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJH on 2016.01.14.
 */
public class DetailActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static final int ID_SHARE = 1;
    private static final int ID_SAVE = 2;
    private static final int ID_RATE = 3;
    private static final long DOWNLOAD_DELAY = 500;
    private final int REQUEST_CODE__SHARE = 1;
    public ImageLoader mImageLoader;
    public DisplayImageOptions mOptions;
    public FlowingAdapter mAdapter;
    public int mPage;
    public int mPosition;
    public Object FOR_SYNC = new Object();
    boolean isShowed;
    boolean isLoaded;
    AdView mAdsView_Google;
    InterstitialAd mInterstitialAds_Google = null;
    private FirebaseAnalytics mAnalytics;
    private List<ImageUtils.Item> mList;
    private TextView mRatingView;
    private int mTabNum;
    private HackyViewPager mViewPager;
    private Handler mHandler;
    private Runnable mAnimateAds;

    private Animation mSrink;
    private Animation mGrow;

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    private void downloadImage() {
        Toast.makeText(getApplicationContext(), getString(R.string.download_message_loading), Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                File resultImage = copyImage(mList.get(mPosition).url, true);
                Intent openGalleryIntent = new Intent();
                openGalleryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                openGalleryIntent.setAction(Intent.ACTION_VIEW);
                Uri pImageUri = getImageContentUri(getApplicationContext(), resultImage);
                openGalleryIntent.setDataAndType(pImageUri, "image/*");

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) (Math.random() * 100), openGalleryIntent, PendingIntent.FLAG_ONE_SHOT);


                Bitmap picture = BitmapFactory.decodeFile(resultImage.getPath());
                NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
                notiStyle.bigPicture(picture).setSummaryText(getResources().getString(R.string.download_message));

                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(getResources().getString(R.string.download_message))
                        .setStyle(notiStyle)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent).build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(1, notification);
            }
        }, DOWNLOAD_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        MobileAds.initialize(getApplicationContext(), getString(R.string.zct_ads_banner_id));

        mPosition = 0;
        mList = new ArrayList<ImageUtils.Item>();
        mImageLoader = ImageLoader.getInstance();

        Constants.initRefresh();
        setContentView(R.layout.activity_detail);

        FunnyQuoteApplication application = (FunnyQuoteApplication) getApplication();
        mAnalytics = application.getFirebaseAnalytics();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPosition = extras.getInt(Constants.BUNDLE_KEY__POSITION, 0);
            mPage = extras.getInt(Constants.BUNDLE_KEY__PAGE);
            mTabNum = extras.getInt(Constants.BUNDLE_KEY__TAB_INDEX);
            mList = ImageUtils.ImgFromJson(extras.getString(Constants.BUNDLE_KEY__IMAGE_LIST));
        }

        mOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).delayBeforeLoading(100).resetViewBeforeLoading(true).cacheInMemory(false).cacheOnDisk(true).considerExifParams(true).imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).build();
        // rate part

        mAdapter = new FlowingAdapter(this, getApplicationContext());
        mViewPager = (HackyViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new PageChangeListener(this));

        mAdapter.updateList(mList);
        mViewPager.setCurrentItem(mPosition);

        mRatingView = (TextView) findViewById(R.id.textRating);
        showRating(mPosition);

        mSrink = AnimationUtils.loadAnimation(this, R.anim.shrink_to_middle);
        mSrink.setAnimationListener(this);

        mGrow = AnimationUtils.loadAnimation(this, R.anim.grow_from_middle);
        mGrow.setAnimationListener(this);

        mAnimateAds = new Runnable() {
            @Override
            public void run() {
                mAdsView_Google.clearAnimation();
                mAdsView_Google.setAnimation(mSrink);
                mAdsView_Google.startAnimation(mSrink);
            }
        };

        mAdsView_Google = (AdView) findViewById(R.id.ads_google);
        mAdsView_Google.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mHandler.post(mAnimateAds);
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();
        if (mAdsView_Google != null)
            mAdsView_Google.loadAd(adRequest);


        /* Changed by Damjan 15.10.2017
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        if (mAdsView_Google != null)
            mAdsView_Google.loadAd(adRequest);
        */

        initAds();
    }

    private int getItem(int offset) {
        return offset + mViewPager.getCurrentItem();
    }

    public void onClickLeft(View view) {
        if (mViewPager.getCurrentItem() > 0) {
            if (!showInterstitial()) {
                mViewPager.setCurrentItem(getItem(-1));
            }
        }
    }

    public void onClickRight(View view) {
        if (mViewPager.getCurrentItem() + 1 < mViewPager.getAdapter().getCount()) {
            if (!showInterstitial()) {
                mViewPager.setCurrentItem(getItem(1));
            }
        }
    }

    public void onClickLike(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("Action", "Liked");
        mAnalytics.logEvent("Action", bundle);
        sendRating(1);
    }

    public void onClickDislike(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("Action", "Disliked");
        mAnalytics.logEvent("Action", bundle);
        sendRating(-1);
    }

    public void onClickShare(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("Action", "Shared");
        mAnalytics.logEvent("Action", bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.SHARE_REQUEST_CODE);
            } else {
                shareImage();
            }
        } else {
            shareImage();
        }
    }

    public void onClickDownload(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Ask for permission
                ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.DOWNLOAD_REQUEST_CODE);
            } else {
                downloadImage();
            }
        } else {
            downloadImage();
        }
    }

    public void onClickRate(View view) {
        final Uri uri = Uri.parse("market://details?id=" + getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
            startActivity(rateAppIntent);
        }
    }

    public void shareImage() {
        File file = copyImage(mList.get(mPosition).url, false);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivityForResult(Intent.createChooser(intent, "Share using"), REQUEST_CODE__SHARE);
    }

    private void sendRating(int rating) {
        int id = mList.get(mPosition).id;
        if (id != 0) {
            String url = "&quotes_id=" + id + "&user_id=" + DeviceUtils.getDeviceID(getApplicationContext());
            if (rating == 1)
                url = Constants.API_URL__RATING.replaceFirst("<rating>", "like") + url;
            if (rating == -1)
                url = Constants.API_URL__RATING.replaceFirst("<rating>", "dislike") + url;

            new TaskRating(DetailActivity.this, mTabNum).execute(new String[]{url});
            FunnyQuoteApplication.sLogger.i(url);
        }
    }

    public void showRating(int position) {
        mPosition = position;
        int rating = mList.get(position).rv;
        String result = rating + "";
        if (rating > 0) {
            result = "+" + result;
        }

        if (mRatingView == null) {
            mRatingView = (TextView) findViewById(R.id.textRating);
        }
        mRatingView.setText(result);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.BUNDLE_KEY__IMAGE_LIST, ImageUtils.ImgToJson(mList));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mList = ImageUtils.ImgFromJson(savedInstanceState.getString(Constants.BUNDLE_KEY__IMAGE_LIST));
        mAdapter.updateList(mList);
    }

    public void updateList() {
        mAdapter.updateList(mList);
    }

    /**
     * @param s
     * @param b if this argument is TRUE than image is stored in published to gallery, else it is just stored locally.
     * @return
     */
    public File copyImage(String s, boolean b) {
        String path = String.valueOf(Environment.getExternalStorageDirectory().toString()) + "/" + this.getString(R.string.app_name);

        if (!b) {
            // Use this path for sharing purpose
            path += "/sent";
        }

        final String filename = s.substring(45);//String.format("%s.jpg", s);
        final File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (!b) {
            File noMediaFile = new File(path, ".nomedia");
            if (!noMediaFile.exists()) {
                try {
                    noMediaFile.createNewFile();
                } catch (IOException ioe) {
                    FunnyQuoteApplication.sLogger.e(ioe.toString());
                }
            }
        }

        File file2 = null;
        final File inCache = DiskCacheUtils.findInCache(s, ImageLoader.getInstance().getDiskCache());
        if (inCache != null) {
            File file3 = null;
            try {
                file3 = new File(path, filename);
                final File file4 = inCache;
                final File file5 = file3;

                ImageUtils.copyFile(file4, file5);
                final boolean b2 = b;
                if (b2) {
                    final File file6 = file3;
                    addImageToGallery(file6);
                    return file3;
                }

                return file3;
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }

        return null;
    }

    public void addImageToGallery(File file) {
        final Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        this.sendBroadcast(intent);
    }

    public void loadMoreData(int page) {
        String url = Constants.API_URL__FEED + "&page=" + page + "&category=" + mTabNum + "&user_id=" + DeviceUtils.getDeviceID(getApplicationContext());
        new TaskGallery(this).execute(new String[]{url});
        FunnyQuoteApplication.sLogger.i(url);
    }

    public void checkNetworkState() {
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected() == false && mMobile.isConnected() == false) {
                Toast.makeText(this, "No connection is detected", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }




    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAds_Google.loadAd(adRequest);
    }


    /*
    Old Interstitial, changed 15.10.2017. By Damjan

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        mInterstitialAds_Google.loadAd(adRequest);
    }
    */

    public void initAds() {
        isShowed = false;
        isLoaded = false;

        if (Constants.GOOGLE_ADS) {
            mInterstitialAds_Google = new InterstitialAd(this);

            mInterstitialAds_Google.setAdUnitId(getString(R.string.zct_ads_interstitial_id));

            /* Old Inters Ads (by google) used for testing. changed by Damjan 15.10.2017.
            mInterstitialAds_Google.setAdUnitId(getString(R.string.google_ads_interstitial_id));*/

            mInterstitialAds_Google.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        }

        if (Constants.ADCOLONY_ADS) {
            AdColony.configure(this, "version:1.0,store:google", Constants.ADCOLONY_ADS__APP_ID, Constants.ADCOLONY_ADS__ZONE_ID);
            AdColony.addAdAvailabilityListener(new AdColonyAdAvailabilityListener() {
                @Override
                public void onAdColonyAdAvailabilityChange(boolean b, String s) {
                    if (b) {
                        if (Constants.ADS_FIRST_TIME) {
                            showInterstitial();
                            Constants.PAGE_VIEWED = 0;
                            Constants.ADS_FIRST_TIME = false;
                            isLoaded = true;
                        } else {
                            isLoaded = true;
                        }
                    }
                }
            });
        }
    }

    private boolean showInterstitial() {
        if (Constants.GOOGLE_ADS) {
            if (Constants.PAGE_VIEWED != 0 && Constants.PAGE_VIEWED % Constants.ADS_LIMIT_REVIEW__GOOGLE == 0) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "Google Admob");
                mAnalytics.logEvent("Action", bundle);

                Constants.PAGE_VIEWED = 0;

                if (mInterstitialAds_Google.isLoaded()) {
                    mInterstitialAds_Google.show();
                }
                return true;
            } else {
                Constants.PAGE_VIEWED++;
                return false;
            }
        }

        if (Constants.ADCOLONY_ADS) {
            if (Constants.PAGE_VIEWED != 0 && Constants.PAGE_VIEWED % Constants.ADS_LIMIT_REVIEW__ADCOLONY == 0) {
                Bundle bundle = new Bundle();
                bundle.putString("action", "Adcolony Ads");
                mAnalytics.logEvent("Action", bundle);

                Constants.PAGE_VIEWED = 0;

                AdColonyVideoAd mInterstitialAds_Adcolony = new AdColonyVideoAd(Constants.ADCOLONY_ADS__ZONE_ID).withListener(new AdColonyAdListener() {
                    @Override
                    public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {
//                    Log.i("AdColony", "finished");
                    }

                    @Override
                    public void onAdColonyAdStarted(AdColonyAd adColonyAd) {
//                    Log.i("AdColony", "started");

//                    try {
//                        mTracker.setScreenName(DeviceUtils.getDeviceID(DetailActivity.this) + UUID.randomUUID().toString());
//                        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//                    } catch (Exception ann) {}
                    }
                });
                mInterstitialAds_Adcolony.show();
                return true;
            } else {
                Constants.PAGE_VIEWED++;
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.GOOGLE_ADS && mAdsView_Google != null) {
            mAdsView_Google.pause();
        }

        if (Constants.ADCOLONY_ADS) {
            AdColony.pause();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Constants.GOOGLE_ADS && mAdsView_Google != null) {
            mAdsView_Google.resume();
        }

        if (Constants.ADCOLONY_ADS) {
            AdColony.resume(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (Constants.GOOGLE_ADS && mAdsView_Google != null) {
            mAdsView_Google.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.SHARE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    shareImage();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.cant_share), Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case Constants.DOWNLOAD_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    downloadImage();

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.cant_download), Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == mSrink) {
            mAdsView_Google.clearAnimation();
            mAdsView_Google.setAnimation(mGrow);
            mAdsView_Google.startAnimation(mGrow);
        } else {
            mHandler.postDelayed(mAnimateAds, Constants.ANIM_ADS_DELAY);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public class TaskGallery extends AsyncTask<String, Integer, String> {
        DetailActivity mInstance;
        ProgressBar mLoading;

        public TaskGallery(DetailActivity fragment) {
            mInstance = fragment;
            mLoading = (ProgressBar) mInstance.findViewById(R.id.loading);
        }

        @Override
        protected void onPreExecute() {
            mLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.readJSON(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s != null && s.length() > 0) {

                    try {
                        JSONObject res = new JSONObject(s);
                        JSONObject status = res.getJSONObject("status");
                        if (Utils.checkNull(status.getInt("code"), -1) == 0) {
                            JSONArray result = res.getJSONObject("response").getJSONArray("list");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject obj = result.getJSONObject(i);
                                ImageUtils.Item item = new ImageUtils.Item();
                                item.id = Utils.checkNull(obj.getString(Constants.API_FIELD__ID), 0);
                                item.rv = Utils.checkNull(obj.getString(Constants.API_FIELD__RV), 0);
                                item.url = Constants.API_URL__BASEPATH + Utils.checkNull(obj.getString(Constants.API_FIELD__URL));

                                synchronized (FOR_SYNC) {
                                    mList.add(item);
                                }
                            }
                        }
                    } catch (JSONException ee) {
                        ee.printStackTrace();
                    }

                    updateList();
                } else {
//                Toast.makeText(mInstance.getActivity(), R.string.update_gallery_failed, Toast.LENGTH_SHORT).show();
                }

                mLoading.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            checkNetworkState();
        }
    }
}

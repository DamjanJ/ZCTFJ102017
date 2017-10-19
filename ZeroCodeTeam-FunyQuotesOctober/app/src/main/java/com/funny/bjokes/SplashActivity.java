package com.funny.bjokes;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.funny.bjokes.fcm.RegistrationIntentService;
import com.funny.bjokes.utils.JsonUtils;
import com.funny.bjokes.utils.PrefsUtils;
import com.funny.bjokes.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by CJH on 2016.01.16.
 */
public class SplashActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int SPLASH_SCREEN_DELAY_TIME = 1000; // time is in ms
    private boolean isNew;
    private Handler mHandler;
    private Runnable mGo2MainDelayed = new Runnable() {
        @Override
        public void run() {
            Intent go2Main = new Intent(SplashActivity.this, MainActivity.class);
            go2Main.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            go2Main.putExtra(Constants.BUNDLE_KEY__NEW_TAB, isNew);
            startActivity(go2Main);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefsUtils.setNewFeed(this, 0);

        isNew = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isNew = extras.getBoolean(Constants.BUNDLE_KEY__NEW_TAB, false);
        }

        setContentView(R.layout.activity_splash);
        mHandler = new Handler();

        // Check google play service, if not present kill app
        checkPlayServices();
//        if (checkPlayServices()) {
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }

        new TaskSetting().execute(new String[]{Constants.API_URL__ADMOB});
    }

    public class TaskSetting extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PrefsUtils.initAdmob();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.readJSON(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject res = new JSONObject(s);
                JSONObject status = res.getJSONObject("status");
                if (Utils.checkNull(status.getInt("code"), -1) == 0) {
                    JSONArray list = res.getJSONArray("response");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        String kind = Utils.checkNull(item.getString("kind"));
                        int state = Utils.checkNull(item.getString("status"), 0);
                        int frequency = Utils.checkNull(item.getString("frequency"), 0);

                        FunnyQuoteApplication.sLogger.d(kind + ">>>" + state);
                        if (state == 1) {
                            if (kind.equals("google")) {
                                Constants.GOOGLE_ADS = true;
                                Constants.ADS_LIMIT_REVIEW__GOOGLE = frequency;
                            }

                            if (kind.equals("adcolony")) {
                                Constants.ADCOLONY_ADS = true;
                                Constants.ADS_LIMIT_REVIEW__ADCOLONY = frequency;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mGo2MainDelayed, SPLASH_SCREEN_DELAY_TIME);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                FunnyQuoteApplication.sLogger.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}

package com.funny.bjokes.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.funny.bjokes.Constants;
import com.funny.bjokes.FunnyQuoteApplication;
import com.funny.bjokes.R;
import com.funny.bjokes.utils.DeviceUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CJH on 2016.01.26.
 */
public class RegistrationIntentService extends IntentService {
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    FirebaseMessaging.INSTANCE_ID_SCOPE);
            // [END get_token]
            FunnyQuoteApplication.sLogger.i("GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
//            subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, true).apply();
            // [END register_for_gcm]
        } catch (Exception e) {
            FunnyQuoteApplication.sLogger.d("Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    public void sendRegistrationToServer(String token) {
        String url = Constants.API_URL__PUSH + "?user_id=" + DeviceUtils.getDeviceID(this) + "&android_id=" + token;
        FunnyQuoteApplication.sLogger.i("URL>>>" + url);
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();

            String result = response.body().string().toString();
            FunnyQuoteApplication.sLogger.i("Response>>>" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

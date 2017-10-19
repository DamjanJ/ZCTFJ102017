package com.funny.bjokes.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.funny.bjokes.Constants;
import com.funny.bjokes.utils.JsonUtils;
import com.funny.bjokes.utils.Utils;

import org.json.JSONObject;

/**
 * Created by Core Station on 4/9/2017.
 */

public class TaskRating extends AsyncTask<String, Void, String> {
    Context mContext;
    private int mTabNum;

    public TaskRating(Context context, int tabNum) {
        this.mContext = context;
        this.mTabNum = tabNum;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
                String kind = res.getJSONObject("request").getString("method");
                String alert = "Failed to submit rating, please try again later.";
                JSONObject result = res.getJSONObject("response");
                int rv = Utils.checkNull(result.getInt("rv"), 0);
                int rate = Utils.checkNull(result.getInt("rate"), 0);

                if (kind.equals("like")) {
                    alert = "Upvoted";
                    if (rate > 1)
                        alert += " " + rate + " times";
                }

                if (kind.equals("dislike")) {
                    alert = "Downvoted";
                    if (rate < -1)
                        alert += " " + (-1 * rate) + " times";
                }

                Constants.HOT_REFRESH = true;
                Constants.NEED_REFRESH[mTabNum] = true;

                Toast.makeText(mContext, alert, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Failed to submit rating, please try again later.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Failed to submit rating, please try again later.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
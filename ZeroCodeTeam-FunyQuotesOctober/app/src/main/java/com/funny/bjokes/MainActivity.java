package com.funny.bjokes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.funny.bjokes.adapters.TabPagerAdapter;
import com.funny.bjokes.components.PromotionFragmentDialog;
import com.funny.bjokes.utils.JsonUtils;
import com.funny.bjokes.utils.PrefsUtils;
import com.funny.bjokes.utils.Utils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by CJH on 2016.01.14.
 */
public class MainActivity extends AppCompatActivity {

    private ViewPager mMainMenuPager;
    private TabPagerAdapter mMainMenuAdapter;
    private ActionBar mActionBar;
    private ImageLoader mImageLoader;
    private FirebaseAnalytics mAnalytics;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new TaskPromotion().execute(new String[]{Constants.API_URL__PROMOTION});

        boolean isNew = false;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isNew = extras.getBoolean(Constants.BUNDLE_KEY__NEW_TAB, false);
        }

        FunnyQuoteApplication application = (FunnyQuoteApplication) getApplication();
        mAnalytics = application.getFirebaseAnalytics();

        mImageLoader = ImageLoader.getInstance();

        mMainMenuPager = (ViewPager) findViewById(R.id.pager);
        mMainMenuAdapter = new TabPagerAdapter(getApplicationContext(), getSupportFragmentManager());
        mMainMenuPager.setAdapter(mMainMenuAdapter);

        /* Damjan */



        mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setCustomView(R.layout.layout_actionbar);

        mMainMenuPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MainActivity.this.mActionBar = MainActivity.this.getSupportActionBar();
                try {
                    if (MainActivity.this.mActionBar != null) {
                        MainActivity.this.mActionBar.setSelectedNavigationItem(position);
                    }
                } catch (Exception e) {
                }
            }
        });

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
                int index = tab.getPosition();
                PrefsUtils.setSelectedTab(MainActivity.this.getApplicationContext(), index);
                MainActivity.this.mMainMenuPager.setCurrentItem(index);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

            }
        };

        mActionBar.addTab(mActionBar.newTab().setText("Hot").setTabListener(tabListener), false);
        mActionBar.addTab(mActionBar.newTab().setText("New").setTabListener(tabListener), false);
        mActionBar.addTab(mActionBar.newTab().setText("Top").setTabListener(tabListener), false);
        mActionBar.addTab(mActionBar.newTab().setText("Fav").setTabListener(tabListener), false);

        int tabIndex = PrefsUtils.getSelectedTab(this);
        if (isNew)
            tabIndex = 1;
        mActionBar.setSelectedNavigationItem(tabIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuid_refresh:
                refresh();

                Bundle bundle = new Bundle();
                bundle.putString("action", "Refreshed");
                mAnalytics.logEvent("Action", bundle);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        mImageLoader.clearMemoryCache();
        mImageLoader.clearDiskCache();
        mMainMenuAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FunnyQuoteApplication.sLogger.i("OnResume");
        if (Constants.HOT_REFRESH)
            refresh();
    }

    @Override

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to Exit?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).show();
    }

    /*
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }*/



    public class TaskPromotion extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
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
                            JSONObject promotionIcon = res.getJSONObject("response");
                            JSONObject promotionUrl = res.getJSONObject("response");


                            final String promotionIconStr = Utils.checkNull(promotionIcon.getString(Constants.API_FIELD_PRO_ICON), "");
                            final String promotionUrlStr = Utils.checkNull(promotionUrl.getString(Constants.API_FIELD_PRO_URL), "");

                            if (promotionUrlStr.isEmpty() || promotionIconStr.contains("nothing.png")) {
                                return;
                            }
                            FunnyQuoteApplication.sLogger.e(promotionIconStr);
                            FunnyQuoteApplication.sLogger.e(promotionUrlStr);

                            Handler handler = new Handler();
                            final URL[] url = new URL[1];
                            new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        url[0] = new URL(promotionIconStr);
                                        final Bitmap bmp = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());
                                        PromotionFragmentDialog.newInstance(bmp, promotionUrlStr).show(getFragmentManager(), "");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }
                    } catch (JSONException ee) {
                        ee.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

package com.funny.bjokes.components;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.funny.bjokes.Constants;
import com.funny.bjokes.DetailActivity;
import com.funny.bjokes.FunnyQuoteApplication;
import com.funny.bjokes.R;
import com.funny.bjokes.adapters.FeedAdapter;
import com.funny.bjokes.listeners.EndlessScrollListener;
import com.funny.bjokes.utils.DeviceUtils;
import com.funny.bjokes.utils.ImageUtils;
import com.funny.bjokes.utils.JsonUtils;
import com.funny.bjokes.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by CJH on 2016.01.14.
 */
public class TabFrg extends Fragment {

    public View mView;
    public FeedAdapter mAdapter;
    public List<ImageUtils.Item> mList;
    private GridView mListView;

    private int mTabNum, mPage;

    public ImageLoader mImageLoader;
    public DisplayImageOptions mOptions;

    public TabFrg() {
        mList = new ArrayList<ImageUtils.Item>();
    }

    public static TabFrg newInstance(int tabIndex) {
        TabFrg fragment = new TabFrg();
        Bundle arg = new Bundle();
        arg.putInt(Constants.BUNDLE_KEY__TAB_INDEX, tabIndex);
        fragment.setArguments(arg);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if (arg != null) {
            mTabNum = arg.getInt(Constants.BUNDLE_KEY__TAB_INDEX, 0);
        } else {
            mTabNum = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mImageLoader = ImageLoader.getInstance();
        mOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_loading).showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        mListView = (GridView) mView.findViewById(R.id.gridview);
        mAdapter = new FeedAdapter(this, getActivity().getApplicationContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDetail(position);
            }
        });

        mListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int count) {
                mPage = page;
//                Log.i("FrgMain", "OnScrollListener  page>>>" + page);
                if (mList.size() >= page * Constants.PAGE_SIZE)
                    loadMoreData(page);
            }
        });

        loadMoreData(0);

        return mView;
    }

    public void loadMoreData(int page) {
        String url = Constants.API_URL__FEED + "&page=" + page + "&category=" + mTabNum + "&user_id=" + DeviceUtils.getDeviceID(getActivity().getApplicationContext());
        new TaskGallery(this).execute(new String[]{url});
        FunnyQuoteApplication.sLogger.e(url);
    }

    public void showDetail(int position) {
        final Intent intent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
        intent.putExtra(Constants.BUNDLE_KEY__POSITION, position);
        intent.putExtra(Constants.BUNDLE_KEY__PAGE, mPage);
        intent.putExtra(Constants.BUNDLE_KEY__TAB_INDEX, mTabNum);
        intent.putExtra(Constants.BUNDLE_KEY__IMAGE_LIST, ImageUtils.ImgToJson(mList));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.BUNDLE_KEY__IMAGE_LIST + mTabNum, ImageUtils.ImgToJson(mList));
    }

    public void onClickRefresh() {
        mImageLoader.clearMemoryCache();
        mImageLoader.clearDiskCache();
        mList.clear();
        loadMoreData(0);
    }

    @Override
    public void onResume() {
        super.onResume();

//        Log.i("TabFragment", "OnResume>>>" + mTabNum);
//        if ((mTabNum==0 && Constants.HOT_REFRESH) || Constants.NEED_REFRESH[mTabNum]) {
//            Constants.NEED_REFRESH[mTabNum] = false;
//            onClickRefresh();
////            Log.e("TabFragment", "OnResume>>>" + mTabNum + " Refresh");
//        }
    }

    public void updateList() {
        mAdapter.updateList(mList);
    }

    public class TaskGallery extends AsyncTask<String, Integer, String> {

        TabFrg mInstance;
        ProgressBar mLoading;
        String page;

        public TaskGallery(TabFrg fragment) {
            mInstance = fragment;
            mLoading = (ProgressBar) mInstance.mView.findViewById(R.id.loading);
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
//                    Log.d("TaskGallery", "PostExecuted>>>" + s);

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

                                mInstance.mList.add(item);
                            }
                        }
                    } catch (JSONException ee) {
                        ee.printStackTrace();
                    }

                    mInstance.updateList();
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

    public void checkNetworkState() {
        try {
            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mWifi.isConnected() == false && mMobile.isConnected() == false) {
                Toast.makeText(getActivity(), "No connection is detected", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

}

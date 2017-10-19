package com.funny.bjokes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.funny.bjokes.R;
import com.funny.bjokes.components.TabFrg;
import com.funny.bjokes.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CJH on 2016.01.14.
 */
public class FeedAdapter extends BaseAdapter {

    private TabFrg mInstance;
    private Context mContext;
    private List<ImageUtils.Item> mImg = new ArrayList();
    private final LayoutInflater mInflater;

    public FeedAdapter(TabFrg fragment, Context paramContext) {
        this.mInstance = fragment;
        this.mContext = paramContext;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    public int getCount() {
        return mImg.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflate = convertView;
        if (convertView == null) {
            inflate = mInflater.inflate(R.layout.item_gallery, parent, false);
        } else {
        }

        String t = "";
        int rating = mImg.get(position).rv;
        if (rating <= 0)
            t = rating + "";
        else
            t = "+" + rating;

        ImageView mImageView = (ImageView) inflate.findViewById(R.id.image);
        TextView mRatingView = (TextView) inflate.findViewById(R.id.tv_rating);
        ImageView gifSign = (ImageView) inflate.findViewById(R.id.gif_sign_menu);
        mInstance.mImageLoader.displayImage(mImg.get(position).url, mImageView, mInstance.mOptions);

        // Set GIF overlay if image is gif
        if (mImg.get(position).url.endsWith(".gif")) {
            gifSign.setVisibility(View.VISIBLE);
        } else {
            gifSign.setVisibility(View.GONE);
        }

        mRatingView.setText(t);
        return inflate;
    }

    public void updateList(final List<ImageUtils.Item> imgs) {
        this.mImg = imgs;
        this.notifyDataSetChanged();
    }
}

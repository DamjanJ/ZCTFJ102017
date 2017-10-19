package com.funny.bjokes.components;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.funny.bjokes.R;


/**
 * Created by CJH on 2016.01.14.
 */
public class FeedViewHolder {
    public ImageView mImageView;
    public TextView mRatingView;

    public FeedViewHolder(View view) {
        mImageView = (ImageView) view.findViewById(R.id.image);
        mRatingView = (TextView) view.findViewById(R.id.tv_rating);
    }
}

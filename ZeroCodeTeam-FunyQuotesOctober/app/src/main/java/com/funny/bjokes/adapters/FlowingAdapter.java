package com.funny.bjokes.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.transcode.BitmapToGlideDrawableTranscoder;
import com.funny.bjokes.DetailActivity;
import com.funny.bjokes.R;
import com.funny.bjokes.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by CJH on 2016.01.14.
 */
public class FlowingAdapter extends PagerAdapter {

    private DetailActivity mInstance;
    private Context mContext;
    private List<ImageUtils.Item> mImg = new ArrayList();
    private final LayoutInflater mInflater;
    private boolean isPlaying;

    public FlowingAdapter(DetailActivity activity, Context context) {
        mInstance = activity;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mImg.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void destroyItem(ViewGroup viewGroup, int position, Object params) {

    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View inflate = mInflater.inflate(R.layout.item_pager, container, false);
        final PhotoView photoView = (PhotoView) inflate.findViewById(R.id.image);
        final View gifSign = inflate.findViewById(R.id.gif_sign);
        isPlaying = false;
        
        // check if the list item doesn`t have an image url
        if (mImg.get(position).url != null) {
            final BitmapRequestBuilder<String, GlideDrawable> thumbRequest = Glide
                    .with(mContext)
                    .load(mImg.get(position).url)
                    .asBitmap() // force first frame for Gif
                    .transcode(new BitmapToGlideDrawableTranscoder(mContext), GlideDrawable.class)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter();

            thumbRequest.into(photoView);

            // check if the image is regular or gif
            if (mImg.get(position).url.endsWith(".gif")) {
                gifSign.setVisibility(View.VISIBLE);
                photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        if (gifSign.getVisibility() != View.VISIBLE) {
                            //STOP
                            thumbRequest.into(photoView);
                            gifSign.setVisibility(View.VISIBLE);
                        } else {
                            //PLAY
                            gifSign.setVisibility(View.GONE);
                            Glide.with(mContext)
                                    .load(mImg.get(position).url) // load as usual (Gif as animated, other formats as Bitmap)
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .thumbnail(thumbRequest)
                                    .dontAnimate()
                                    .into(photoView);
                            gifSign.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }


        ((ViewPager) container).addView(inflate, 0);
        return inflate;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    public void updateList(List<ImageUtils.Item> paramList) {
        mImg = paramList;
        notifyDataSetChanged();
    }
}

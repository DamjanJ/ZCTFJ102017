package com.funny.bjokes.components;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.funny.bjokes.Constants;
import com.funny.bjokes.R;

/**
 * Created by Rade on 3/13/2016.
 */
public class PromotionFragmentDialog extends DialogFragment {

    private static String IMAGE_URL = "image_url";
    private static String APP_URL = "app_url";
    private static Bitmap bmp;
    private View mView;
    private ImageView mImage;
    private String appUrl;
    private View.OnClickListener mGlobalClickListener;
    private View mCloseDialog;

    /**
     * Create a new instance of InputDialog, providing "CategoryID"
     * as an argument.
     */
    public static PromotionFragmentDialog newInstance(Bitmap bitmap, String appUrl) {

        PromotionFragmentDialog dialog = new PromotionFragmentDialog();
        Bundle data = new Bundle();
        bmp = bitmap;
        data.putString(APP_URL, appUrl);
        dialog.setArguments(data);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyCustomTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout with host Activity inflater, because of onClick XML method
        mView = inflater.inflate(R.layout.d_f_promotion, container, false);
        setupAll(savedInstanceState);
        return mView;
    }

    protected void setupAll(Bundle savedInstanceState) {
        setupViews();
        setupListeners();

        appUrl = getArguments().getString(APP_URL);
        final String imageUrl = getArguments().getString(IMAGE_URL);
        final Handler handler = new Handler();

        mImage.setImageBitmap(bmp);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCloseDialog.setVisibility(View.VISIBLE);
            }
        }, Constants.DELAY_CLOSE_OPTION);
    }

    protected void setupViews() {
        mImage = (ImageView) mView.findViewById(R.id.image);
        mCloseDialog = (View) mView.findViewById(R.id.close_dialog);
    }

    protected void setupListeners() {
        prepareListeners();
        mImage.setOnClickListener(mGlobalClickListener);
        mCloseDialog.setOnClickListener(mGlobalClickListener);
    }

    protected void prepareListeners() {

        mGlobalClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.image:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            Toast.makeText(getActivity(), "Action not supported!", Toast.LENGTH_LONG).show();
                        }
                        dismiss();
                    case R.id.close_dialog:
                        dismiss();
                        break;
                }
            }
        };
    }
}

package com.funny.bjokes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Jaysiyaram on 4/18/2017.
 */

public class CustomDialogActivity extends Activity {

    private Button Click_btn, close;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.dialog_custom);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textView2= (TextView)findViewById(R.id.message);
        Click_btn= (Button)findViewById(R.id.positive_button);
        close =(Button)findViewById(R.id.close_button);


        Spanned text = Html.fromHtml("<font color=\"black\">You can check our <b>privacy policy</b></font>");
        textView2.setText(text);

        Click_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.google.com"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}

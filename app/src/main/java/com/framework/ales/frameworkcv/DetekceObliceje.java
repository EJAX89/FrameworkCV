package com.framework.ales.frameworkcv;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class DetekceObliceje extends Activity {

    private Context mContext;
    private static int screenHeight;
    private static int screenWidth;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_page);


        RelativeLayout.LayoutParams adViewlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        adViewlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adViewlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

        mContext = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

       /** final TextView headerText = (TextView) findViewById(R.id.mainHeaderText);
        RelativeLayout.LayoutParams headerTextParams = (RelativeLayout.LayoutParams) headerText.getLayoutParams();
        headerTextParams.leftMargin = screenHeight / 8;
        headerText.setLayoutParams(headerTextParams);
        headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) screenHeight / 35);
        headerText.setText("Face Recognition");
        headerText.setTextColor(Color.LTGRAY);
        headerText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        headerText.setTypeface(null, Typeface.BOLD);
**/
  //      ImageView headerIcon = (ImageView) findViewById(R.id.mainHeaderIcon);
        RelativeLayout.LayoutParams iconLParams = new RelativeLayout.LayoutParams(screenHeight / 11, screenHeight / 11);
        iconLParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT | RelativeLayout.CENTER_VERTICAL);
        iconLParams.leftMargin = screenHeight / 80;
//        headerIcon.setLayoutParams(iconLParams);


        ListView listView = (ListView) findViewById(R.id.mainListView);
        ListAdapter listAdapter = new ListAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(itemClickListener);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listView.getLayoutParams();
        params.leftMargin = screenWidth / 10;
        params.rightMargin = screenWidth / 10;
        params.topMargin = screenHeight / 12;
        listView.setLayoutParams(params);
        listView.setVerticalScrollBarEnabled(false);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

    OnItemClickListener itemClickListener = new OnItemClickListener() {

        private boolean buttonClicked = false;

        @Override
        public void onItemClick(AdapterView<?> a, View view, int position, long id) {
            if (position % 2 == 1) {
                return;
            }
            if (buttonClicked) {
                return;
            }
            buttonClicked = true;
            final RelativeLayout itemLayout = (RelativeLayout) view;
            //final ImageView bgImage = (ImageView)itemLayout.getChildAt(0);
            final TextView itemText = (TextView) itemLayout.getChildAt(1);
            //bgImage.setImageResource(R.drawable.backdenemepr2);
            final RelativeLayout overLay = new RelativeLayout(mContext);
            overLay.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            overLay.setBackgroundColor(0x66888888);
            itemLayout.addView(overLay);
            itemText.setTextColor(Color.DKGRAY);

        }
    };

    OnTouchListener onItemsTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            switch (action) {
                case MotionEvent.ACTION_DOWN:

                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DetekceObliceje Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.framework.ales.frameworkcv/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DetekceObliceje Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.framework.ales.frameworkcv/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 5;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new RelativeLayout(mContext);
                convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, screenHeight / 7));
                if (position % 2 == 1) {
                    convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, screenHeight / 21));
                    convertView.setVisibility(View.INVISIBLE);
                    return convertView;
                }
                convertView.setBackgroundColor(Color.TRANSPARENT);
                //if(position != 1) {
                ImageView bgImage = new ImageView(mContext);
                bgImage.setScaleType(ScaleType.FIT_XY);
                bgImage.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
               // bgImage.setImageResource(R.drawable.btn);
                ((RelativeLayout) convertView).addView(bgImage);


                TextView itemText = new TextView(mContext);
                RelativeLayout.LayoutParams itemTextLParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
                //itemTextLParams.leftMargin = screenHeight/7;
                itemText.setLayoutParams(itemTextLParams);
                itemText.setGravity(Gravity.CENTER);
                itemText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) screenHeight / 40);
                itemText.setTextColor(Color.BLACK);
                itemText.setBackgroundColor(Color.TRANSPARENT);
                ((RelativeLayout) convertView).addView(itemText);
                itemText.setTextColor(Color.WHITE);
                ImageView icon = new ImageView(mContext);
                RelativeLayout.LayoutParams iconLParams = new RelativeLayout.LayoutParams(screenHeight / 7, screenHeight / 7);
                iconLParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                iconLParams.topMargin = screenHeight / 60;
                iconLParams.bottomMargin = screenHeight / 40;
                //iconLParams.leftMargin = screenHeight/80;
                icon.setLayoutParams(iconLParams);
                //((RelativeLayout)convertView).addView(icon);
                if (position == 0) {
                    itemText.setText("Train Recognizer");
                } else if (position == 2) {
                    itemText.setText("Face Recognition");
                } else if (position == 4) {
                    itemText.setText("Edit Database");
                }
                //}

                convertView.setOnTouchListener(onItemsTouchListener);

            }
            return convertView;
        }

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyUp(keyCode, event);
    }

}

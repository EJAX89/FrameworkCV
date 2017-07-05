package data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.framework.ales.frameworkcv.HlavniActivity;
import com.framework.ales.frameworkcv.R;

/**
 * Created by Aleš on 16.03.2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
    }
    public Integer[] mThumbIds = {
            R.drawable.p24239, R.drawable.p24252,
            R.drawable.p24297, R.drawable.p24299,
            R.drawable.p24324, R.drawable.p24328

    };

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(730, 500));
        return imageView;
    }



}

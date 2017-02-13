package com.scientists.happy.botanist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by wzhang on 2/13/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Intent intent;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public ImageAdapter(Context c, Intent i) {
        mContext = c;
        intent = i;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int width = mContext.getResources().getDisplayMetrics().widthPixels;
            imageView.setLayoutParams(new GridView.LayoutParams(width/2 - 50, width/2 - 50));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        if (intent.getExtras() != null) {
            String photoPath = (String) intent.getExtras().get("photoPath");
            File f = new File(photoPath);

            if(f.exists()) {
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                bmp = ImageUtils.correctRotation(photoPath, bmp);
                // ImageView image.setImageBitmap(bmp);
            }
        }

        imageView.setImageResource(mThumbIds[position]);

        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.flowey, R.drawable.flowey,
            R.drawable.flowey, R.drawable.flowey,
            R.drawable.flowey, R.drawable.flowey,
            R.drawable.flowey, R.drawable.flowey
    };
}

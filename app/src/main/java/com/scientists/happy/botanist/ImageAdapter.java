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
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by wzhang on 2/13/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private static final Integer DEFAULT_IMAGE = R.drawable.flowey;

    private Context mContext;
    private Intent intent;
    private HashSet<Bitmap> imgs = new HashSet<Bitmap>();
    PlantArray plantArray = PlantArray.getInstance();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public ImageAdapter(Context c, Intent i) {
        this(c);
        intent = i;
    }

    public int getCount() {
        return plantArray.size();
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
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            imageView.setLayoutParams(new GridView.LayoutParams(width/2 - 50, width/2 - 50));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        if (position < plantArray.size()) {
            Plant p = plantArray.get(position);
            if (p != null) {
                String photoPath = p.getPhotoPath();
                if(photoPath != null) {
                    File f = new File(photoPath);

                    if(f.exists()) {
                        Bitmap bmp = ImageUtils.loadScaledImage(photoPath, width, width);
                        imgs.add(bmp);
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setImageBitmap(bmp);
                    } else {
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        imageView.setImageResource(DEFAULT_IMAGE);
                    }
                } else {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageResource(DEFAULT_IMAGE);
                }
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(DEFAULT_IMAGE);
            }
        } else {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(DEFAULT_IMAGE);
        }
        return imageView;
    }

    public HashSet<Bitmap> getImgs()
    {
        return imgs;
    }
}

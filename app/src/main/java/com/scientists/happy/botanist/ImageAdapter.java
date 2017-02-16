package com.scientists.happy.botanist;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

import static android.widget.GridView.AUTO_FIT;

/**
 * Created by wzhang on 2/13/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private static final Integer DEFAULT_IMAGE = R.drawable.flowey;

    private Context mContext;
    //private HashSet<Bitmap> imgs = new HashSet<>();
    private PlantArray plantArray = PlantArray.getInstance();

    public ImageAdapter(Context c) {
        mContext = c;
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
        int height = (int)mContext.getResources().getDimension(R.dimen.profile_picture_height);
        int width = height / 2;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(AUTO_FIT, height));
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
                        Bitmap bmp = ImageUtils.loadScaledImage(photoPath, width, height);
                        //imgs.add(bmp);
                        imageView.setImageBitmap(bmp);
                    } else {
                        imageView.setImageResource(DEFAULT_IMAGE);
                    }
                } else {
                    imageView.setImageResource(DEFAULT_IMAGE);
                }
            } else {
                imageView.setImageResource(DEFAULT_IMAGE);
            }
        } else {
            imageView.setImageResource(DEFAULT_IMAGE);
        }
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
}

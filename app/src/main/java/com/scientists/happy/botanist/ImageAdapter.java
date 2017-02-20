package com.scientists.happy.botanist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.GridView.AUTO_FIT;

/**
 * Created by wzhang on 2/13/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private static final Integer DEFAULT_IMAGE = R.drawable.flowey;

    private Context mContext;
    private PlantArray plantArray = PlantArray.getInstance();
    private Activity activity;
    private LayoutInflater mInflater;

    public ImageAdapter(Context c, Activity activity) {
        super();
        mContext = c;
        this.mInflater = LayoutInflater.from(c);
        this.activity = activity;
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

    private static class ViewHolder {
        private ImageView imageView;
        private ProgressBar mProgress;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        ViewHolder vh = null;
        int height = (int)mContext.getResources().getDimension(R.dimen.profile_picture_height);
        int width = height / 2;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.row_grid_view, parent, false);
            vh.mProgress = (ProgressBar) convertView.findViewById(R.id.progress);
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(AUTO_FIT, height));
            vh.imageView = imageView;
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (position < plantArray.size()) {
            Plant p = plantArray.get(position);
            if (p != null) {
                String photoPath = p.getPhotoPath();
                if(photoPath != null) {
                    File f = new File(photoPath);
                    if(f.exists()) {
                        Bitmap bmp = ImageUtils.loadScaledImage(photoPath, width, height);
                        vh.imageView.setImageBitmap(bmp);
                    }
                }
            }
        }
        ProgressItem mItem = new ProgressItem(20); // placeholder for future watering time
        vh.mProgress.setProgress(mItem.getProgress());
        vh.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        convertView.setTag(vh);
        return convertView;
    }

}

package com.scientists.happy.botanist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

/**
 * Created by wzhang on 2/13/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private PlantArray plantArray = PlantArray.getInstance();
    private LayoutInflater mInflater;

    public ImageAdapter(Context c, Activity activity) {
        super();
        mContext = c;
        this.mInflater = LayoutInflater.from(c);
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
        private TextView nickName;
        private TextView species;
        private ProgressBar mProgress;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        int height = (int)mContext.getResources().getDimension(R.dimen.profile_picture_height);
        int width = height / 2;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item_view, parent, false);
            vh.mProgress = (ProgressBar)convertView.findViewById(R.id.progress);
            vh.imageView = (ImageView)convertView.findViewById(R.id.grid_item_image_view);
            vh.nickName = (TextView)convertView.findViewById(R.id.grid_item_nickname);
            vh.species = (TextView)convertView.findViewById(R.id.grid_item_species);
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
                vh.nickName.setText(p.getNickname());
                vh.species.setText(p.getSpecies());
            }
        }
        ProgressItem mItem = new ProgressItem(20); // placeholder for future watering time
        vh.mProgress.setProgress(mItem.getProgress());
        vh.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        convertView.setTag(vh);
        return convertView;
    }
}

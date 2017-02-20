package com.scientists.happy.botanist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzhang on 2/19/2017.
 */

public class ProgressBarAdapter extends BaseAdapter {

    private ArrayList<ProgressItem> mData;
    private LayoutInflater mInflater;

    public ProgressBarAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ProgressItem getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // if your items have any unique ids, return that instead
        return position;
    }

    public void setData(List<ProgressItem> newData) {
        this.mData.clear();
        if (newData != null && !newData.isEmpty()) {
            mData.addAll(newData);
        }
    }

    private static class ViewHolder {
        private ProgressBar mProgress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // view holder pattern
        ViewHolder vh = null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.content_main, parent, false);
            vh.mProgress = (ProgressBar) convertView.findViewById(R.id.progress);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        ProgressItem mItem = getItem(position);
        vh.mProgress.setProgress(mItem.getProgress());

        // do the remaining of the stuff here
        return convertView;
    }
}
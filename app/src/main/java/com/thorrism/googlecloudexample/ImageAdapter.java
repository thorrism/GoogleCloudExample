package com.thorrism.googlecloudexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

/**
 * Created by Lucas Crawford on 3/24/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mCtx;
    private List<Element> mData;

    public ImageAdapter(Context ctx, List<Element> data){
        mCtx = ctx;
        mData = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent ){
        ViewHolder holder = null;

        //ListViews re-use views
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mCtx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            //ImageView for the image in the gridview.
            holder.view    = (ImageView) convertView.findViewById(R.id.itemImage);
            holder.playBtn = (ImageView) convertView.findViewById(R.id.itemButton);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.element = getItem(position);
        holder.view.setImageBitmap(holder.element.getBitmap());
        holder.view.setVisibility(View.VISIBLE);

        //Add the play button for video elements
        if(holder.element.getType() == Utils.VIDEO_ELEM)
            holder.playBtn.setVisibility(View.VISIBLE);
        else
            holder.playBtn.setVisibility(View.GONE);

        return convertView;
    }

    public void updateData(List<Element> data){
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mData != null) return mData.size();
        else return 0;
    }

    @Override
    public Element getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    /**
     * View holder for utilizing the ViewHolder pattern. Avoids constantly calling findViewById
     */
    static class ViewHolder{
        public ImageView view;
        public ImageView playBtn;
        public Element element;
    }
}

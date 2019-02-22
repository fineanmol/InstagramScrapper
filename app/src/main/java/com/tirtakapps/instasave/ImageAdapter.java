package com.tirtakapps.instasave;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by gamew on 7.06.2017.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;
    List<String> imagesUrl;

    public ImageAdapter(Context context, List<String> imagesUrl) {
        this.context = context;
        this.imagesUrl = imagesUrl;
    }

    @Override
    public int getCount() {
        return imagesUrl.size();
    }

    @Override
    public Object getItem(int position) {
        return imagesUrl.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if(convertView == null){
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        }else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(context).load(imagesUrl.get(position)).into(imageView);

        return imageView;
    }
}

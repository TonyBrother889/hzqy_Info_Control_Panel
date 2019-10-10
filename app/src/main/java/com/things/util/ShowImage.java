package com.things.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by zhangxiang on 2018/3/9.
 */

public class ShowImage {

    public static void showImage(Context context, String imageUrl, ImageView imageView) {

        Glide.with(context)
                .load(imageUrl)
                //.placeholder(R.drawable.loading_spinner)
                .into(imageView);
    }

    public static void showImage(Context context, int localImageUrl, ImageView imageView) {

        Glide.with(context)
                .load(localImageUrl)
                //.placeholder(R.drawable.loading_spinner)
                .into(imageView);
    }
}

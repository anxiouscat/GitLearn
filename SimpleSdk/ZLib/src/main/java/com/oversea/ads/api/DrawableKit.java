package com.oversea.ads.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import com.oversea.ads.util.DensityUtil;

public class DrawableKit {
    /**
     * <shape xmlns:android="http://schemas.android.com/apk/res/android"
     *        android:shape="oval">
     *     <size
     *         android:width="6dp"
     *         android:height="6dp"/>
     *     <solid android:color="#d5d9d9"/>
     *
     * </shape>
     */

    public static Drawable createShapeOval(Context context, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        int width = DensityUtil.dip2px(context, 6);
        int height = width;
        drawable.setSize(width, height);

        return drawable;
    }

    /**
     * <shape xmlns:android="http://schemas.android.com/apk/res/android"
     *        android:shape="rectangle">
     *     <solid android:color="#99c4c4c4"/>
     *     <corners android:radius="20dp"/>
     *     <stroke
     *         android:width="0.7dp"
     *         android:color="#7fffffff"/>
     *
     * </shape>
     */
    @SuppressLint("NewApi")
    public static Drawable createRectangle(Context context, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        int width = DensityUtil.dip2px(context, 0.7f);
        int radius = 20;
        drawable.setStroke(width, ColorStateList.valueOf(Color.parseColor("#7fffffff")));
        drawable.setCornerRadius(radius);

        int w = DensityUtil.dip2px(context, 60);
        int height = w/2;
        drawable.setSize(width, height);

        return drawable;
    }
}

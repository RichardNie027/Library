package com.nec.lib.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

public class ImageUtil {

    public static BitmapFactory.Options getBitmapOption(int inSampleSize){
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    public static Bitmap getBitmap(Context context, int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        TypedValue value = new TypedValue();
        context.getResources().openRawResource(resId, value);
        options.inTargetDensity = value.density;
        options.inScaled=false;//不缩放
        return BitmapFactory.decodeResource(context.getResources(), resId, options);
    }

    /****************************************************************************/
    /************************* Drawable Bitmap Res 的转换 ***********************/
    /****************************************************************************/

    public static Bitmap getBitmapFormResources(Context context,int resId){
        return BitmapFactory.decodeResource(context.getResources(),resId);
    }

    public static Drawable getDrawableFromResources(Context context, int resId){
        //return context.getResources().getDrawable(resId);   //deprecated
        return ContextCompat.getDrawable(context, resId);
    }

    public static Drawable getDrawbleFormBitmap(Context context,Bitmap bitmap){
        return new BitmapDrawable(context.getResources(),bitmap);
    }

    public static Bitmap getBitmapFormDrawable(Context context,Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),drawable.getOpacity()!= PixelFormat.OPAQUE
                        ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        //设置绘画的边界，此处表示完整绘制
        drawable.draw(canvas);
        return bitmap;
    }
}

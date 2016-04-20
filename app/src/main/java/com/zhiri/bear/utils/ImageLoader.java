package com.zhiri.bear.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;

/**
 * Created by MagicBean on 2016/01/13 15:15:37
 */
public class ImageLoader {
    public static void loadImage(Activity activity, String url, ImageView view) {
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(view);
    }

    public static void loadImage(Activity activity, String url, int placeholder, ImageView view) {
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).error(placeholder).into(view);
    }

    public static void loadImage(Activity activity, String url, int placeholder, int width, int height, ImageView view) {
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).override(width, height).error(placeholder).into(view);
    }

    public static void loadImage(Fragment fragment, String url, ImageView view) {
        Glide.with(fragment)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
//                .placeholder(R.drawable.loading_spinner)
                .crossFade()
                .into(view);

    }

    public static void loadImage(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
//                .placeholder(R.drawable.loading_spinner)
//                .centerCrop()
                .into(view);

    }

    public static void loadImage(Context context, Uri url, ImageView view) {
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
//                .placeholder(R.drawable.loading_spinner)
                .crossFade()
                .into(view);

    }

    public static void loadCicleImage(Activity activity, String url, ImageView view) {
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(activity)).into(view);
    }

    public static void loadCicleImage(Activity activity, String url, int placeholder, ImageView view) {
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(placeholder).error(placeholder).bitmapTransform(new CropCircleTransformation(activity)).into(view);
    }

    public static void loadCicleImage(Activity activity, int resId, ImageView view) {
        Glide.with(activity).load(resId).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(activity)).into(view);
    }

    public static void loadCicleImage(Fragment fragment, int resId, ImageView view) {
        Glide.with(fragment).load(resId).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(fragment.getActivity())).into(view);
    }

    public static void loadCicleImage(Fragment fragment, String url, ImageView view) {
        Glide.with(fragment).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).bitmapTransform(new CropCircleTransformation(fragment.getActivity())).into(view);
    }

    public static void loadCicleImage(Fragment fragment, String url, int placeholder, ImageView view) {
        Glide.with(fragment)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholder).error(placeholder)
                .bitmapTransform(new CropCircleTransformation(fragment.getActivity()))
                .into(view);
    }

    public static void loadImage(Activity activity, int resId, ImageView imageView) {
        Glide.with(activity).load(resId).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    public static class CropCircleTransformation implements Transformation<Bitmap> {

        private BitmapPool mBitmapPool;

        public CropCircleTransformation(Context context) {
            this(Glide.get(context).getBitmapPool());
        }

        public CropCircleTransformation(BitmapPool pool) {
            this.mBitmapPool = pool;
        }

        @Override
        public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
            Bitmap source = resource.get();
            int size = Math.min(source.getWidth(), source.getHeight());

            int width = (source.getWidth() - size) / 2;
            int height = (source.getHeight() - size) / 2;

            Bitmap bitmap = mBitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            if (width != 0 || height != 0) {
                // source isn't square, move viewport to center
                Matrix matrix = new Matrix();
                matrix.setTranslate(-width, -height);
                shader.setLocalMatrix(matrix);
            }
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);
            return BitmapResource.obtain(bitmap, mBitmapPool);
        }

        @Override
        public String getId() {
            return "CropCircleTransformation()";
        }
    }
}

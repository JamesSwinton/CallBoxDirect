package com.zebra.jamesswinton.wfccallboxdirect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GetPromoImagesAsync extends AsyncTask<Void, Void, List<Bitmap>> {

    // Context
    private WeakReference<Context> mContextWeakRef;
    private OnPromoImagesFoundListener mOnPromoImagesFoundListener;

    public GetPromoImagesAsync(Context context, OnPromoImagesFoundListener onPromoImagesFoundListener) {
        this.mContextWeakRef = new WeakReference<>(context);
        this.mOnPromoImagesFoundListener = onPromoImagesFoundListener;
    }

    @Override
    protected List<Bitmap> doInBackground(Void... voids) {
        List<Bitmap> promoImageBitmaps = new ArrayList<>();
        File imagesDirectory = new File("/sdcard/callbox-direct-promo-images/");
        if (imagesDirectory.exists()) {
            if (imagesDirectory.isDirectory()) {
                File[] imageFiles = imagesDirectory.listFiles();
                if (imageFiles != null && imageFiles.length != 0) {
                    for (File imageFile : imageFiles) {
                        promoImageBitmaps.add(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                    } return promoImageBitmaps;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            imagesDirectory.mkdirs();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Bitmap> result) {
        super.onPostExecute(result);
        if (result != null) {
            mOnPromoImagesFoundListener.onImagesFound(result);
        } else {
            mOnPromoImagesFoundListener.onError("No promo images found");
        }
    }

    public interface OnPromoImagesFoundListener {
        void onImagesFound(List<Bitmap> promoImages);
        void onError(String e);
    }
}

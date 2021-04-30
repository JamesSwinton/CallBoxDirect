
package com.zebra.jamesswinton.wfccallboxdirect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.zebra.jamesswinton.wfccallboxdirect.databinding.ActivityMainBinding;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GetPromoImagesAsync.OnPromoImagesFoundListener, View.OnTouchListener {

    // UI
    private ActivityMainBinding mDataBinding;

    // Permissions
    private PermissionsHelper mPermissionsHelper;

    // Promo SlideShow Variables
    private List<Bitmap> mPromoImages;
    private int mCurrentImageIndex = 0;
    private static final int TimeToDisplayImage = 5000;
    private static final Handler mSlideShowHandler = new Handler();

    // PTT Intents
    private static final String PTTPressed = "com.symbol.wfc.ptt_pressed";
    private static final String PTTReleased = "com.symbol.wfc.ptt_released";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Init Permissions
        mPermissionsHelper = new PermissionsHelper(this, () -> {
            // Load Images from File
            new GetPromoImagesAsync(MainActivity.this,
                    MainActivity.this).execute();

            // Set Click Listener
            mDataBinding.pttButton.setOnTouchListener(MainActivity.this);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionsHelper.onRequestPermissionsResult();
    }

    /**
     * Mic Button Touch Listener
     */

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            sendBroadcast(new Intent(PTTPressed));
            mDataBinding.pttButton.setImageResource(R.drawable.ic_mic);
            mDataBinding.statusHelperText.setText(getString(R.string.press_for_help));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            sendBroadcast(new Intent(PTTReleased));
            mDataBinding.pttButton.setImageResource(R.drawable.ic_mic_off);
            mDataBinding.statusHelperText.setText(getString(R.string.release_to_hear));
        }
        return true;
    }

    /**
     * Callback for loading images from File
     */

    @Override
    public void onImagesFound(List<Bitmap> promoImages) {
        this.mPromoImages = promoImages;
        startSlideShow();
    }

    @Override
    public void onError(String e) {
        Toast.makeText(this, e, Toast.LENGTH_LONG).show();
    }

    private void startSlideShow() {
        // Restart Slide Show
        if (mCurrentImageIndex == mPromoImages.size()) {
            mCurrentImageIndex = 0;
        }

        // Display Image
        mDataBinding.promotionSlideShow.setImageBitmap(mPromoImages.get(mCurrentImageIndex++));

        // Show Next Slide
        mSlideShowHandler.postDelayed(this::startSlideShow, TimeToDisplayImage);
    }
}
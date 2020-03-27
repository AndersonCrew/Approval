package com.crewcloud.apps.crewapproval.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.util.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by Dat on 7/26/2016.
 */
public class ImageViewActivity extends AppCompatActivity {
    /**
     * VIEW
     */
    private ImageView ivAvatar;
    public static final DisplayImageOptions optionsProfileAvatar = new DisplayImageOptions.Builder()
            .cacheOnDisk(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(false)
            .build();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        initView();
        receiveData();
    }

    private void initView() {
        ivAvatar = findViewById(R.id.iv_avatar);
    }

    private void receiveData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String url = bundle.getString(Constants.KEY_URL, "");
            if (!TextUtils.isEmpty(url) && ivAvatar != null) {
                ImageLoader.getInstance().displayImage(url, ivAvatar, optionsProfileAvatar);
            }
        }
    }
}

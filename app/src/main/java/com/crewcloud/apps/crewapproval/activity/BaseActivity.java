package com.crewcloud.apps.crewapproval.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.util.DialogUtil;
import com.crewcloud.apps.crewapproval.util.Util;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public abstract class BaseActivity extends AppCompatActivity {
    public ActionBar mActionBar;
    protected Context mContext;
    public static BaseActivity Instance;
    public ImageLoader mImageLoader = ImageLoader.getInstance();
    private Dialog mProgressDialog;
    protected String domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Instance = this;
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableHomeAction();

        domain = "http://google.com";
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .build();

        mImageLoader.init(config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Instance = this;
    }

    public void showProgressDialog() {
        if (null == mProgressDialog || !mProgressDialog.isShowing()) {
            mProgressDialog = new Dialog(mContext, R.style.ProgressCircleDialog);
            mProgressDialog.setTitle(getString(R.string.loading_content));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnCancelListener(null);
            mProgressDialog.addContentView(new ProgressBar(mContext), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void callActivity(Class cls) {
        Intent newIntent = new Intent(this, cls);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(newIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void startSingleActivity(Class cls) {
        Intent newIntent = new Intent(this, cls);
        newIntent.putExtra("count_id", 1);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    protected void enableHomeAction() {
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setDisplayShowCustomEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void showNetworkDialog() {
        if (Util.isWifiEnable()) {
            DialogUtil.customAlertDialog(this, getString(R.string.no_connection_error), getString(R.string.string_ok), null, new DialogUtil.OnAlertDialogViewClickEvent() {
                @Override
                public void onOkClick(DialogInterface alertDialog) {

                }

                @Override
                public void onCancelClick() {

                }
            });
        } else {
            DialogUtil.customAlertDialog(this, getString(R.string.no_wifi_error), getString(R.string.turn_wifi_on), getString(R.string.string_cancel), new DialogUtil.OnAlertDialogViewClickEvent() {
                @Override
                public void onOkClick(DialogInterface alertDialog) {
                    Intent wireLess = new Intent(
                            Settings.ACTION_WIFI_SETTINGS);
                    startActivity(wireLess);
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
    }
}
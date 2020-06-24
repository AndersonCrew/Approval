package com.crewcloud.apps.crewapproval.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crewcloud.apps.crewapproval.BuildConfig;
import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.dtos.ErrorDto;
import com.crewcloud.apps.crewapproval.dtos.Profile;
import com.crewcloud.apps.crewapproval.interfaces.BaseHTTPCallBack;
import com.crewcloud.apps.crewapproval.interfaces.GetUserCallBack;
import com.crewcloud.apps.crewapproval.util.DialogUtil;
import com.crewcloud.apps.crewapproval.util.HttpRequest;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;
import com.crewcloud.apps.crewapproval.util.Utils;
import com.crewcloud.apps.crewapproval.util.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

public class SettingActivity extends BaseActivity implements View.OnClickListener, GetUserCallBack {
    private ImageView imgAvatar;
    private LinearLayout lnProfile, lnGeneral, lnNotify, lnLogout, lnAbout;
    public PreferenceUtilities prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_page_layout);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.myColor_PrimaryDark));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lnProfile = findViewById(R.id.ln_profile);
        lnGeneral = findViewById(R.id.ln_general);
        lnNotify = findViewById(R.id.ln_notify);
        lnLogout = findViewById(R.id.ln_logout);
        lnAbout = findViewById(R.id.ln_about);
        lnProfile.setOnClickListener(this);
        lnGeneral.setOnClickListener(this);
        lnNotify.setOnClickListener(this);
        lnLogout.setOnClickListener(this);
        lnAbout.setOnClickListener(this);
        imgAvatar = findViewById(R.id.img_avatar);
        String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getUserAvatar();
        if (url.contains("/Images/Avatar.jpg"))
            HttpRequest.getInstance().GetUser(prefs.getCurrentUserNo(), this);
        Utils.showImage(url, imgAvatar);
    }

    @Override
    public void onClick(View v) {
        if (v == lnProfile) {
            BaseActivity.Instance.callActivity(ProfileUserActivity.class);
        } else if (v == lnGeneral) {
            Toast.makeText(getApplicationContext(), "undev", Toast.LENGTH_SHORT).show();
        } else if (v == lnNotify) {
            BaseActivity.Instance.callActivity(NotificationSettingActivity.class);
        } else if (v == lnLogout) {
            logoutAlert();
        } else if (v == lnAbout) {
            String title = getString(R.string.about) + " " + getString(R.string.app_name);
            String versionName = BuildConfig.VERSION_NAME;
            String user_version = getResources().getString(R.string.user_version) + " " + versionName;
            DialogUtil.oneButtonAlertDialog(this, title, user_version, getString(R.string.confirm));
        }
    }

    private void logoutAlert() {
        DialogUtil.customAlertDialog(this, getString(R.string.are_you_sure_loguot),
                getString(R.string.auto_login_button_yes), getString(R.string.auto_login_button_no), new DialogUtil.OnAlertDialogViewClickEvent() {
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        logout();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }

    public void logout() {
        HttpRequest.getInstance().deleteAndroidDevice(new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                new SettingActivity.WebClientAsync_Logout_v2().execute();
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                new SettingActivity.WebClientAsync_Logout_v2().execute();
            }
        });

    }

    @Override
    public void onGetUserSuccess(Profile profile) {
        prefs.setUserAvatar(profile.avatar);
        Utils.showImage(profile.avatar, imgAvatar);
    }

    @Override
    public void onError() {

    }

    private class WebClientAsync_Logout_v2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            final PreferenceUtilities preferenceUtilities = CrewCloudApplication.getInstance().getPreferenceUtilities();

            WebClient.Logout_v2(preferenceUtilities.getCurrentMobileSessionId(),
                    preferenceUtilities.getDomain(), new WebClient.OnWebClientListener() {
                        @Override
                        public void onSuccess(JsonNode jsonNode) {
                        }

                        @Override
                        public void onFailure() {
                        }
                    });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            PreferenceUtilities preferenceUtilities = CrewCloudApplication.getInstance().getPreferenceUtilities();
            preferenceUtilities.setCurrentMobileSessionId("");
            preferenceUtilities.setCurrentCompanyNo(0);
            preferenceUtilities.setCurrentUserID("");
            preferenceUtilities.setUserAvatar("");
            preferenceUtilities.clearNotificationSetting();
            CrewCloudApplication.getInstance().removeShortcut();
            BaseActivity.Instance.startSingleActivity(LoginActivity.class);
            finish();
        }
    }
}
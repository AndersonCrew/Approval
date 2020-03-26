package com.crewcloud.apps.crewapproval.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.crewcloud.apps.crewapproval.util.Util;
import com.crewcloud.apps.crewapproval.util.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingActivity extends BaseActivity implements View.OnClickListener, GetUserCallBack {
    private ImageView img_avatar;
    private LinearLayout ln_profile, ln_general, ln_notify, ln_logout, ln_about;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_back_ic);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ln_profile = (LinearLayout) findViewById(R.id.ln_profile);
        ln_general = (LinearLayout) findViewById(R.id.ln_general);
        ln_notify = (LinearLayout) findViewById(R.id.ln_notify);
        ln_logout = (LinearLayout) findViewById(R.id.ln_logout);
        ln_about = (LinearLayout) findViewById(R.id.ln_about);
        ln_profile.setOnClickListener(this);
        ln_general.setOnClickListener(this);
        ln_notify.setOnClickListener(this);
        ln_logout.setOnClickListener(this);
        ln_about.setOnClickListener(this);
//        PreferenceUtilities preferenceUtilities = CreCloudApplication.getInstance().getPreferenceUtilities();
//        String serviceDomain = prefUtils.getCurrentServiceDomain();
//        String avatar = prefUtils.getAvatar();
//        String newAvatar = avatar.replaceAll("\"", "");
//        String mUrl = serviceDomain + newAvatar;
//        ImageLoader imageLoader = ImageLoader.getInstance();
        img_avatar = (ImageView) findViewById(R.id.img_avatar);
//        imageLoader.displayImage(mUrl, img_avatar);
//        UserDto userDto = UserDBHelper.getUser();
        String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getUserAvatar();
        if (url.contains("/Images/Avatar.jpg"))
            HttpRequest.getInstance().GetUser(prefs.getCurrentUserNo(), this);
        Util.showImage(url, img_avatar);
    }

    @Override
    public void onClick(View v) {
        if (v == ln_profile) {
//            Intent intent = new Intent(SettingActivity.this, LogoutActivity.class);
//            startActivity(intent);
            BaseActivity.Instance.callActivity(ProfileUserActivity.class);
        } else if (v == ln_general) {
            Toast.makeText(getApplicationContext(), "undev", Toast.LENGTH_SHORT).show();
        } else if (v == ln_notify) {
//            Intent intent = new Intent(SettingActivity.this, NotificationSettingActivity.class);
//            startActivity(intent);
            BaseActivity.Instance.callActivity(NotificationSettingActivity.class);
        } else if (v == ln_logout) {
            logoutAlert();
        } else if (v == ln_about) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(getString(R.string.about) + getString(R.string.app_name));
            String title = getString(R.string.about) + " " + getString(R.string.app_name);
            String versionName = BuildConfig.VERSION_NAME;
            String user_version = getResources().getString(R.string.user_version) + " " + versionName;
//
////            String lastest_version = getResources().getString(R.string.lastest_version) + " " + prefs.getSERVER_VERSION();
////        String msg = user_version + "\n\n" + lastest_version;
//            builder.setMessage(user_version);
//
//            builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.cancel();
//                }
//            });
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//            Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
//            if (b != null) {
//                b.setTextColor(ContextCompat.getColor(this, R.color.light_black));
//            }
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
        Util.showImage(profile.avatar, img_avatar);
//        ImageLoader.getInstance().displayImage(profile.avatar, img_avatar);
    }

    @Override
    public void onError() {

    }

    private class WebClientAsync_Logout_v2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            final PreferenceUtilities preferenceUtilities = CrewCloudApplication.getInstance().getPreferenceUtilities();

            WebClient.Logout_v2(preferenceUtilities.getCurrentMobileSessionId(),
                    "http://" + preferenceUtilities.getCurrentCompanyDomain(), new WebClient.OnWebClientListener() {
                        @Override
                        public void onSuccess(JsonNode jsonNode) {
//                            preferenceUtilities.setCurrentMobileSessionId("");
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
            preferenceUtilities.setCurrentServiceDomain("");
            preferenceUtilities.setCurrentCompanyDomain("");
            preferenceUtilities.setCurrentUserID("");
            preferenceUtilities.setUserAvatar("");
            preferenceUtilities.clearNotificationSetting();
            CrewCloudApplication.getInstance().removeShortcut();
            BaseActivity.Instance.startSingleActivity(LoginActivity.class);
            finish();
        }
    }
}
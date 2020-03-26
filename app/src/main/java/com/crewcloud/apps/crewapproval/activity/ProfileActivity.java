package com.crewcloud.apps.crewapproval.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.dtos.Profile;
import com.crewcloud.apps.crewapproval.interfaces.GetUserCallBack;
import com.crewcloud.apps.crewapproval.util.HttpRequest;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;
import com.crewcloud.apps.crewapproval.util.Util;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements GetUserCallBack {
    private String TAG = "ProfileActivity";
    private ImageView img_bg;
    private TextView tv_name, tv_personal, tv_email, tv_company, tv_phone;
    private JSONObject object;
    private String CellPhone = "";
    private String MailAddress = "";
    private ImageView avatar_imv;
    private ImageView image_profile;
    public PreferenceUtilities prefs;
    private RelativeLayout lay_image_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_myinfor);

        prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_back_ic);
        toolbar.setTitle(getString(R.string.profle));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        image_profile = (ImageView) findViewById(R.id.image_profile);
        lay_image_profile = (RelativeLayout) findViewById(R.id.lay_image_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_personal = (TextView) findViewById(R.id.tv_personal);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        avatar_imv = (ImageView) findViewById(R.id.avatar_imv);

//        PreferenceUtilities pref = CrewCloudApplication.getInstance().getPreferenceUtilities();

        tv_name.setText(prefs.getFullName());
        tv_personal.setText(prefs.getCurrentUserID());
        tv_company.setText(prefs.getCompanyName());
        tv_email.setText(prefs.getEmail());

        Util.showImage(prefs.getUserAvatar(), avatar_imv);
        Util.showImage(prefs.getUserAvatar(), image_profile);

//        ImageUtils.showImage(userDto, img_bg);
//        try {
//            object = new JSONObject(MainActivity.myInfor);
//            CellPhone = object.optString("CellPhone");
////            MailAddress = object.optString("MailAddress");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        tv_email.setText(prefs.getEmail());
//        tv_phone.setText("" + CellPhone);
        avatar_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_image_profile.setVisibility(View.VISIBLE);
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_image_profile.setVisibility(View.GONE);
            }
        });
        if (prefs.getUserAvatar().contains("/Images/Avatar.jpg"))
            HttpRequest.getInstance().GetUser(prefs.getCurrentUserNo(), this);
    }

    @Override
    public void onBackPressed() {
        if (lay_image_profile.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            lay_image_profile.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetUserSuccess(Profile profile) {
        prefs.setUserAvatar(profile.avatar);
        Util.showImage(profile.avatar, avatar_imv);
        Util.showImage(profile.avatar, image_profile);
    }

    @Override
    public void onError() {

    }
}
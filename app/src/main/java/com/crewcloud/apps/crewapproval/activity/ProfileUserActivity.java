package com.crewcloud.apps.crewapproval.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.dtos.BelongDepartmentDTO;
import com.crewcloud.apps.crewapproval.dtos.Profile;
import com.crewcloud.apps.crewapproval.interfaces.GetUserCallBack;
import com.crewcloud.apps.crewapproval.util.HttpRequest;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;
import com.crewcloud.apps.crewapproval.util.Statics;
import com.crewcloud.apps.crewapproval.util.TimeUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ProfileUserActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvName, tvPersonal, tvEmail, tvCompany, tvCompanyPhone, tvExtensionNumber,
            tvCellPhone, tvPosition, tvPassword, tvDate, tvBirthday;
    private LinearLayout layoutExtensionNumber, layoutCellPhone, layoutCompanyPhone;
    private String cellPhone = "";
    private String emailAddress = "";
    private ImageView imgAvatar;
    public PreferenceUtilities prefs;
    private RelativeLayout rlProfile;
    private static final int REQUEST_CALL = 1;
    private TextView btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_back_ic);
        toolbar.setTitle(getString(R.string.profle));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();
        rlProfile = findViewById(R.id.lay_image_profile);
        tvName = findViewById(R.id.tv_name);
        tvPersonal = findViewById(R.id.tv_personal);
        tvEmail = findViewById(R.id.tv_email);
        tvCompany = findViewById(R.id.tv_company);
        tvCompanyPhone = findViewById(R.id.tv_company_phone);
        tvPosition = findViewById(R.id.tv_position);
        tvPassword = findViewById(R.id.tv_password);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvDate = findViewById(R.id.tv_join);

        tvExtensionNumber = findViewById(R.id.tvExtensionNumber);
        tvCellPhone = findViewById(R.id.tvCellPhone);


        layoutExtensionNumber = findViewById(R.id.layoutExtensionNumber);
        layoutCellPhone = findViewById(R.id.layoutCellPhone);
        layoutCompanyPhone = findViewById(R.id.layoutCompanyNumber);

        imgAvatar = findViewById(R.id.avatar_imv);

        btnChangePassword = findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(this);
        findViewById(R.id.img_call).setOnClickListener(this);
        findViewById(R.id.img_email).setOnClickListener(this);
        findViewById(R.id.img_message).setOnClickListener(this);
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpRequest.getInstance().GetUser(prefs.getCurrentUserNo(), new GetUserCallBack() {
            @Override
            public void onGetUserSuccess(Profile profile) {
                fillData(profile);
            }

            @Override
            public void onError() {

            }
        });
    }

    private void fillData(Profile profile) {
        PreferenceUtilities prefs = new PreferenceUtilities();
        emailAddress = profile.mailAddress;
        final String url = prefs.getCurrentServiceDomain() + profile.getAvatar();
        ImageLoader.getInstance().displayImage(url, imgAvatar);

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUserActivity.this, ImageViewActivity.class);
                intent.putExtra(Statics.KEY_URL, url);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tvName.setText(profile.getName());
        tvPersonal.setText(profile.getUserId());
        tvName.setText(profile.getName());
        tvEmail.setText(profile.getMailAddress());
        tvPosition.setText(getPositionName(profile));
        tvPassword.setText(prefs.getPass());

        String company = prefs.getStringValue(Statics.PREFS_KEY_COMPANY_NAME, "");
        tvCompany.setText(company);
        cellPhone = !TextUtils.isEmpty(profile.getCellPhone().trim()) ?
                profile.getCellPhone() :
                !TextUtils.isEmpty(profile.getCompanyPhone().trim()) ?
                        profile.getCompanyPhone() :
                        "";

        String companyPhone = "";
        String extensionNumber = "";
        try {
            companyPhone = profile.getCompanyPhone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            extensionNumber = profile.getExtensionNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvCellPhone.setText(cellPhone);
        tvExtensionNumber.setText(extensionNumber);
        tvCompanyPhone.setText(companyPhone);
        if (cellPhone.length() > 0) layoutCellPhone.setVisibility(View.VISIBLE);
        else layoutCellPhone.setVisibility(View.GONE);
        if (companyPhone.length() > 0) layoutCompanyPhone.setVisibility(View.VISIBLE);
        else layoutCompanyPhone.setVisibility(View.GONE);
        if (extensionNumber.length() > 0) layoutExtensionNumber.setVisibility(View.VISIBLE);
        else layoutExtensionNumber.setVisibility(View.GONE);

        String birthDay = TimeUtils.displayTimeWithoutOffsetV2(profile.getBirthDate());
        String joinDate = TimeUtils.displayTimeWithoutOffsetV2(profile.getEntranceDate());

        tvDate.setText(joinDate);
        tvBirthday.setText(birthDay);
    }

    @Override
    public void onBackPressed() {
        if (rlProfile.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            rlProfile.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_password:
                callActivity(ChangePasswordActivity.class);
                break;
            case R.id.img_call:
                onClickCall(cellPhone);
                break;
            case R.id.img_email:
                onSendEmail(emailAddress);
                break;
            case R.id.img_message:
                onClickMessage(cellPhone);
                break;
        }
    }

    Intent callIntent;
    public void onClickCall(String phoneNumber) {
        callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    public void onClickMessage(String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null)));
    }

    private void onSendEmail(String emailAddress){
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", emailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private String getPositionName(Profile profile){
        String result = "";

        if(profile.getBelongs()!= null && profile.getBelongs().size() > 0){
            BelongDepartmentDTO belongDefault =  null;
            for (BelongDepartmentDTO belong : profile.getBelongs()) {
                if(belong.isDefault()){
                    belongDefault = belong;
                    result = belong.getDepartName() + "/" + ((belong.getPositionName() == null || belong.getPositionName().isEmpty())? belong.getDutyName() : belong.getPositionName());
                    break;
                }
            }
            if(belongDefault == null){
                BelongDepartmentDTO belong = profile.getBelongs().get(0);
                result = belong.getDepartName() + "/" + ((belong.getPositionName() == null || belong.getPositionName().isEmpty())? belong.getDutyName() : belong.getPositionName());
            }
        }

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, "please try again", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
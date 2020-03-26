package com.crewcloud.apps.crewapproval.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

import org.json.JSONObject;


public class ProfileUserActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = "ProfileActivity";
    private ImageView img_bg;
    private TextView tv_name, tv_personal, tv_email, tv_company, tv_company_phone, tvExtensionNumber,
            tvCellPhone, tv_position, tv_password, tv_date, tv_birthday;
    private LinearLayout layoutExtensionNumber, layoutCellPhone, layoutCompanyPhone, layout_date, layout_birthday;
    private JSONObject object;
    private String cellPhone = "";
    private String emailAddress = "";
    private ImageView avatar_imv;
    private ImageView image_profile;
    public PreferenceUtilities prefs;
    private RelativeLayout lay_image_profile;
    private static final int REQUEST_CALL = 1;
    /**
     * VIEW
     */
    private TextView btnChangePassword;
    private ImageView btnChangePhoto;

    /**
     * PARAM
     */
    private Uri uriCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

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

        prefs = CrewCloudApplication.getInstance().getPreferenceUtilities();
        image_profile = (ImageView) findViewById(R.id.image_profile);
        lay_image_profile = (RelativeLayout) findViewById(R.id.lay_image_profile);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_personal = (TextView) findViewById(R.id.tv_personal);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_company = (TextView) findViewById(R.id.tv_company);
        tv_company_phone = (TextView) findViewById(R.id.tv_company_phone);
        tv_position = findViewById(R.id.tv_position);
        tv_password = findViewById(R.id.tv_password);
        tv_birthday = findViewById(R.id.tv_birthday);
        tv_date = findViewById(R.id.tv_join);

        tvExtensionNumber = (TextView) findViewById(R.id.tvExtensionNumber);
        tvCellPhone = (TextView) findViewById(R.id.tvCellPhone);


        layoutExtensionNumber = (LinearLayout) findViewById(R.id.layoutExtensionNumber);
        layoutCellPhone = (LinearLayout) findViewById(R.id.layoutCellPhone);
        layoutCompanyPhone = (LinearLayout) findViewById(R.id.layoutCompanyNumber);

        avatar_imv = (ImageView) findViewById(R.id.avatar_imv);

        btnChangePassword = (TextView) findViewById(R.id.btn_change_password);
        btnChangePassword.setOnClickListener(this);

//        btnChangePhoto = (ImageView) findViewById(R.id.btn_change_photo);
//        btnChangePhoto.setOnClickListener(this);
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
//        Log.d(TAG,new Gson().toJson(profile));
        PreferenceUtilities prefs = new PreferenceUtilities();

        emailAddress = profile.mailAddress;
        final String url = prefs.getCurrentServiceDomain() + profile.getAvatar();
        ImageLoader.getInstance().displayImage(url, avatar_imv);

        avatar_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileUserActivity.this, ImageViewActivity.class);
                intent.putExtra(Statics.KEY_URL, url);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tv_name.setText(profile.getName());
        tv_personal.setText(profile.getUserId());
        tv_name.setText(profile.getName());
        tv_email.setText(profile.getMailAddress());
        tv_position.setText(getPositionName(profile));
        tv_password.setText(prefs.getPass());
        //tvSex.setText(profile.getSex() == 0 ? "Female" : "Male");


        String company = prefs.getStringValue(Statics.PREFS_KEY_COMPANY_NAME, "");
        /*
        ArrayList<BelongDepartmentDTO> belongs = profile.getBelongs();
        if (belongs != null) {
            for (int i = 0; i < belongs.size(); i++) {
                if (i == 0) {
                    company += belongs.get(i).getDepartName();
                } else {
                    company += "," + belongs.get(i).getDepartName();
                }
            }
        }*/

        tv_company.setText(company);

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

        Log.d(">>>",">>>" + cellPhone);
        tvCellPhone.setText(cellPhone);
        tvExtensionNumber.setText(extensionNumber);
        tv_company_phone.setText(companyPhone);
        if (cellPhone.length() > 0) layoutCellPhone.setVisibility(View.VISIBLE);
        else layoutCellPhone.setVisibility(View.GONE);
        if (companyPhone.length() > 0) layoutCompanyPhone.setVisibility(View.VISIBLE);
        else layoutCompanyPhone.setVisibility(View.GONE);
        if (extensionNumber.length() > 0) layoutExtensionNumber.setVisibility(View.VISIBLE);
        else layoutExtensionNumber.setVisibility(View.GONE);


        //tvPhoneNumber.setText(profile.getCellPhone());
        //tvCompanyNumber.setText(profile.getCompanyPhone());
        //tvExtensionNumber.setText(profile.getExtensionNumber());
        //tvEntranceDate.setText(profile.displayTimeWithoutOffset(profileUserDTO.getEntranceDate()));
//        tvBirthday.setText(TimeUtils.displayTimeWithoutOffset(profileUserDTO.getBirthDate()));
        //tvBelongToDepartment.setText(Html.fromHtml(belongToDepartment));
        String birthDay = TimeUtils.displayTimeWithoutOffsetV2(profile.getBirthDate());
        String joinDate = TimeUtils.displayTimeWithoutOffsetV2(profile.getEntranceDate());

        tv_date.setText(joinDate);
        tv_birthday.setText(birthDay);
    }

    @Override
    public void onBackPressed() {
        if (lay_image_profile.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            lay_image_profile.setVisibility(View.GONE);
        }
    }

    private void showDialogListPhoto() {
        /** DISPLAY LIST CHOICE */
//        final ArrayList<String> itemList = new ArrayList<>();
//        itemList.add(Util.getString(R.string.dialog_list_choice_photo_camera));
//        itemList.add(Util.getString(R.string.dialog_list_choice_photo_attachment));
//
//        DialogUtil.displayDialogWithListChoice(this, itemList, new OnDialogWithListChoiceCallBack() {
//            @Override
//            public void onClickOK(final int position) {
//                String choice = itemList.get(position);
//                if (choice.equals(Util.getString(R.string.dialog_list_choice_photo_camera))) {
//                    choiceCamera();
//                } else if (choice.equals(Util.getString(R.string.dialog_list_choice_photo_attachment))) {
//                    choiceAttachment();
//                }
//            }
//        });
    }

    private void choiceCamera() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        uriCamera = getOutputMediaFileUri(Constant.MEDIA_TYPE_IMAGE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCamera);
//        startActivityForResult(intent, Constant.REQUEST_CODE_CAPTURE_IMAGE);
    }

    private Uri getOutputMediaFileUri(int type) {
//        return Uri.fromFile(Util.getOutputMediaFile(type));
        return null;
    }

    private void choiceAttachment() {
//        Intent i = new Intent(this, FileChooser.class);
//        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
//        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
//        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
//        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
//        startActivityForResult(i, Constant.REQUEST_CODE_SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String realPath = "";
            switch (requestCode) {
//                case Constant.REQUEST_CODE_CAPTURE_IMAGE:
//                    realPath = uriCamera.getPath();
//                    Util.showShortMessage(realPath);
//                    break;
//                case Constant.REQUEST_CODE_SELECT_FILE:
//                    realPath = data.getData().getPath();
//                    Util.showShortMessage(realPath);
//                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_password:
                callActivity(ChangePasswordActivity.class);
                break;
//            case R.id.btn_change_photo:
//                showDialogListPhoto();
//                break;
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


//        if (ContextCompat.checkSelfPermission(ProfileUserActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(ProfileUserActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
//        } else {
//            startActivity(callIntent);
//        }
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
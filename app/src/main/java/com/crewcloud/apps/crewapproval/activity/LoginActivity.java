package com.crewcloud.apps.crewapproval.activity;

import android.content.*;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.dtos.ErrorDto;
import com.crewcloud.apps.crewapproval.interfaces.BaseHTTPCallBack;
import com.crewcloud.apps.crewapproval.interfaces.ICheckSSL;
import com.crewcloud.apps.crewapproval.interfaces.OnAutoLoginCallBack;
import com.crewcloud.apps.crewapproval.interfaces.OnHasAppCallBack;
import com.crewcloud.apps.crewapproval.util.DialogUtil;
import com.crewcloud.apps.crewapproval.util.HttpRequest;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;
import com.crewcloud.apps.crewapproval.util.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends BaseActivity implements BaseHTTPCallBack, OnHasAppCallBack {
    private EditText etDomain, etUsername, etPassword;
    private TextView rlLogin;
    public PreferenceUtilities mPrefs;
    private boolean mFirstLogin = true;
    private boolean mFirstStart = false;
    private boolean isAutoLoginShow = false;
    private String mRegId;
    private LinearLayout llMain;
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPrefs = CrewCloudApplication.getInstance().getPreferenceUtilities();
        initViews();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_ACTION);
        registerReceiver(accountReceiver, intentFilter);
    }

    private void initViews() {
        rlLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.login_edt_username);
        etPassword = findViewById(R.id.login_edt_password);
        etDomain = findViewById(R.id.login_edt_server);
        llMain = findViewById(R.id.llMain);
        imgLogo = findViewById(R.id.img_login_logo);

        llMain.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                llMain.getWindowVisibleDisplayFrame(r);
                int screenHeight = llMain.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    imgLogo.setVisibility(View.GONE);
                } else {
                    imgLogo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(accountReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPrefs.getIntroCount() < 1) {
            mFirstStart = true;
            mPrefs.putaeSortType(2);
        }
        firstChecking();

        if (mRegId == null || mRegId.isEmpty())
            registerInBackground();
    }

    private void firstChecking() {
        if (mFirstLogin) {
            if (Utils.isNetworkAvailable()) {
                if (mFirstStart) {
                    doLogin();
                    mFirstStart = false;
                } else {
                    doLogin();
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    private void doLogin() {
        if (Utils.checkStringValue(mPrefs.getCurrentMobileSessionId())) {
            HttpRequest.getInstance().checkLogin(this);
        } else {
            mFirstLogin = false;
            init();
        }
    }

    @Override
    public void showNetworkDialog() {
        if (Utils.isWifiEnable()) {
            DialogUtil.customAlertDialog(this, getString(R.string.no_connection_error), getString(R.string.string_ok), null, new DialogUtil.OnAlertDialogViewClickEvent() {
                @Override
                public void onOkClick(DialogInterface alertDialog) {
                    finish();
                }

                @Override
                public void onCancelClick() {

                }
            });
        } else {
            DialogUtil.customAlertDialog(this, getString(R.string.no_wifi_error), getString(R.string.turn_wifi_on), getString(R.string.string_cancel), new DialogUtil.OnAlertDialogViewClickEvent() {
                @Override
                public void onOkClick(DialogInterface alertDialog) {
                    Intent wireLess = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(wireLess);
                    finish();
                }

                @Override
                public void onCancelClick() {
                    finish();
                }
            });
        }
    }

    public static String BROADCAST_ACTION = "com.dazone.crewcloud.account.receive";

    BroadcastReceiver accountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receiverPackageName = intent.getExtras().getString("receiverPackageName");
            if (LoginActivity.this.getPackageName().equals(receiverPackageName)) {
                String companyID = intent.getExtras().getString("companyID");
                String userID = intent.getExtras().getString("userID");
                if (!TextUtils.isEmpty(companyID) && !TextUtils.isEmpty(userID) && !isAutoLoginShow) {
                    isAutoLoginShow = true;
                    showPopupAutoLogin(companyID, userID);
                }
            }
        }
    };

    private void showPopupAutoLogin(final String companyID, final String userID) {
        String alert1 = Utils.getString(R.string.auto_login_company_ID) + companyID;
        String alert2 = Utils.getString(R.string.auto_login_user_ID) + userID;
        String alert3 = Utils.getString(R.string.auto_login_text);
        String msg = alert1 + "\n" + alert2 + "\n\n" + alert3;

        DialogUtil.customAlertDialog(this, getString(R.string.auto_login_title), msg,
                getString(R.string.auto_login_button_yes), getString(R.string.auto_login_button_no),
                new DialogUtil.OnAlertDialogViewClickEvent() {
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        autoLogin(companyID, userID);
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
    }

    public void autoLogin(String companyID, final String userID) {
        Utils.setServerSite(companyID);
        showProgressDialog();
        HttpRequest.getInstance().AutoLogin(userID, new OnAutoLoginCallBack() {
            @Override
            public void OnAutoLoginSuccess(String response) {
                CrewCloudApplication.getInstance().getPreferenceUtilities().setCurrentUserID(userID);
                loginSuccess();
            }

            @Override
            public void OnAutoLoginFail(ErrorDto dto) {
                if (mFirstLogin) {
                    dismissProgressDialog();

                    mFirstLogin = false;
                    init();
                } else {
                    dismissProgressDialog();
                    String error_msg = dto.message;

                    if (TextUtils.isEmpty(error_msg)) {
                        error_msg = getString(R.string.connection_falsed);
                    }

                    showSaveDialog(error_msg);
                }
            }
        });
    }

    private void init() {
        /** SEND BROADCAST */
        Intent intent = new Intent();
        intent.setAction("com.dazone.crewcloud.account.get");
        intent.putExtra("senderPackageName", this.getPackageName());
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);

        etDomain.setText(new PreferenceUtilities().getCompanyName());
        etPassword.setText(new PreferenceUtilities().getPass());
        etUsername.setText(new PreferenceUtilities().getName());

        etUsername.setPrivateImeOptions("defaultInputmode=english;");
        etDomain.setPrivateImeOptions("defaultInputmode=english;");

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result)) {
                    etUsername.setText(result);
                    etUsername.setSelection(result.length());
                }
            }
        });

        etDomain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(" ", "");

                if (!s.toString().equals(result)) {
                    etDomain.setText(result);
                    etDomain.setSelection(result.length());
                }
            }
        });

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    rlLogin.callOnClick();
                }
                return false;
            }
        });


        rlLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerInBackground();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String domain = etDomain.getText().toString();

                Utils.setServerSite(domain);
                if (TextUtils.isEmpty(checkStringValue(domain, username, password))) {

                    showProgressDialog();
                    HttpRequest.getInstance().checkSSL(new ICheckSSL() {
                        @Override
                        public void hasSSL(boolean hasSSL) {
                            Utils.setServerSite(domain);
                            HttpRequest.getInstance().login(LoginActivity.this, username, password);
                        }

                        @Override
                        public void checkSSLError(ErrorDto errorData) {
                            dismissProgressDialog();
                            Toast.makeText(LoginActivity.this, "Cannot check ssl this domain!", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    DialogUtil.customAlertDialog(LoginActivity.this, checkStringValue(domain, username, password), getString(R.string.string_ok), null, new DialogUtil.OnAlertDialogViewClickEvent() {
                        @Override
                        public void onOkClick(DialogInterface alertDialog) {
                        }

                        @Override
                        public void onCancelClick() {
                        }
                    });
                }
            }
        });
    }

    private String checkStringValue(String server_site, String username, String password) {
        String result = "";

        if (TextUtils.isEmpty(server_site)) {
            result += getString(R.string.string_server_site);
        }

        if (TextUtils.isEmpty(username)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.login_username);
            } else {
                result += ", " + getString(R.string.login_username);
            }
        }

        if (TextUtils.isEmpty(password)) {
            if (TextUtils.isEmpty(result)) {
                result += getString(R.string.login_password);
            } else {
                result += ", " + getString(R.string.login_password);
            }
        }

        if (TextUtils.isEmpty(result)) {
            return result;
        } else {
            return result + " " + getString(R.string.login_empty_input);
        }
    }

    @Override
    public void onHTTPSuccess() {
        loginSuccess();
    }

    private void loginSuccess() {
        if (mRegId == null || mRegId.isEmpty()) {
            callActivity(MainActivity.class);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            insertAndroidDevice();
        }
        dismissProgressDialog();
    }

    public void insertAndroidDevice() {
        if (mRegId == null || mRegId.isEmpty()) {
            Toast.makeText(this, R.string.string_cant_get_token, Toast.LENGTH_LONG).show();
            return;
        }


        String start_time = mPrefs.getSTART_TIME();
        String end_time = mPrefs.getEND_TIME();

        if (start_time.length() == 0) {
            start_time = "AM 08:00";
            mPrefs.setSTART_TIME(start_time);
        }

        if (end_time.length() == 0) {
            end_time = "PM 06:00";
            mPrefs.setEND_TIME(end_time);
        }

        String notificationOptions = "{" +
                "\"enabled\": " + mPrefs.getNOTIFI_MAIL() + "," +
                "\"sound\": " + mPrefs.getNOTIFI_SOUND() + "," +
                "\"vibrate\": " + mPrefs.getNOTIFI_VIBRATE() + "," +
                "\"notitime\": " + mPrefs.getNOTIFI_TIME() + "," +
                "\"starttime\": \"" + Utils.getFullHour(start_time) + "\"," +
                "\"endtime\": \"" + Utils.getFullHour(end_time) + "\"" + "}";

        notificationOptions = notificationOptions.trim();
        HttpRequest.getInstance().insertAndroidDevice(new BaseHTTPCallBack() {
            @Override
            public void onHTTPSuccess() {
                callActivity(MainActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }

            @Override
            public void onHTTPFail(ErrorDto errorDto) {
                Toast.makeText(LoginActivity.this, errorDto.message, Toast.LENGTH_LONG).show();
                Log.d(">>> insert token fail", errorDto.message);
            }
        }, mRegId, notificationOptions);
    }

    private void registerInBackground() {
        new register().execute("");
    }

    public class register extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                if (mRegId == null || mRegId.isEmpty())
                    mRegId = FirebaseInstanceId.getInstance().getToken();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            mPrefs.setGCMregistrationid(mRegId);
        }
    }

    @Override
    public void onHTTPFail(ErrorDto errorDto) {
        if (mFirstLogin) {
            dismissProgressDialog();
            mFirstLogin = false;
            init();
        } else {
            dismissProgressDialog();
            String error_msg = errorDto.message;

            if (TextUtils.isEmpty(error_msg)) {
                error_msg = getString(R.string.connection_falsed);
            }

            showSaveDialog(error_msg);
        }
    }

    @Override
    public void hasApp() {
        loginSuccess();
    }

    @Override
    public void noHas(ErrorDto errorDto) {
        if (mFirstLogin) {
            mFirstLogin = false;
            init();
        } else {
            dismissProgressDialog();
            showSaveDialog(errorDto.message);
        }
    }

    private void showSaveDialog(String message) {
        DialogUtil.customAlertDialog(this, message, getString(R.string.string_ok), null, new DialogUtil.OnAlertDialogViewClickEvent() {
            @Override
            public void onOkClick(DialogInterface alertDialog) {

            }

            @Override
            public void onCancelClick() {

            }
        });
    }
}
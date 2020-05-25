package com.crewcloud.apps.crewapproval.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.webkit.*;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crewcloud.apps.crewapproval.BuildConfig;
import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.crewcloud.apps.crewapproval.base.WebContentChromeClient;
import com.crewcloud.apps.crewapproval.base.WebContentClient;
import com.crewcloud.apps.crewapproval.dtos.ErrorDto;
import com.crewcloud.apps.crewapproval.interfaces.OnHasUpdateAppCallBack;
import com.crewcloud.apps.crewapproval.util.DialogUtil;
import com.crewcloud.apps.crewapproval.util.HttpRequest;
import com.crewcloud.apps.crewapproval.util.PermissionUtil;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;
import com.crewcloud.apps.crewapproval.util.Utils;
import com.crewcloud.apps.crewapproval.util.WebClient;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements OnHasUpdateAppCallBack {
    private String updateUrl = "";
    private WebView wvContent = null;
    private ProgressBar mProgressBar;
    private final int UPDATE_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        wvContent = findViewById(R.id.wvContent);
        mProgressBar = findViewById(R.id.pbProgress);
        initWebContent();
        CrewCloudApplication.getInstance().setMainActivityInstance(this);

        HttpRequest.getInstance().getBadgeCount();
        HttpRequest.getInstance().checkApplicationUpdate(this);
    }

    @Override
    protected void onDestroy() {
        CrewCloudApplication.getInstance().setMainActivityInstance(null);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PreferenceUtilities preferenceUtilities = CrewCloudApplication.getInstance().getPreferenceUtilities();
        int timezone = preferenceUtilities.getTIME_ZONE();
        int Cur = Utils.getTimeZoneOffset();
        if (timezone != Cur) {
            preferenceUtilities.setTIME_ZONE(Cur);
            HttpRequest.getInstance().updateTimeZone(preferenceUtilities.getGCMregistrationid());
        }

        if (CrewCloudApplication.getInstance().isHasUpdate()) {
            refreshMainURL();
        }
        CrewCloudApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CrewCloudApplication.activityPaused();
    }

    private void initWebContent() {
        WebSettings webSettings = wvContent.getSettings();

        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);

        WebContentChromeClient client = new WebContentChromeClient();

        wvContent.setWebChromeClient(new WebContentChromeClient());
        wvContent.setWebViewClient(new WebContentClient(this, mProgressBar));

        wvContent.setVerticalScrollBarEnabled(true);
        wvContent.setHorizontalScrollBarEnabled(true);

        wvContent.addJavascriptInterface(new JavaScriptExtension(), "crewcloud");

        mFileDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        wvContent.setDownloadListener(mDownloadListener);

        PreferenceUtilities preferenceUtilities = CrewCloudApplication.getInstance().getPreferenceUtilities();

        String domain = preferenceUtilities.getCurrentCompanyDomain();

        CookieManager.getInstance().setCookie("http://" + domain, "skey0=" + preferenceUtilities.getCurrentMobileSessionId());
        CookieManager.getInstance().setCookie("http://" + domain, "skey1=" + "123123123123132");
        CookieManager.getInstance().setCookie("http://" + domain, "skey2=" + Utils.getLanguageCode());
        CookieManager.getInstance().setCookie("http://" + domain, "skey3=" + preferenceUtilities.getCurrentCompanyNo());

        wvContent.loadUrl("http://" + domain + "/UI/_EAPPMobile/Main.aspx");

    }

    @Override
    public void hasApp(String url) {
        updateUrl = url;
        DialogUtil.customAlertDialog(this, getString(R.string.string_update_content), getString(R.string.auto_login_button_yes), getString(R.string.auto_login_button_no),
                new DialogUtil.OnAlertDialogViewClickEvent() {
                    @Override
                    public void onOkClick(DialogInterface alertDialog) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (checkPermissions()) {
                                new MainActivity.Async_DownloadApkFile(MainActivity.this, getString(R.string.app_name)).execute();
                            } else {
                                setPermissions(UPDATE_PERMISSIONS_REQUEST_CODE);
                            }
                        } else {
                            new MainActivity.Async_DownloadApkFile(MainActivity.this, getString(R.string.app_name)).execute();
                        }
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
    }

    @Override
    public void noHas(ErrorDto dto) {

    }


    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED));

    }

    private void setPermissions(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case UPDATE_PERMISSIONS_REQUEST_CODE: {
                boolean isGranted = true;
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        break;
                    }
                }

                if (isGranted) {
                    new MainActivity.Async_DownloadApkFile(MainActivity.this, getString(R.string.app_name)).execute();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private final class JavaScriptExtension {
        @JavascriptInterface
        public void openSetting() {
            BaseActivity.Instance.callActivity(SettingActivity.class);
        }
    }

    public void refreshMainURL() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (wvContent.getUrl().contains("/Main.aspx") || wvContent.getUrl().contains("/list.aspx")) {
                    wvContent.reload();
                    CrewCloudApplication.getInstance().setHasUpdate(false);
                }
            }
        });

    }

    public void logout() {
        new WebClientAsync_Logout_v2().execute();
    }

    private class WebClientAsync_Logout_v2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            PreferenceUtilities preferenceUtilities = CrewCloudApplication.getInstance().getPreferenceUtilities();

            WebClient.Logout_v2(preferenceUtilities.getCurrentMobileSessionId(),
                    "http://" + preferenceUtilities.getCurrentCompanyDomain(), new WebClient.OnWebClientListener() {
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
            preferenceUtilities.setCurrentServiceDomain("");
            preferenceUtilities.setCurrentCompanyDomain("");
            preferenceUtilities.setCurrentUserID("");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }

    private boolean mIsBackPressed = false;

    private static class ActivityHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public ActivityHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null) {
                activity.setBackPressed(false);
            }
        }
    }

    private final ActivityHandler mActivityHandler = new ActivityHandler(this);

    public void setBackPressed(boolean isBackPressed) {
        mIsBackPressed = isBackPressed;
    }

    @Override
    public void onBackPressed() {
        if (wvContent.canGoBack()) {
            wvContent.goBack();
        } else {
            if (!mIsBackPressed) {
                Toast.makeText(this, R.string.mainActivity_message_exit, Toast.LENGTH_SHORT).show();
                mIsBackPressed = true;
                mActivityHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                finish();
            }
        }
    }

    private DownloadManager mFileDownloadManager = null;
    private final Pattern CONTENT_DISPOSITION_PATTERN = Pattern.compile("(filename\\*?=.*;)|(filename\\*?=.*\\n)|(filename\\*?=.*)");

    private DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionUtil.INSTANCE.checkPermissions(MainActivity.this, PermissionUtil.INSTANCE.getWriteExternal())) {
                    downloadDialog(url, userAgent, contentDisposition, mimeType);
                } else {
                    PermissionUtil.INSTANCE.requestPermissions(MainActivity.this, 1, PermissionUtil.INSTANCE.getWriteExternal());
                }
            } else {
                downloadDialog(url, userAgent, contentDisposition, mimeType);
            }
        }
    };

    private class Async_DownloadApkFile extends AsyncTask<Void, Void, Void> {
        private String mApkFileName;
        private final WeakReference<MainActivity> mWeakActivity;
        private ProgressDialog mProgressDialog = null;

        public Async_DownloadApkFile(MainActivity activity, String apkFileName) {
            mWeakActivity = new WeakReference<>(activity);
            mApkFileName = apkFileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = mWeakActivity.get();

            if (activity != null) {
                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setMessage(getString(R.string.mailActivity_message_download_apk));
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                URL apkUrl = new URL(updateUrl);
                urlConnection = (HttpURLConnection) apkUrl.openConnection();
                inputStream = urlConnection.getInputStream();
                bufferedInputStream = new BufferedInputStream(inputStream);

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + mApkFileName + "_new.apk";
                fileOutputStream = new FileOutputStream(filePath);

                byte[] buffer = new byte[4096];
                int readCount;

                while (true) {
                    readCount = bufferedInputStream.read(buffer);
                    if (readCount == -1) {
                        break;
                    }

                    fileOutputStream.write(buffer, 0, readCount);
                    fileOutputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bufferedInputStream != null) {
                    try {
                        bufferedInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (urlConnection != null) {
                    try {
                        urlConnection.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            MainActivity activity = mWeakActivity.get();

            if (activity != null) {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + mApkFileName + "_new.apk";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                    activity.startActivity(intent);
                }
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    private String parseContentDisposition(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            String fileName = "";

            while (m.find()) {
                fileName = m.group(0);
                fileName = fileName.substring(fileName.indexOf("=") + 1);

                if (fileName.endsWith(";")) {
                    fileName = fileName.substring(0, fileName.length() - 2);
                }

                if (fileName.startsWith("\"")) {
                    fileName = fileName.substring(1, fileName.length() - 2);
                }

                if (fileName.startsWith("UTF-8''")) {
                    fileName = fileName.substring(7);
                }
            }

            return java.net.URLDecoder.decode(fileName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private void downloadDialog(String url, String userAgent, String contentDisposition, String mimeType) {
        String fileName = parseContentDisposition(contentDisposition);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //cookie
        String cookie = CookieManager.getInstance().getCookie(url);
        //Add cookie and User-Agent to request
        request.addRequestHeader("Cookie", cookie);
        request.addRequestHeader("User-Agent", userAgent);
        //file scanned by MediaScannar
        request.allowScanningByMediaScanner();
        //Download is visible and its progress, after completion too.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //DownloadManager created
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //Saving file in Download folder
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        //download enqued
        downloadmanager.enqueue(request);
        Toast.makeText(MainActivity.this, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show();
    }
}
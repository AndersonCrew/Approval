package com.crewcloud.apps.crewapproval;

import android.annotation.SuppressLint;
import android.app.Application;
import android.text.TextUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crewcloud.apps.crewapproval.activity.MainActivity;
import com.crewcloud.apps.crewapproval.util.PreferenceUtilities;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.leolin.shortcutbadger.ShortcutBadger;

public class CrewCloudApplication extends Application {
    private static CrewCloudApplication mInstance;
    private static PreferenceUtilities mPreferenceUtilities;
    private RequestQueue mRequestQueue;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    public MainActivity getMainActivityInstance() {
        return mainActivityInstance;
    }

    public void setMainActivityInstance(MainActivity mainActivityInstance) {
        this.mainActivityInstance = mainActivityInstance;
    }

    private MainActivity mainActivityInstance;

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public void setHasUpdate(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }

    private boolean hasUpdate;

    public static String getProjectCode() {
        return "_EAPP";
    }

    public CrewCloudApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        handleSSLHandshake();
    }

    public void shortcut(int badgeCount) {
        ShortcutBadger.applyCount(this, badgeCount);
    }

    public void removeShortcut() {
        ShortcutBadger.removeCount(this);
    }

    public static synchronized CrewCloudApplication getInstance() {
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setRetryPolicy(new DefaultRetryPolicy(15000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? "CrewApproval" : tag);
        getRequestQueue().add(req);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public synchronized PreferenceUtilities getPreferenceUtilities() {
        if (mPreferenceUtilities == null) {
            mPreferenceUtilities = new PreferenceUtilities();
        }

        return mPreferenceUtilities;
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }
}
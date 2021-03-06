package com.crewcloud.apps.crewapproval.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.crewcloud.apps.crewapproval.BuildConfig;
import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) CrewCloudApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    public static boolean isWifiEnable() {
        WifiManager wifi = (WifiManager) CrewCloudApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) CrewCloudApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifi.isWifiEnabled() && mWifi.isConnected();
    }

    public static void printLogs(String logs) {
        if (BuildConfig.ENABLE_DEBUG) {
            if (logs == null)
                return;
            int maxLogSize = 1000;
            if (logs.length() > maxLogSize) {
                for (int i = 0; i <= logs.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > logs.length() ? logs.length() : end;
                    Log.d(">>>>CrewApproval", logs.substring(start, end));
                }
            } else {
                Log.d(">>>>CrewApproval", logs);
            }
        }
    }

    public static String getString(int stringID) {
        return CrewCloudApplication.getInstance().getApplicationContext().getResources().getString(stringID);
    }

    public static boolean checkStringValue(String... params) {
        for (String param : params) {
            if (param != null) {
                if (TextUtils.isEmpty(param.trim())) {
                    return false;
                }

                if (param.contains("\n") && TextUtils.isEmpty(param.replace("\n", ""))) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    public static long getTimeOffsetInMinute() {
        return TimeUnit.MINUTES.convert(getTimeOffsetInMillis(), TimeUnit.MILLISECONDS);
    }

    public static long getTimeOffsetInMillis() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();

        return mTimeZone.getRawOffset();
    }

    public static int getTimezoneOffsetInMinutes() {
        TimeZone tz = TimeZone.getDefault();
        int offsetMinutes = tz.getRawOffset() / 60000;
        return offsetMinutes;
    }

    public static String getFullHour(String tv) {
        String hour = "", minute = "";
        int h = getHour(tv);
        int m = getMinute(tv);
        if (h < 10) hour = "0" + h;
        else hour = "" + h;
        if (m < 10) minute = "0" + m;
        else minute = "" + m;
        String text = hour + ":" + minute;
        return text;
    }

    public static int getHour(String tv) {
        int h = 0;
        String str[] = tv.trim().split(" ");
        h = Integer.parseInt(str[1].split(":")[0]);
        if (str[0].equalsIgnoreCase("PM")) h += 12;
        return h;
    }

    public static int getMinute(String tv) {
        return Integer.parseInt(tv.split(" ")[1].split(":")[1]);
    }

    public static String getPhoneLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static void showImage(String url, ImageView view) {
        if (url.contains("content") || url.contains("storage")) {
            File f = new File(url);
            if (f.exists()) {
                Picasso.with(view.getContext()).load(f).error(R.drawable.avatar).into(view);
            } else {
                Picasso.with(view.getContext()).load(url).error(R.drawable.avatar).into(view);
            }
        } else {
            String path = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain();
            Picasso.with(view.getContext()).load(path + url).error(R.drawable.avatar).into(view);
        }
    }

    /**
     * Compares two version strings.
     *
     * Use this instead of String.compareTo() for a non-lexicographical
     * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2.
     *         The result is a positive integer if str1 is _numerically_ greater than str2.
     *         The result is zero if the strings are _numerically_ equal.
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    public static void showShortMessage(String text) {
        Toast.makeText(CrewCloudApplication.getInstance().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static String getLanguageCode() {
        Context context = CrewCloudApplication.getInstance().getBaseContext();
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();

        switch (language) {
            case "ko":
                return "KO";
            case "vi":
                return "VN";
            case "zh":
                return "CH";
            default:
                return "EN";
        }
    }

    public static int getTimeZoneOffset() {
        return TimeZone.getDefault().getRawOffset() / 1000 / 60;
    }

    public static String setServerSite(String domain) {
        String[] domains = domain.split("[.]");
        if (domain.contains(".bizsw.co.kr") && !domain.contains("8080")) {
            domain =  domain.replace(".bizsw.co.kr", ".bizsw.co.kr:8080");
        }

        if (domains.length == 1) {
            domain = domains[0] + ".crewcloud.net";
        }

        if(domain.startsWith("http://") || domain.startsWith("https://")){
            CrewCloudApplication.getInstance().getPreferenceUtilities().putStringValue(Constants.DOMAIN, domain);
            String companyName = domain.startsWith("http://") ? domain.replace("http://", ""): domain.startsWith("https://") ? domain.replace("https://", ""): domain;
            CrewCloudApplication.getInstance().getPreferenceUtilities().putStringValue(Constants.COMPANY_NAME, companyName);
            return domain;
        }

        String head = CrewCloudApplication.getInstance().getPreferenceUtilities().getBooleanValue(Constants.HAS_SSL, false) ? "https://" : "http://";
        String domainCompany = head + domain;
        CrewCloudApplication.getInstance().getPreferenceUtilities().putStringValue(Constants.DOMAIN, domainCompany);
        CrewCloudApplication.getInstance().getPreferenceUtilities().putStringValue(Constants.COMPANY_NAME, domain);
        return domainCompany;
    }
}
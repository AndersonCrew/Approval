package com.crewcloud.apps.crewapproval.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.crewcloud.apps.crewapproval.CrewCloudApplication;


public class PreferenceUtilities {
    private SharedPreferences mPreferences;

    private final String KEY_CURRENT_COMPANY_NO = "currentCompanyNo";
    private final String KEY_CURRENT_MOBILE_SESSION_ID = "currentMobileSessionId";
    private final String KEY_CURRENT_USER_ID = "currentUserID";
    private final String KEY_CURRENT_USER_NO = "currentUserNo";
    private final String KEY_CURRENT_USER_IS_ADMIN = "currentUserIsAdmin";
    private final String INTRO_COUNT = "introCount";
    private final String AESORTTYPE = "aeSortType";

    private final String NOTIFI_MAIL = "notifi_newmail";
    private final String NOTIFI_SOUND = "notifi_sound";
    private final String NOTIFI_VIBRATE = "notifi_vibrate";
    private final String NOTIFI_TIME = "notifi_time";
    private final String START_TIME = "starttime";
    private final String END_TIME = "endtime";
    private final String TIME_ZONE = "timezone_off";
    private final String KEY_GCM = "googlecloudmsg";

    private final String FULL_NAME = "fullname";
    private final String EMAIL = "email";
    private final String COMPANY_NAME = "companyname";
    private final String AVATAR = "avatar";
    private final String PASS = "pass";
    private final String NAME = "name";

    private static final String ACCESSTOKEN = "accesstoken";
    public PreferenceUtilities() {
        mPreferences = CrewCloudApplication.getInstance().getApplicationContext().getSharedPreferences("CrewApproval_Prefs", Context.MODE_PRIVATE);
    }

    public void setCurrentCompanyNo(int companyNo) {
        mPreferences.edit().putInt(KEY_CURRENT_COMPANY_NO, companyNo).apply();
    }

    public int getCurrentCompanyNo() {
        return mPreferences.getInt(KEY_CURRENT_COMPANY_NO, 0);
    }

    public void setCurrentMobileSessionId(String sessionId) {
        mPreferences.edit().putString(KEY_CURRENT_MOBILE_SESSION_ID, sessionId).apply();
    }

    public String getCurrentMobileSessionId() {
        return mPreferences.getString(KEY_CURRENT_MOBILE_SESSION_ID, "");
    }


    public void setFullName(String fullName) {
        mPreferences.edit().putString(FULL_NAME, fullName).apply();
    }


    public void setEmail(String email) {
        mPreferences.edit().putString(EMAIL, email).apply();
    }


    public void setCompanyName(String companyName) {
        mPreferences.edit().putString(COMPANY_NAME, companyName).apply();
    }


    public void setUserAvatar(String avatar) {
        mPreferences.edit().putString(AVATAR, avatar).apply();
    }

    public String getUserAvatar() {
        return mPreferences.getString(AVATAR, "");
    }

    public String getDomain() {
        return mPreferences.getString(Constants.DOMAIN, "");
    }

    public String getCompanyName() {
        return mPreferences.getString(Constants.COMPANY_NAME, "");
    }

    public void setPass(String pass) {
        mPreferences.edit().putString(PASS, pass).apply();
    }

    public String getPass() {
        return mPreferences.getString(PASS, "");
    }

    public void setName(String name) {
        mPreferences.edit().putString(NAME, name).apply();
    }

    public String getName() {
        return mPreferences.getString(NAME, "");
    }

    public void setCurrentUserID(String userID) {
        mPreferences.edit().putString(KEY_CURRENT_USER_ID, userID).apply();
    }


    public int getCurrentUserNo() {
        return mPreferences.getInt(KEY_CURRENT_USER_NO, 0);
    }

    public void setCurrentUserNo(int userNo) {
        mPreferences.edit().putInt(KEY_CURRENT_USER_NO, userNo).apply();
    }


    public void setCurrentUserIsAdmin(int isAdmin) {
        mPreferences.edit().putInt(KEY_CURRENT_USER_IS_ADMIN, isAdmin).apply();
    }

    public int getIntroCount() {
        return mPreferences.getInt(INTRO_COUNT, 0);
    }

    public void putaeSortType(int aeSortType) {
        mPreferences.edit().putInt(AESORTTYPE, aeSortType).apply();
    }

      /*--------------------------Set Notification----------------------------*/

    public void setGCMregistrationid(String domain) {
        mPreferences.edit().putString(KEY_GCM, domain).apply();
    }

    public String getGCMregistrationid() {
        return mPreferences.getString(KEY_GCM, "");
    }

    public void setEND_TIME(String domain) {
        mPreferences.edit().putString(END_TIME, domain).apply();
    }

    public String getEND_TIME() {
        return mPreferences.getString(END_TIME, "PM 06:00");
    }

    public void setSTART_TIME(String domain) {
        mPreferences.edit().putString(START_TIME, domain).apply();
    }

    public String getSTART_TIME() {
        return mPreferences.getString(START_TIME, "AM 08:00");
    }

    public void setNOTIFI_TIME(boolean b) {
        mPreferences.edit().putBoolean(NOTIFI_TIME, b).apply();
    }

    public boolean getNOTIFI_TIME() {
        return mPreferences.getBoolean(NOTIFI_TIME, true);
    }

    public void setNOTIFI_VIBRATE(boolean b) {
        mPreferences.edit().putBoolean(NOTIFI_VIBRATE, b).apply();
    }

    public boolean getNOTIFI_VIBRATE() {
        return mPreferences.getBoolean(NOTIFI_VIBRATE, true);
    }

    public void setNOTIFI_MAIL(boolean b) {
        mPreferences.edit().putBoolean(NOTIFI_MAIL, b).apply();
    }

    public boolean getNOTIFI_MAIL() {
        return mPreferences.getBoolean(NOTIFI_MAIL, true);
    }

    public void setNOTIFI_SOUND(boolean b) {
        mPreferences.edit().putBoolean(NOTIFI_SOUND, b).apply();
    }

    public boolean getNOTIFI_SOUND() {
        return mPreferences.getBoolean(NOTIFI_SOUND, true);
    }

    public void setTIME_ZONE(int domain) {
        mPreferences.edit().putInt(TIME_ZONE, domain).apply();
    }

    public int getTIME_ZONE() {
        return mPreferences.getInt(TIME_ZONE, Utils.getTimeZoneOffset());
    }

    public void clearNotificationSetting(){
        mPreferences.edit().remove(NOTIFI_MAIL).apply();
        mPreferences.edit().remove(NOTIFI_SOUND).apply();
        mPreferences.edit().remove(NOTIFI_VIBRATE).apply();
        mPreferences.edit().remove(NOTIFI_TIME).apply();
        mPreferences.edit().remove(START_TIME).apply();
        mPreferences.edit().remove(END_TIME).apply();
    }

    public void putaccesstoken(String accesstoken) {
        putStringValue(ACCESSTOKEN, accesstoken);
    }


    public void putStringValue(String KEY, String value) {
        mPreferences.edit().putString(KEY, value).apply();
    }

    public String getStringValue(String KEY, String defvalue) {
        return mPreferences.getString(KEY, defvalue);
    }

    public void putBooleanValue(String key, boolean value) {
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void putIntValue(String key, int value) {
        mPreferences.edit().putInt(key, value).apply();
    }

    public int getIntValue(String key, int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public void putLongValue(String key, long value) {
        mPreferences.edit().putLong(key, value).apply();
    }

    public long getLongValue(String key, long defaultValue) {
        return mPreferences.getLong(key, defaultValue);
    }
}
package com.crewcloud.apps.crewapproval.util;

import android.util.Log;

import com.android.volley.Request;
import com.crewcloud.apps.crewapproval.BuildConfig;
import com.crewcloud.apps.crewapproval.CrewCloudApplication;
import com.crewcloud.apps.crewapproval.dtos.ErrorDto;
import com.crewcloud.apps.crewapproval.dtos.Profile;
import com.crewcloud.apps.crewapproval.dtos.UserDto;
import com.crewcloud.apps.crewapproval.interfaces.BaseHTTPCallBack;
import com.crewcloud.apps.crewapproval.interfaces.GetUserCallBack;
import com.crewcloud.apps.crewapproval.interfaces.OnAutoLoginCallBack;
import com.crewcloud.apps.crewapproval.interfaces.OnChangePasswordCallBack;
import com.crewcloud.apps.crewapproval.interfaces.OnHasAppCallBack;
import com.crewcloud.apps.crewapproval.interfaces.OnHasUpdateAppCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private static HttpRequest mInstance;
    private static String root_link;

    public static HttpRequest getInstance() {
        if (null == mInstance) {
            mInstance = new HttpRequest();
        }

        root_link = CrewCloudApplication.getInstance().getPreferenceUtilities().getServerSite();

        return mInstance;
    }

    public void login(final BaseHTTPCallBack baseHTTPCallBack, final String userID, final String password, final String companyDomain, String server_link) {
        final String url = server_link + Urls.URL_GET_LOGIN;
        Util.printLogs(">>>User info =" + url);
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Util.getTimeOffsetInMinute());
        params.put("companyDomain", companyDomain);
        params.put("password", password);
        params.put("userID", userID);
        params.put("mobileOSVersion", "Android " + android.os.Build.VERSION.RELEASE);
        Util.printLogs(">>>login =" + params.toString());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Util.printLogs(">>>User info =" + response);
                Gson gson = new Gson();
                UserDto userDto = gson.fromJson(response, UserDto.class);

                userDto.prefs.setCurrentMobileSessionId(userDto.session);
                userDto.prefs.setCurrentUserIsAdmin(userDto.PermissionType);
                userDto.prefs.setCurrentCompanyNo(userDto.CompanyNo);
                userDto.prefs.setCurrentUserNo(userDto.Id);
                userDto.prefs.setFullName(userDto.FullName);
                userDto.prefs.setCompanyName(userDto.NameCompany);
                userDto.prefs.setEmail(userDto.MailAddress);
                userDto.prefs.setCurrentUserID(userDto.userID);
                userDto.prefs.setUserAvatar(userDto.avatar);

                PreferenceUtilities preferenceUtilities = new PreferenceUtilities();
                preferenceUtilities.setName(userID);
                preferenceUtilities.setPass(password);
                preferenceUtilities.setDomain(companyDomain);
                baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void checkLogin(final BaseHTTPCallBack baseHTTPCallBack) {
        final String url = root_link + Urls.URL_CHECK_SESSION;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Util.getTimeOffsetInMinute());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Gson gson = new Gson();
                UserDto userDto = gson.fromJson(response, UserDto.class);
                userDto.prefs.setCurrentMobileSessionId(userDto.session);
                userDto.prefs.setCurrentUserIsAdmin(userDto.PermissionType);
                userDto.prefs.setCurrentCompanyNo(userDto.CompanyNo);
                userDto.prefs.setCurrentUserNo(userDto.Id);
                userDto.prefs.setCurrentUserID(userDto.userID);
                //UserDBHelper.addUser(userDto);
                if (baseHTTPCallBack != null) {
                    baseHTTPCallBack.onHTTPSuccess();
                }
            }

            @Override
            public void onFailure(ErrorDto error) {
                if (baseHTTPCallBack != null) {
                    baseHTTPCallBack.onHTTPFail(error);
                }
            }
        });
    }

    public void AutoLogin(final String companyDomain, String userID, String server_link, final OnAutoLoginCallBack callBack) {
        final String url = server_link + Urls.URL_AUTO_LOGIN;
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Util.getTimeOffsetInMinute());
        params.put("companyDomain", companyDomain);
        params.put("userID", userID);
        params.put("mobileOSVersion", "Android " + android.os.Build.VERSION.RELEASE);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
//                Log.d(">>>>>", response);
                Util.printLogs("User info =" + response);
                Gson gson = new Gson();
                UserDto userDto = gson.fromJson(response, UserDto.class);

                userDto.prefs.setCurrentMobileSessionId(userDto.session);
                userDto.prefs.setCurrentUserIsAdmin(userDto.PermissionType);
                userDto.prefs.setCurrentCompanyNo(userDto.CompanyNo);
                userDto.prefs.setCurrentUserNo(userDto.Id);
                userDto.prefs.setFullName(userDto.FullName);
                userDto.prefs.setCompanyName(userDto.NameCompany);
                userDto.prefs.setEmail(userDto.MailAddress);
                userDto.prefs.setCurrentUserID(userDto.userID);
                userDto.prefs.setUserAvatar(userDto.avatar);
                //UserDBHelper.addUser(userDto);
                userDto.prefs.setCurrentCompanyDomain(companyDomain);
                callBack.OnAutoLoginSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                callBack.OnAutoLoginFail(error);
            }
        });
    }

    //----------------------------------------------- Notification ---------------------------------------------------------------
    public final static String URL_GET_USER = "/UI/WebService/WebServiceCenter.asmx/GetUser";
    public final static String URL_INSERT_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/InsertAndroidDevice";
    public final static String URL_DELETE_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/DeleteAndroidDevice";
    public final static String URL_UPDATE_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/UpdateAndroidDevice_NotificationOptions";
    public final static String URL_UPDATE_TIMEZONE_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/UpdateAndroidDevice_TimezoneOffset";
    public final static String URL_GET_BADGE_COUNT = "/UI/_EAPPMobile/EAPPMobileService.asmx/RefreshAndroidBadgeCount";

    public static void insertAndroidDevice(final BaseHTTPCallBack callBack, String regid, String json) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentServiceDomain() + URL_INSERT_ANDROID_DEVICE;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Util.getTimezoneOffsetInMinutes());
        params.put("deviceID", regid);
        params.put("osVersion", "Android " + android.os.Build.VERSION.RELEASE);
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("notificationOptions", json);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                callBack.onHTTPSuccess();
//                Log.e(TAG,"response:"+response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                callBack.onHTTPFail(error);
            }
        });
    }

    public void updateAndroidDevice(String regid, String json) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentServiceDomain() + URL_UPDATE_ANDROID_DEVICE;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Util.getTimezoneOffsetInMinutes());
        params.put("deviceID", regid);
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("notificationOptions", json);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Log.e("Update API", "Update:" + response);
            }

            @Override
            public void onFailure(ErrorDto error) {

//                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public static void deleteAndroidDevice(final BaseHTTPCallBack callBack) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentServiceDomain() + URL_DELETE_ANDROID_DEVICE;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Util.getTimezoneOffsetInMinutes());
        params.put("languageCode", Util.getPhoneLanguage());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Log.e("Delete", " :" + response);
                callBack.onHTTPSuccess();
//                Log.e(TAG,"response:"+response);
            }

            @Override
            public void onFailure(ErrorDto error) {

//                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public static void updateTimeZone(String regid) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentServiceDomain() + URL_UPDATE_TIMEZONE_ANDROID_DEVICE;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Util.getTimezoneOffsetInMinutes());
        params.put("deviceID", regid);
        params.put("languageCode", Util.getPhoneLanguage());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {

                Log.e("CrewApproval", "Update TimeZone response:" + response);
            }

            @Override
            public void onFailure(ErrorDto error) {

//                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void GetUser(int userNo, final GetUserCallBack callBack) {
        String url = root_link + URL_GET_USER;
//        Log.e(TAG, root_link + " GetUser:" + url);
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("languageCode", Util.getPhoneLanguage());
        params.put("timeZoneOffset", TimeUtils.getTimezoneOffsetInMinutes());
        params.put("userNo", userNo);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Type listType = new TypeToken<Profile>() {
                }.getType();
                Profile profile = new Gson().fromJson(response, listType);
                callBack.onGetUserSuccess(profile);
            }

            @Override
            public void onFailure(ErrorDto error) {
                Log.e("", "onFailure");
            }
        });
    }

    public void getBadgeCount() {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentServiceDomain() + URL_GET_BADGE_COUNT;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Util.getTimezoneOffsetInMinutes());
        params.put("languageCode", Util.getPhoneLanguage());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
//                    Log.d(">>>HttpRequest", String.valueOf(response));
//                    JSONObject data = new JSONObject(response);
                    int count = Integer.parseInt(response);
//                    Log.d(">>>HttpRequest", String.valueOf(count));
                    if (count > 0)
                        CrewCloudApplication.getInstance().shortcut(count);
                    else
                        CrewCloudApplication.getInstance().removeShortcut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorDto error) {
                Log.d(">>>HttpRequest", error.message);
//                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void checkApplicationUpdate(final OnHasUpdateAppCallBack callBack) {
        final String url = "http://mobileupdate.crewcloud.net/WebServiceMobile.asmx/Mobile_Version";
        Map<String, String> params = new HashMap<>();

        String domain = CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentServiceDomain();
        domain = domain.replace("http://", "");
        params.put("MobileType", "Android");
        params.put("Domain", domain);
        params.put("Applications", "CrewApproval");
        Log.d(">>>Update", params.toString());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Log.d(">>>Update", response);
                try {
                    String appVersion = BuildConfig.VERSION_NAME;
                    JSONObject json = new JSONObject(response);
                    String version = json.getString("version");
                    if(version.isEmpty()) {
                        callBack.noHas(new ErrorDto());
                    }else {
                        if (callBack != null) {
                            if (Util.versionCompare(version, appVersion) > 0) {
                                String url = json.getString("packageUrl");
                                callBack.hasApp(url);
                            } else {
                                ErrorDto errorDto = new ErrorDto();
                                errorDto.message = "";
                                callBack.noHas(errorDto);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.noHas(new ErrorDto());
                }
            }

            @Override
            public void onFailure(ErrorDto error) {
                Log.d(">>>Update", "Fail " + error.message);
                callBack.noHas(error);
            }
        });
    }

    public void ChangePassword(String originalPassword, final String newPassword, final OnChangePasswordCallBack callBack) {
        String url = root_link + Urls.URL_CHANGE_PASSWORD;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("languageCode", Locale.getDefault().getLanguage().toUpperCase());
        params.put("timeZoneOffset", "" +TimeUtils.getTimezoneOffsetInMinutes());
        params.put("originalPassword", originalPassword);
        params.put("newPassword", newPassword);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String newSessionID = json.getString("newSessionID");
                    CrewCloudApplication.getInstance().getPreferenceUtilities().putaccesstoken(newSessionID);
                    if (callBack != null) {
                        callBack.onSuccess(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ErrorDto errorDto = new ErrorDto();
                    errorDto.message = e.toString();
                    callBack.onFail(errorDto);
                }

            }

            @Override
            public void onFailure(ErrorDto error) {
                if (callBack != null) {
                    callBack.onFail(error);
                }
            }
        });
    }
}
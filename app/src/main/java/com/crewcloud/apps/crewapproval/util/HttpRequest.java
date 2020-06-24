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
import com.crewcloud.apps.crewapproval.interfaces.ICheckSSL;
import com.crewcloud.apps.crewapproval.interfaces.OnAutoLoginCallBack;
import com.crewcloud.apps.crewapproval.interfaces.OnChangePasswordCallBack;
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

        root_link = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain();
        return mInstance;
    }

    public void login(final BaseHTTPCallBack baseHTTPCallBack, final String userID, final String password) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_GET_LOGIN;
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Utils.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Utils.getTimeOffsetInMinute());
        params.put("companyDomain", CrewCloudApplication.getInstance().getPreferenceUtilities().getCompanyName());
        params.put("password", password);
        params.put("userID", userID);
        params.put("mobileOSVersion", "Android " + android.os.Build.VERSION.RELEASE);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Utils.printLogs(">>>User info =" + response);
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
                baseHTTPCallBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                baseHTTPCallBack.onHTTPFail(error);
            }
        });
    }

    public void checkLogin(final BaseHTTPCallBack baseHTTPCallBack) {
        final String url = root_link + Config.URL_CHECK_SESSION;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("languageCode", Utils.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Utils.getTimeOffsetInMinute());
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

    public void AutoLogin(String userID, final OnAutoLoginCallBack callBack) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_AUTO_LOGIN;
        Map<String, String> params = new HashMap<>();
        params.put("languageCode", Utils.getPhoneLanguage());
        params.put("timeZoneOffset", "" + Utils.getTimeOffsetInMinute());
        params.put("companyDomain", CrewCloudApplication.getInstance().getPreferenceUtilities().getCompanyName());
        params.put("userID", userID);
        params.put("mobileOSVersion", "Android " + android.os.Build.VERSION.RELEASE);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Utils.printLogs("User info =" + response);
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
                callBack.OnAutoLoginSuccess(response);
            }

            @Override
            public void onFailure(ErrorDto error) {
                callBack.OnAutoLoginFail(error);
            }
        });
    }

    /*Insert Token*/
    public static void insertAndroidDevice(final BaseHTTPCallBack callBack, String regid, String json) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_INSERT_ANDROID_DEVICE;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Utils.getTimezoneOffsetInMinutes());
        params.put("deviceID", regid);
        params.put("osVersion", "Android " + android.os.Build.VERSION.RELEASE);
        params.put("languageCode", Utils.getPhoneLanguage());
        params.put("notificationOptions", json);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
                callBack.onHTTPFail(error);
            }
        });
    }

    /*Update Token*/
    public void updateAndroidDevice(String regid, String json) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_UPDATE_ANDROID_DEVICE;

        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Utils.getTimezoneOffsetInMinutes());
        params.put("deviceID", regid);
        params.put("languageCode", Utils.getPhoneLanguage());
        params.put("notificationOptions", json);
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(ErrorDto error) {
            }
        });
    }

    /*Delete Token*/
    public static void deleteAndroidDevice(final BaseHTTPCallBack callBack) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_DELETE_ANDROID_DEVICE;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Utils.getTimezoneOffsetInMinutes());
        params.put("languageCode", Utils.getPhoneLanguage());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                Log.e("Delete", " :" + response);
                callBack.onHTTPSuccess();
            }

            @Override
            public void onFailure(ErrorDto error) {
            }
        });
    }

    /*Update Timezone*/
    public static void updateTimeZone(String regid) {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_UPDATE_TIMEZONE_ANDROID_DEVICE;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Utils.getTimezoneOffsetInMinutes());
        params.put("deviceID", regid);
        params.put("languageCode", Utils.getPhoneLanguage());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
            }

            @Override
            public void onFailure(ErrorDto error) {
            }
        });
    }

    /*Get UserProfile*/
    public void GetUser(int userNo, final GetUserCallBack callBack) {
        String url = root_link + Config.URL_GET_USER;
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("languageCode", Utils.getPhoneLanguage());
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

    /*Get BadgeCount*/
    public void getBadgeCount() {
        final String url = CrewCloudApplication.getInstance().getPreferenceUtilities().getDomain() + Config.URL_GET_BADGE_COUNT;
        Map<String, String> params = new HashMap<>();
        params.put("sessionId", "" + CrewCloudApplication.getInstance().getPreferenceUtilities().getCurrentMobileSessionId());
        params.put("timeZoneOffset", "" + Utils.getTimezoneOffsetInMinutes());
        params.put("languageCode", Utils.getPhoneLanguage());
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    int count = Integer.parseInt(response);
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
            }
        });
    }


    /*Check Update*/
    public void checkApplicationUpdate(final OnHasUpdateAppCallBack callBack) {
        final String url = "http://mobileupdate.crewcloud.net/WebServiceMobile.asmx/Mobile_Version";
        Map<String, String> params = new HashMap<>();

        params.put("MobileType", "Android");
        params.put("Domain", CrewCloudApplication.getInstance().getPreferenceUtilities().getCompanyName());
        params.put("Applications", "CrewApproval");
        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    String appVersion = BuildConfig.VERSION_NAME;
                    JSONObject json = new JSONObject(response);
                    String version = json.getString("version");
                    if(version.isEmpty()) {
                        callBack.noHas(new ErrorDto());
                    } else {
                        if (callBack != null) {
                            if (Utils.versionCompare(version, appVersion) > 0) {
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


    /*Change Password*/
    public void ChangePassword(String originalPassword, final String newPassword, final OnChangePasswordCallBack callBack) {
        String url = root_link + Config.URL_CHANGE_PASSWORD;
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

    /*Check SSL*/
    public void checkSSL(final ICheckSSL checkSSL) {
        final String url = Config.URL_CHECK_SSL;
        Map<String, String> params = new HashMap<>();
        params.put("Domain", CrewCloudApplication.getInstance().getPreferenceUtilities().getCompanyName());
        params.put("Applications", "CrewApproval");

        WebServiceManager webServiceManager = new WebServiceManager();
        webServiceManager.doJsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new WebServiceManager.RequestListener<String>() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean hasSSL = jsonObject.getBoolean("SSL");
                    CrewCloudApplication.getInstance().getPreferenceUtilities().putBooleanValue(Constants.HAS_SSL, hasSSL);
                    checkSSL.hasSSL(hasSSL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ErrorDto error) {
                checkSSL.checkSSLError(error);
            }
        });
    }
}
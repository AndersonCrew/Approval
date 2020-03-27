package com.crewcloud.apps.crewapproval.util

object Config {
    const val URL_GET_USER = "/UI/WebService/WebServiceCenter.asmx/GetUser"
    const val URL_INSERT_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/InsertAndroidDevice"
    const val URL_DELETE_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/DeleteAndroidDevice"
    const val URL_UPDATE_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/UpdateAndroidDevice_NotificationOptions"
    const val URL_UPDATE_TIMEZONE_ANDROID_DEVICE = "/UI/_EAPPMobile/EAPPMobileService.asmx/UpdateAndroidDevice_TimezoneOffset"
    const val URL_GET_BADGE_COUNT = "/UI/_EAPPMobile/EAPPMobileService.asmx/RefreshAndroidBadgeCount"

    const val ROOT_URL_ANDROID = "http://www.crewcloud.net/Android"
    const val VERSION = "/Version/"
    const val PACKAGE = "/Package/"
    const val CREWAPPROVAL = "CrewApproval"
    const val ACTIVITY_HANDLER_NEXT_ACTIVITY = 1111
    const val ACTIVITY_HANDLER_START_UPDATE = 1112

    const val URL_AUTO_LOGIN = "/UI/WebService/WebServiceCenter.asmx/AutoLogin"
    const val URL_GET_LOGIN = "/UI/WebService/WebServiceCenter.asmx/Login_v2"
    const val URL_CHECK_SESSION = "/UI/WebService/WebServiceCenter.asmx/CheckSessionUser_v2"
    const val URL_HAS_APPLICATION = "/UI/WebService/WebServiceCenter.asmx/HasApplication_v2"
    const val URL_CHANGE_PASSWORD = "/UI/WebService/WebServiceCenter.asmx/UpdatePassword"

    const val SERVICE_URL_LOGOUT_V2 = "/UI/WebService/WebServiceCenter.asmx/Logout_v2"
    const val SERVICE_URL_CHECK_SESSION_USER_V2 = "/UI/WebService/WebServiceCenter.asmx/CheckSessionUser_v2"
    const val SERVICE_URL_HAS_APPLICATION_V2 = "/UI/WebService/WebServiceCenter.asmx/HasApplication_v2"
}
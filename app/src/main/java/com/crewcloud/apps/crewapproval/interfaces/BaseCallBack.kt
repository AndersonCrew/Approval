package com.crewcloud.apps.crewapproval.interfaces

import com.crewcloud.apps.crewapproval.dtos.ErrorDto
import com.crewcloud.apps.crewapproval.dtos.Profile

interface ICheckSSL {
    fun hasSSL(hasSSL: Boolean)
    fun checkSSLError(errorData: ErrorDto)
}

interface BaseHTTPCallBack {
    fun onHTTPSuccess()
    fun onHTTPFail(errorDto: ErrorDto)
}

interface GetUserCallBack {
    fun onGetUserSuccess(profile: Profile)
    fun onError()
}

interface OnAutoLoginCallBack {
    fun OnAutoLoginSuccess(response: String)
    fun OnAutoLoginFail(dto: ErrorDto)
}

interface OnChangePasswordCallBack {
    fun onSuccess(response: String)
    fun onFail(errorDto: ErrorDto)
}


interface OnHasAppCallBack {
    fun hasApp()
    fun noHas(dto: ErrorDto)
}

interface OnHasUpdateAppCallBack {
    fun hasApp(url: String)
    fun noHas(dto: ErrorDto)
}

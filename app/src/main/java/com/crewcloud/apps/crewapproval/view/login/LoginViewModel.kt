package com.crewcloud.apps.crewapproval.view.login

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.crewcloud.apps.crewapproval.R
import com.crewcloud.apps.crewapproval.services.APIServices
import com.crewcloud.apps.crewapproval.services.BaseResponse
import com.crewcloud.apps.crewapproval.util.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.util.HashMap

class LoginViewModel : ViewModel() {
    var isSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var strError: MutableLiveData<String> = MutableLiveData()

    fun login(companyDomain: String, password: String, userID: String) {
        val params = HashMap<String, String>()
        params["languageCode"] = Utils.getPhoneLanguage()
        params["timeZoneOffset"] = "" + Utils.getTimeOffsetInMinute()
        params["companyDomain"] = companyDomain
        params["password"] = password
        params["userID"] = userID
        params["mobileOSVersion"] = "Android " + android.os.Build.VERSION.RELEASE

        APIServices.apiServices.login(JSONObject(params)).enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                t.message?.let {
                    strError.value = it
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

            }

        })

    }
}
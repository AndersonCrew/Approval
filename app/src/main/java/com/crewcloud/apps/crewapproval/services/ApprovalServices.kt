package com.crewcloud.apps.crewapproval.services

import android.arch.lifecycle.LiveData
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.POST

interface ApprovalServices {
    @POST("Login_v2")
    fun login(bodyParam: JSONObject): Call<BaseResponse>
}
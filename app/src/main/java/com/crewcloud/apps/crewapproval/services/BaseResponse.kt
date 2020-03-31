package com.crewcloud.apps.crewapproval.services

import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

class BaseError(code: Int, message: String)
class BaseResponse(response : String){
    init {
        getResult(response)
    }

    private fun getResult(response: String){
        try {
            JSONObject(response).get("d")?.let {
                Timber.d(it.toString())
                val d = 1
            }
        } catch (ex: Exception){
            ex.printStackTrace()
        }
    }
}

sealed class Resource{
    class Success(data: String?)
    class Error(error: BaseError?)
}
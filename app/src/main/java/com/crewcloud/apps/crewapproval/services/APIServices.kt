package com.crewcloud.apps.crewapproval.services

import com.crewcloud.apps.crewapproval.util.Constants
import com.crewcloud.apps.crewapproval.util.PreferenceUntils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIServices {
    private var retrofit: Retrofit?= null
    private var domain = PreferenceUntils.getStringValue(Constants.DOMAIN)?: ""

    val apiServices: ApprovalServices
    get() {
        if(retrofit == null){
            retrofit = Retrofit.Builder()
                    .baseUrl(domain)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }

        return retrofit!!.create(ApprovalServices::class.java)
    }
}
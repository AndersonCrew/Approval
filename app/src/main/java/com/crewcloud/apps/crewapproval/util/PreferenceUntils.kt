package com.crewcloud.apps.crewapproval.util

import android.content.Context
import android.content.SharedPreferences
import com.crewcloud.apps.crewapproval.CrewCloudApplication

object PreferenceUntils {
    private var mPref: SharedPreferences = CrewCloudApplication.getInstance().applicationContext.getSharedPreferences("CrewApproval_Prefs", Context.MODE_PRIVATE)
    private var mEditor: SharedPreferences.Editor

    init {
        mEditor = mPref.edit()
    }

    /*Get Set Int Value*/
    fun setIntValue(key: String, value: Int) {
        mEditor.putInt(key, value).apply()
    }

    fun getIntValue(key: String) : Int{
        return mPref.getInt(key, -1)
    }

    /*Get Set Float Value*/
    fun setFloatValue(key: String, value: Float) {
        mEditor.putFloat(key, value).apply()
    }

    fun getFloatValue(key: String) : Float{
        return mPref.getFloat(key, -1f)
    }

    /*Get Set String*/
    fun setStrngValue(key: String, value: String) {
        mEditor.putString(key, value)
    }

    fun getStringValue(key: String): String? {
        return mPref.getString(key, null)
    }
}
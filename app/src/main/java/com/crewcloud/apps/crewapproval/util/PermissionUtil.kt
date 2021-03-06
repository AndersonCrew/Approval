package com.crewcloud.apps.crewapproval.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

object PermissionUtil {
    fun checkPermissions(context: Context, permissions: List<String>) = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity, REQUEST_CODE: Int, permissions: List<String>) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), REQUEST_CODE)
    }

    val writeExternal : List<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE).toList()
}
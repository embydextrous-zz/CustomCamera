package com.embydextrous.customcamera

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


fun isPermissionGranted(context: Context, permissionList: List<String>): Boolean {
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

fun shouldShowPermissionRationale(activity: Activity, permissionList: List<String>): Boolean {
        for (permission in permissionList) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                return true
        }
        return false
    }


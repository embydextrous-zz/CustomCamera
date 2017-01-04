package com.embydextrous.customcamera

import android.app.Activity
import android.content.Context
import android.hardware.Camera
import android.view.Surface

fun getOptimalPreviewSize(sizes: List<Camera.Size>?, context: Context): Camera.Size? {
    val ASPECT_TOLERANCE = 0.1
    val screenRatio = getDisplayHeight(context) / (getScreenWidth(context).toDouble())
    val targetRatio: Double
    if (screenRatio > 1.59) {
        targetRatio = 16.0 / 9.0
    } else {
        targetRatio = 4.0/3.0
    }
    if (sizes == null) return null

    var optimalSize: Camera.Size? = null
    var minDiff = java.lang.Double.MAX_VALUE

    val targetHeight = getScreenHeight(context)

    for (size in sizes) {
        val ratio = size.width.toDouble() / size.height
        if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
        if (Math.abs(size.height - targetHeight) < minDiff) {
            optimalSize = size
            minDiff = Math.abs(size.height - targetHeight).toDouble()
        }
    }

    if (optimalSize == null) {
        minDiff = java.lang.Double.MAX_VALUE
        for (size in sizes) {
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }
    }
    return optimalSize
}

fun getOptimalPictureSize(sizes: List<Camera.Size>?, context: Context): Camera.Size? {
    val ASPECT_TOLERANCE = 0.1
    val screenRatio = getDisplayHeight(context) / (getScreenWidth(context).toDouble())
    val targetRatio: Double
    if (screenRatio > 1.59) {
        targetRatio = 16.0 / 9.0
    } else {
        targetRatio = 1.0
    }

    if (sizes == null) return null

    var optimalSize: Camera.Size? = null
    var minDiff = java.lang.Double.MAX_VALUE

    val targetHeight = getScreenHeight(context)

    for (size in sizes) {
        val ratio = size.width.toDouble() / size.height
        if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue
        if (Math.abs(size.height - targetHeight) < minDiff) {
            optimalSize = size
            minDiff = Math.abs(size.height - targetHeight).toDouble()
        }
    }

    if (optimalSize == null) {
        minDiff = java.lang.Double.MAX_VALUE
        for (size in sizes) {
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size
                minDiff = Math.abs(size.height - targetHeight).toDouble()
            }
        }
    }
    return optimalSize
}

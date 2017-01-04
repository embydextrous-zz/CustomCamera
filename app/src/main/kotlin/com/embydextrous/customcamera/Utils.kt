package com.embydextrous.customcamera

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

fun getScreenHeight(c: Context): Int {
    val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
}

fun getScreenWidth(c: Context): Int {
    val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.x
}

fun getDisplayHeight(context: Context): Int {
    var height = 0
    val metrics = DisplayMetrics()
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    var mGetRawH: Method? = null
    val mGetRawW: Method? = null

    try {
        // For JellyBean 4.2 (API 17) and onward
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics)

            height = metrics.heightPixels
        } else {
            mGetRawH = Display::class.java.getMethod("getRawHeight")
            try {
                height = mGetRawH!!.invoke(display) as Int
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        }
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    }

    return height
}

fun getNavigationBarHeight(context: Context): Int {
    val appUsableSize = getAppUsableScreenSize(context)
    val realScreenSize = getRealScreenSize(context)
    if (appUsableSize.y < realScreenSize.y) {
        return realScreenSize.y - appUsableSize.y
    }
    return 0
}

fun getAppUsableScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

fun getRealScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()

    if (Build.VERSION.SDK_INT >= 17) {
        display.getRealSize(size)
    } else if (Build.VERSION.SDK_INT >= 14) {
        try {
            size.x = Display::class.java.getMethod("getRawWidth").invoke(display) as Int
            size.y = Display::class.java.getMethod("getRawHeight").invoke(display) as Int
        } catch (e: IllegalAccessException) {
        } catch (e: InvocationTargetException) {
        } catch (e: NoSuchMethodException) {
        }

    }
    return size
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

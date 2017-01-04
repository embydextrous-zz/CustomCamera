package com.listup.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v7.widget.AppCompatDrawableManager

object BitmapUtil {

    enum class Direction {
        VERTICAL, HORIZONTAL
    }

    @JvmStatic
    fun flip(src: Bitmap, direction: Direction): Bitmap {
        val matrix = Matrix()
        when(direction) {
            Direction.VERTICAL -> matrix.preScale(1.0f, -1.0f)
            Direction.HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
        }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    @JvmStatic
    fun getBitmapDrawable(context: Context, @DrawableRes resId: Int): Drawable {
        return AppCompatDrawableManager.get().getDrawable(context, resId)
    }

    @JvmStatic
    fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
        val mtx = Matrix()
        mtx.setRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, mtx, true)
    }
}
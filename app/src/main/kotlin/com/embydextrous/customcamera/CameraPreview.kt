package com.embydextrous.customcamera

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Camera
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(context : Context, camera : Camera) : SurfaceView(context), SurfaceHolder.Callback {

    private val camera : Camera
    private var mDist: Float = 0.toFloat()

    init {
        this.camera = camera
        holder.addCallback(this)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
        try {
            camera.setPreviewDisplay(surfaceHolder)
            camera.startPreview()
        } catch (e : IOException) {
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (surfaceHolder?.surface == null)
            return
        try {
            camera.stopPreview()
            camera.setPreviewDisplay(surfaceHolder)
            setCameraParameters()
            camera.startPreview()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    private fun setCameraParameters() {
        //setCameraDisplayOrientation(context as Activity, 0, camera)
        val previewSize = getOptimalPreviewSize(camera.parameters.supportedPreviewSizes, context)
        val pictureSize = getOptimalPictureSize(camera.parameters.supportedPictureSizes, context)
        val params = camera.parameters
        params.setPreviewSize(previewSize!!.width, previewSize.height)
        if (params.supportedFocusModes != null && params.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
        if (params.supportedFocusModes != null && params.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
            params.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        if (params.supportedSceneModes != null && params.supportedSceneModes.contains(Camera.Parameters.SCENE_MODE_AUTO)) {
            params.sceneMode = Camera.Parameters.SCENE_MODE_AUTO
        }
        if (params.supportedWhiteBalance != null && params.supportedWhiteBalance.contains(Camera.Parameters.WHITE_BALANCE_AUTO)) {
            params.whiteBalance = Camera.Parameters.WHITE_BALANCE_AUTO
        }
        if (!(params.minExposureCompensation == 0 && params.maxExposureCompensation == 0))
            params.exposureCompensation = 0
        params.setPictureSize(pictureSize!!.width, pictureSize.height)
        params.jpegQuality = 100
        params.pictureFormat = ImageFormat.JPEG
        camera.parameters = params
    }

    private fun handleZoom(event: MotionEvent, params: Camera.Parameters) {
        val maxZoom = params.maxZoom / 2
        var zoom = params.zoom
        val newDist = getFingerSpacing(event)
        if (newDist > mDist) {
            if (zoom < maxZoom)
                zoom++
        } else if (newDist < mDist) {
            if (zoom > 0)
                zoom--
        }
        mDist = newDist
        params.zoom = zoom
        camera.parameters = params
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val params: Camera.Parameters
        if (camera == null) return false
        try {
            params = camera.parameters
        } catch (e: Exception) {
            return false
        }

        val action = event.action
        if (event.pointerCount > 1) {
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event)
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported) {
                camera.cancelAutoFocus()
                handleZoom(event, params)
            }
        }
        return true
    }
}

package com.embydextrous.customcamera

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.app.Fragment
import android.hardware.Camera.CameraInfo
import android.hardware.SensorManager
import android.media.ExifInterface
import android.os.Environment
import android.support.v7.content.res.AppCompatResources
import android.util.Log
import android.view.*
import com.listup.android.util.BitmapUtil
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment(), Camera.AutoFocusCallback {

    override fun onAutoFocus(success: Boolean, p1: Camera?) {
        if (success) {
            camera!!.takePicture(null, null, pictureCallback)
        } else {
            camera!!.autoFocus(this)
        }
    }

    private val ORIENTATION_PORTRAIT_NORMAL = 1
    private val ORIENTATION_PORTRAIT_INVERTED = 2
    private val ORIENTATION_LANDSCAPE_NORMAL = 3
    private val ORIENTATION_LANDSCAPE_INVERTED = 4

    var isFrontCamera = false
    var isFlashEnabled = false
    var camera : Camera? = null
    var selfieCamId = -1
    private var mOrientation = -1
    var preview : CameraPreview? = null
    private var rotation = 0
    private var mOrientationEventListener: OrientationEventListener? = null
    private var outputFileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selfieCamId = getFrontCameraId()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_camera, container, false)
        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (selfieCamId==-1) cameraToggle.visibility = View.GONE else cameraToggle.visibility = View.VISIBLE
        flashToggle.setOnClickListener { toggleFlash() }
        cameraToggle.setOnClickListener { toggleCamera() }
        shutterButton.setOnClickListener { takePicture() }
    }

    private fun takePicture() {
        if (camera==null) return
        if (camera!!.parameters.supportedFocusModes != null && camera!!.parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            val params = camera!!.parameters
            params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            camera!!.parameters = params
        }

        if (camera!!.parameters.supportedFocusModes != null && camera!!.parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
            try {
                camera!!.autoFocus(this)
            } catch (e: Exception) {
                camera!!.takePicture(null, null, pictureCallback)
            }
        else
            camera!!.takePicture(null, null, pictureCallback)
    }

    private val pictureCallback = Camera.PictureCallback { data, camera ->
        try {
            camera.stopPreview()
            mOrientationEventListener!!.disable()
            rotateViews(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (!dir.exists()) {
            val directory = File("/sdcard/customcamera/")
            directory.mkdirs()
        }
        outputFileName = dir.absolutePath + File.separator + System.currentTimeMillis().toString() + "_listup_portarit.jpg"
        val portraitFile = File(outputFileName)
        try {
            val fos = FileOutputStream(portraitFile)
            var realImage = BitmapFactory.decodeByteArray(data, 0, data.size)
            val exif = ExifInterface(portraitFile.toString())
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            exif.setAttribute(ExifInterface.TAG_DATETIME, sdf.format(Date()))
            Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION))
            var rotateAngle = 90
            if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("6", ignoreCase = true)) {
                when (mOrientation) {
                    ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 0
                    ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 180
                    ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 90
                    ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = 270
                    else -> rotateAngle = 0
                }
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("8", ignoreCase = true)) {
                when (mOrientation) {
                    ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 180
                    ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 0
                    ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 270
                    ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = 90
                    else -> rotateAngle = 180
                }
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("3", ignoreCase = true)) {
                when (mOrientation) {
                    ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 270
                    ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 90
                    ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 180
                    ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = 0
                    else -> rotateAngle = 270
                }
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("0", ignoreCase = true) || exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("1", ignoreCase = true)) {
                when (mOrientation) {
                    ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 90
                    ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 270
                    ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 0
                    ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = 180
                    else -> rotateAngle = 90
                }
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("2", ignoreCase = true)) {
                when (mOrientation) {
                    ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 90
                    ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 270
                    ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 0
                    ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = 180
                    else -> rotateAngle = 90
                }
                realImage = BitmapUtil.flip(realImage, BitmapUtil.Direction.HORIZONTAL)
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equals("4", ignoreCase = true)) {
                when (mOrientation) {
                    ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 90
                    ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 270
                    ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 0
                    ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = 180
                    else -> rotateAngle = 90
                }
                realImage = BitmapUtil.flip(realImage, BitmapUtil.Direction.VERTICAL)
            }
            if (isFrontCamera) {
                rotateAngle -= 180
            }
            realImage = BitmapUtil.rotate(realImage, rotateAngle)
            realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            (activity as CameraActivity).showPreview(realImage.height.toDouble()/realImage.width, outputFileName)
            /* setTakenPictureView(realImage.width, realImage.height, outputPortraitFileName, orientation)
             showUseButtons()*/
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun toggleCamera() {
        if (camera==null) return
        isFrontCamera = !isFrontCamera
        camera?.stopPreview()
        camera?.release()
        camera = getCameraInstance()
        preview = CameraPreview(activity, camera!!)
        cameraView.removeAllViews()
        cameraView.addView(preview)
        setCameraDisplayOrientation(activity, getCameraId(), camera!!)
        if (isFrontCamera)
            flashToggle.animate().scaleX(0f).scaleY(0f).start()
        else
            flashToggle.animate().scaleX(1f).scaleY(1f).start()
        cameraToggle.animate().scaleX(if (isFrontCamera) -1f else 1f).start()
        flashToggle.requestLayout()
    }

    fun toggleFlash() {
        if (camera==null) return
        if (isFlashEnabled) {
            flashToggle.animate().scaleY(1.0f).scaleX(1.0f).setDuration(300).start()
            flashToggle.setBackgroundResource(android.R.color.transparent)
            flashToggle.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.ic_flash_off))
        } else {
            flashToggle.animate().scaleY(1.2f).scaleX(1.2f).setDuration(300).start()
            flashToggle.setBackgroundResource(R.drawable.circular_bg)
            flashToggle.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.ic_flash_on))
        }
        isFlashEnabled = !isFlashEnabled
        try {
            val p = camera?.parameters
            p?.flashMode = if (isFlashEnabled) Camera.Parameters.FLASH_MODE_ON else Camera.Parameters.FLASH_MODE_OFF
            camera?.parameters = p
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        camera = getCameraInstance()
        if (camera != null) {
            preview = CameraPreview(activity, camera!!)
            cameraView.addView(preview)
            setCameraDisplayOrientation(activity, getCameraId(), camera!!)
        } else {
            activity.finish()
        }

        if (mOrientationEventListener == null) {
            mOrientationEventListener = object : OrientationEventListener(activity, SensorManager.SENSOR_DELAY_NORMAL) {

                override fun onOrientationChanged(orientation: Int) {

                    // determine our orientation based on sensor response
                    val lastOrientation = mOrientation

                    if (orientation >= 315 || orientation < 45) {
                        if (mOrientation != ORIENTATION_PORTRAIT_NORMAL) {
                            mOrientation = ORIENTATION_PORTRAIT_NORMAL
                        }
                    } else if (orientation < 315 && orientation >= 225) {
                        if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL) {
                            mOrientation = ORIENTATION_LANDSCAPE_NORMAL
                        }
                    } else if (orientation < 225 && orientation >= 135) {
                        if (mOrientation != ORIENTATION_PORTRAIT_INVERTED) {
                            mOrientation = ORIENTATION_PORTRAIT_INVERTED
                        }
                    } else { // orientation <135 && orientation > 45
                        if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED) {
                            mOrientation = ORIENTATION_LANDSCAPE_INVERTED
                        }
                    }

                    if (lastOrientation != mOrientation) {
                        setViewsOrientation(mOrientation)
                    }
                }
            }
        }
        if (mOrientationEventListener!!.canDetectOrientation()) {
            mOrientationEventListener!!.enable()
        }
    }

    private fun setViewsOrientation(orientation : Int) {
        var rotateAngle = 0
        when (orientation) {
            ORIENTATION_PORTRAIT_NORMAL -> rotateAngle = 0
            ORIENTATION_PORTRAIT_INVERTED -> rotateAngle = 180
            ORIENTATION_LANDSCAPE_NORMAL -> rotateAngle = 90
            ORIENTATION_LANDSCAPE_INVERTED -> rotateAngle = -90
        }
        rotateViews(rotateAngle)
    }

    private fun rotateViews(rotateAngle: Int) {
        flashToggle.animate().rotation(rotateAngle.toFloat()).start()
        cameraToggle.animate().rotation(rotateAngle.toFloat()).start()
    }

    private fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            val cameraId = getCameraId()
            c = Camera.open(cameraId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return c
    }

    private fun  getCameraId(): Int {
        return if (isFrontCamera) selfieCamId else 0
    }

    private fun getFrontCameraId(): Int {
        var camId = -1
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0..numberOfCameras - 1) {
            val info = CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                camId = i
                break
            }
        }
        return camId
    }

    override fun onPause() {
        super.onPause()
        mOrientationEventListener?.disable()
        rotateViews(0)
        if (camera != null) {
            camera!!.release()
            camera = null
        }
        cameraView.removeView(preview)
        preview = null
    }

    fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: android.hardware.Camera) {
        val info = android.hardware.Camera.CameraInfo()
        android.hardware.Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
                .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }
}

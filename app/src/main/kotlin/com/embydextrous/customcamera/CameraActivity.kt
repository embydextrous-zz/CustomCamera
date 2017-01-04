package com.embydextrous.customcamera

import android.Manifest
import android.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_permission_required.view.*

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_camera)
        checkCameraAndStoragePermission()
    }

    private fun checkCameraAndStoragePermission() {
        if(isPermissionGranted(this, permissions.asList())) {
            showCamera()
        } else {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
            val dialogHandled = arrayOf(false)
            val permissionDialogBuilder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.dialog_permission_required, null)
            permissionDialogBuilder.setView(dialogView)
            val alertDialog = permissionDialogBuilder.create()
            val button = dialogView.permissionButton
            button.setOnClickListener {
                dialogHandled[0] = true
                alertDialog.dismiss()
                ActivityCompat.requestPermissions(this, permissions, 200)
            }
            alertDialog.setOnDismissListener {
                if(!dialogHandled[0]&&!isPermissionGranted(this, permissions.asList())) {
                    Toast.makeText(applicationContext, "Please grant camera and storage permissions first.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            alertDialog.window.setWindowAnimations(R.style.MyAnimation_Window1)
            alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionGranted(this, permissions.asList()))
            showCamera()
        else {
            Toast.makeText(applicationContext, "Please grant camera and storage permissions first.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun showCamera() {
        val tag = "CameraF"
        val manager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped) {
            val ft = manager.beginTransaction()
            ft.replace(R.id.fragmentContainer, CameraFragment(), tag)
            ft.addToBackStack(tag)
            ft.commit()
        }
    }

    companion object {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun showPreview(aspect: Double, uri: String) {
        val tag = "PreviewF"
        val manager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped) {
            val ft = manager.beginTransaction()
            ft.replace(R.id.fragmentContainer, PreviewFragment.newInstance(uri, aspect), tag)
            ft.addToBackStack(tag)
            ft.commit()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount==1)
            finish()
    }
}

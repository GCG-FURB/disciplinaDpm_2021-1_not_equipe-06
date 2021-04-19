package com.furb.br.nathan.dispositivosmoveis3c

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                showCameraPermissionErrorMessage()
            }
        }

    private val registerTakePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        Log.d(this.localClassName, isSuccess.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handlePermissionRequest()
    }

    private fun handlePermissionRequest() {
        if (hasCameraPermission()) {
            return
        }
        requestPermissionLauncher.launch(PackageManager.FEATURE_CAMERA)
    }

    private fun hasCameraPermission(): Boolean {
        return checkSelfPermission(PackageManager.FEATURE_CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun showCameraPermissionErrorMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Camera permission not granted, this function won't work")

        builder.create().show()
    }

    fun dispatchTakeVideoIntent(view: View) {
        if (!hasCameraPermission()) {
            handlePermissionRequest()
            return
        }

        registerTakePictureLauncher.launch(createPictureFile())
    }

    private fun createPictureFile(): Uri {
        val file = File(fileName())

        return Uri.fromFile(file)
    }

    private fun fileName(): String = "Mobile_${Date().time}_pic.png"

}
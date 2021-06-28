package com.furb.br.nathan.dispositivosmoveis3c

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
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

    private val takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        Log.d(this.localClassName, isSuccess.toString())
    }

    private val takeVideoLauncher = registerForActivityResult(
            ActivityResultContracts.TakeVideo()
    ) { isSuccess ->

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

        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun hasCameraPermission(): Boolean {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun showCameraPermissionErrorMessage() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Camera permission not granted, this function won't work")

        builder.create().show()
    }

    fun dispatchTakePictureIntent(view: View) {
        if (!hasCameraPermission()) {
            handlePermissionRequest()
            return
        }

        takePictureLauncher.launch(createPictureFileUri())
    }

    fun dispatchTakeVideoIntent(view: View) {
        if (!hasCameraPermission()) {
            handlePermissionRequest()
            return
        }

        takeVideoLauncher.launch(createVideoFileUri())
    }

    private fun createPictureFileUri(): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName())
            put(MediaStore.MediaColumns.MIME_TYPE, "jpg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")
    }

    private fun createVideoFileUri(): Uri {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, videoFileName())
            put(MediaStore.MediaColumns.MIME_TYPE, "mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        return contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("Failed to create new MediaStore record.")
    }

    private fun imageFileName() = "pic_${fileName()}.jpg"
    private fun videoFileName() = "video_${fileName()}.mp4"
    private fun fileName() = "Mobile_${Date().time}"

}
package com.example.alertasullana.data.services

import android.graphics.Bitmap


interface CameraResultListener {
    fun onCameraResult(imageBitmap: Bitmap)
}

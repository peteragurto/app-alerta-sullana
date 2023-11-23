package com.example.alertasullana.data.services

import android.net.Uri

interface ImagenCapturaListener {
    fun onImageCaptured(imageUri: Uri)
}
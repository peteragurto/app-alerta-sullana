package com.example.alertasullana.services

/* Servicio de mensajería propia de Android */
import android.telephony.SmsManager
class ServicioSMS {

    fun sendVerificationCode(phoneNumber: String, verificationCode: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(
            phoneNumber,
            null,
            "Tu código de verificación es: $verificationCode",
            null,
            null
        )
    }
}
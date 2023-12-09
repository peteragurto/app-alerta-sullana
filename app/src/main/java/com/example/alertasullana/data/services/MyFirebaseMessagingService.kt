package com.example.alertasullana.data.services
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alertasullana.R
import com.example.alertasullana.ui.principal.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Obtener el ID del reporte del mensaje
        val reporteId = remoteMessage.data["reporteId"]

        // Crear un Intent para iniciar MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            // Pasar el ID del reporte como un extra
            putExtra("reporteId", reporteId)
            // Configurar la bandera para iniciar una nueva tarea
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Crear un PendingIntent para el Intent
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Crear la notificación
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logoalertasullana)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 123
    }
}
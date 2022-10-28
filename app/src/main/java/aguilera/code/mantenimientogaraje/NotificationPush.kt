package aguilera.code.mantenimientogaraje

import aguilera.code.mantenimientogaraje.data.db.entity.Concepto
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationPush : Service() {

    private var CHANNEL_ID = "channel_garage"
    private var notificationId = 0

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.e("miapp", "Servicio creado...")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("miapp", "Servicio iniciado...")
        //Lanzar notificaciones Push
        val concept = intent?.getSerializableExtra("concept") as Concepto
        notificationId = intent.getIntExtra("notificationId", 0)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //val bitmap= BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_repair_gear)
        val bitmapLargeIcon=BitmapFactory.decodeResource(applicationContext.resources,R.drawable.ic_repair_gear)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.garage)
            .setContentTitle("${concept.matricula}")
            .setContentText("Tiene un recordatorio pendiente de: ${concept.concepto}")
            .setLargeIcon(bitmapLargeIcon)
            //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            //.setStyle(NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line so we extended it to be much much longer") )
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.e("miapp", "Servicio destruido...")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
package edu.usna.mobileos.p_ramarosonallan

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

//for API >= 26 (Android 8.0 - Oreo), create an actual channel with the provided ID
fun createNotificationChannel(context: Context, channelId: String) {
    // Create the NotificationChannel, but only on API 26+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "IT472 channel"
        val descriptionText = "channel for IT472 notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        channel.description = descriptionText

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun generateNotification(
    context: Context, title: String,
    message: String, notificationId: Int, channelId: String = "IT472 Channel"
)
{

    //build notification
    var mBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.crest)
        .setContentTitle(title)
        .setContentText(message)


    //set high priority so the notifications will appear as floating window on top of screen
    mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))




    // intent to specify which activity to launch when the
    // notification is selected
    val notificationIntent = Intent(
        context,
        MainActivity::class.java
    )

    // set intent so it does not start a new activity
    notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
            or Intent.FLAG_ACTIVITY_SINGLE_TOP)

    // pending intent to allow the system to launch the activity
    // inside our app from the notification
    val pendingIntent = PendingIntent.getActivity(
        context, 0,
        notificationIntent, 0
    )
    mBuilder.setContentIntent(pendingIntent)

    // set notification to cancel itself when selected
// as opposed to canceling it manually
    mBuilder.setAutoCancel(true)



    /*   mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND)
       mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE)
       mBuilder.setDefaults(NotificationCompat.DEFAULT_LIGHTS)

       mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
   */
//    mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)

    /* mBuilder.setSound(
         Uri.parse(
             "android.resource://" +
                     context.getPackageName() + "/raw/lefreak"
         )
     )
 */
    // customize vibration pattern
    // delay 50ms delay, vibrate 600ms, pause 500ms, vibrate 1200ms
    //mBuilder.setVibrate(longArrayOf(50, 600, 500, 1200))

    // get instance of notification manager
    val notificationManager = NotificationManagerCompat.from(context)

    //send notification
    notificationManager.notify(notificationId, mBuilder.build())
}

fun cancelNotification(context: Context, notificationId: Int){
    NotificationManagerCompat.from(context).cancel(notificationId)
}
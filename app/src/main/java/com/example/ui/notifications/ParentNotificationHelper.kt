package com.example.ui.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

object ParentNotificationHelper {
    const val CHANNEL_ID = "parent_control_channel"
    const val CHANNEL_NAME = "Contrôle Parental & Validations"
    const val CHANNEL_DESC = "Notifications directes lors des validations de routines et demandes des enfants"

    fun initChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun sendMissionValidationNotification(
        context: Context,
        childName: String,
        childAge: Int,
        missionTitle: String,
        points: Int,
        requiresApproval: Boolean
    ) {
        try {
            initChannel(context)
            if (!hasNotificationPermission(context)) return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("OPEN_PARENT_TAB", "DASHBOARD")
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = if (requiresApproval) {
                "🛡️ Validation requise : $childName ($childAge ans)"
            } else {
                "🐝 Routine validée : $childName ($childAge ans)"
            }

            val text = if (requiresApproval) {
                "$childName a terminé '$missionTitle' ! Touchez pour valider et accorder +$points miel 🍯."
            } else {
                "$childName a terminé avec succès '$missionTitle' (+$points miel 🍯)."
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(0, 250, 150, 250))
                .build()

            NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 100000).toInt(), notification)
        } catch (e: Exception) {
            // Graceful fallback if notifications are blocked or restricted
        }
    }

    fun sendRewardRequestNotification(
        context: Context,
        childName: String,
        childAge: Int,
        rewardTitle: String,
        costPoints: Int
    ) {
        try {
            initChannel(context)
            if (!hasNotificationPermission(context)) return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("OPEN_PARENT_TAB", "REWARDS")
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                (System.currentTimeMillis() + 1).toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val title = "🎁 Demande de récompense : $childName ($childAge ans)"
            val text = "$childName souhaite débloquer '$rewardTitle' ($costPoints miel 🍯). Touchez pour accorder le cadeau !"

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(0, 200, 100, 200))
                .build()

            NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 100000).toInt(), notification)
        } catch (e: Exception) {
            // Graceful fallback
        }
    }

    fun sendTestNotification(context: Context) {
        try {
            initChannel(context)
            if (!hasNotificationPermission(context)) return

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                42,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("🔔 Contrôle Parental Opérationnel !")
                .setContentText("Votre téléphone recevra bien une alerte à chaque validation de routine de vos enfants.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            NotificationManagerCompat.from(context).notify(101, notification)
        } catch (e: Exception) {
            // Graceful fallback
        }
    }
}

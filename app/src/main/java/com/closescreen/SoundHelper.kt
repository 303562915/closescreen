package com.closescreen

import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * 静音 = 切到「勿扰 / 无打扰」，需要用户在设置里授予「勿扰访问」。
 * 没授权也不影响关屏，只是不会静音。
 */
object SoundHelper {

    fun notificationManager(ctx: Context): NotificationManager? =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

    fun canSilence(ctx: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        return notificationManager(ctx)?.isNotificationPolicyAccessGranted == true
    }

    fun setSilence(ctx: Context, on: Boolean): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
        val nm = notificationManager(ctx) ?: return false
        if (!nm.isNotificationPolicyAccessGranted) return false
        return try {
            nm.setInterruptionFilter(
                if (on) NotificationManager.INTERRUPTION_FILTER_NONE
                else NotificationManager.INTERRUPTION_FILTER_ALL
            )
            true
        } catch (t: Throwable) {
            false
        }
    }
}

package com.closescreen

import android.accessibilityservice.AccessibilityService
import android.os.Build

/**
 * 关屏的唯一入口。真正的动作在无障碍服务里执行，
 * 所以这里只是持有它的引用 + 做能力判断。
 */
object LockHelper {

    @Volatile
    var service: LockAccessibilityService? = null

    /** 系统版本够 + 无障碍服务连着，才可能关屏。 */
    fun canLock(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && service != null

    /** 触发锁屏。成功返回 true。 */
    fun lock(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        val s = service ?: return false
        return try {
            s.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
        } catch (t: Throwable) {
            false
        }
    }
}

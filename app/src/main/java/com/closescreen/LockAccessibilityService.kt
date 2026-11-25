package com.closescreen

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * 只为了拿到 performGlobalAction 的能力，不读取任何界面内容。
 * 服务配置里 canRetrieveWindowContent=false。
 */
class LockAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        LockHelper.service = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不需要监听内容
    }

    override fun onInterrupt() {
        // 不需要
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        LockHelper.service = null
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        LockHelper.service = null
        super.onDestroy()
    }
}

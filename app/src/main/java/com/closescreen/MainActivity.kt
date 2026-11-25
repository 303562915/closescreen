package com.closescreen

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    companion object {
        const val PREFS = "closescreen"
        const val PREF_MUTE = "mute_on_lock"
    }

    private lateinit var statusView: TextView
    private lateinit var muteCheck: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(28), dp(20), dp(28))
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 22f
        }

        statusView = TextView(this).apply {
            textSize = 14f
            setPadding(0, dp(16), 0, dp(8))
        }

        muteCheck = CheckBox(this).apply {
            text = getString(R.string.opt_mute)
            isChecked = prefs().getBoolean(PREF_MUTE, true)
            setOnCheckedChangeListener { _, value ->
                prefs().edit().putBoolean(PREF_MUTE, value).apply()
            }
        }

        root.addView(title)
        root.addView(statusView)
        root.addView(muteCheck)
        root.addView(button(getString(R.string.btn_lock_now)) { lockNow() })
        root.addView(button(getString(R.string.btn_enable_accessibility)) { openAccessibilitySettings() })
        root.addView(button(getString(R.string.btn_grant_dnd)) { openDndSettings() })
        root.addView(button(getString(R.string.btn_unmute)) {
            SoundHelper.setSilence(this, false)
            refreshStatus()
        })

        root.addView(TextView(this).apply {
            text = getString(R.string.status_tip)
            textSize = 13f
            setPadding(0, dp(20), 0, 0)
        })

        setContentView(ScrollView(this).apply { addView(root) })
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun lockNow() {
        if (prefs().getBoolean(PREF_MUTE, true)) {
            SoundHelper.setSilence(this, true)
        }
        val ok = LockHelper.lock()
        val msg = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.P -> getString(R.string.toast_need_api28)
            ok -> getString(R.string.toast_locked)
            else -> getString(R.string.toast_no_accessibility)
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun openAccessibilitySettings() {
        try {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        } catch (t: Throwable) {
            Toast.makeText(this, t.message ?: "无法打开设置", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDndSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "系统版本太低", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        } catch (t: Throwable) {
            Toast.makeText(this, t.message ?: "无法打开设置", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshStatus() {
        val a11y = isAccessibilityEnabled()
        val dnd = SoundHelper.canSilence(this)
        statusView.text = buildString {
            append(if (a11y) getString(R.string.status_accessibility_on) else getString(R.string.status_accessibility_off))
            append("\n")
            append(if (dnd) getString(R.string.status_dnd_on) else getString(R.string.status_dnd_off))
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val target = LockAccessibilityService::class.java.name
        val enabled = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (info in enabled) {
            val name = info.resolveInfo?.serviceInfo?.name ?: continue
            if (name == target) return true
        }
        return false
    }

    private fun button(text: String, onClick: () -> Unit): Button =
        Button(this).apply {
            setText(text)
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12) }
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun prefs() = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}

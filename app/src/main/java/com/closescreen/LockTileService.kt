package com.closescreen

import android.os.Build
import android.service.quicksettings.TileService
import android.widget.Toast

/**
 * 下拉快捷开关里的一格「关屏」，点一下 = 关屏（可带静音）。
 * 这是日常最方便的入口。
 */
class LockTileService : TileService() {

    override fun onClick() {
        super.onClick()

        val prefs = getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE)
        if (prefs.getBoolean(MainActivity.PREF_MUTE, true)) {
            SoundHelper.setSilence(this, true)
        }

        val ok = LockHelper.lock()
        if (!ok) {
            val msg = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                getString(R.string.toast_need_api28)
            } else {
                getString(R.string.toast_no_accessibility)
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}

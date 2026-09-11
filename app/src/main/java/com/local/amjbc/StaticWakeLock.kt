package com.local.amjbc

import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.WakeLock

/**
 * Created by sharjeelhussain on 15-11-12.
 */
object StaticWakeLock {
    private var wl: WakeLock? = null

    @JvmStatic
    fun lockOn(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        //Object flags;
        if (wl == null) wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "com.local.amjbc:MATH_ALARM")

        wl!!.acquire()
    }

    @JvmStatic
    fun lockOff(context: Context?) {
//		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            if (wl != null) wl!!.release()
        } catch (e: Exception) {
            //e.printStackTrace();
        }
    }
}

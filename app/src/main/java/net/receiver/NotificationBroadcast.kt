package net.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBroadcast:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

    }

    fun ComponentName(): String? {
        return this.javaClass.name
    }
}
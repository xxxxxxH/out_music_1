package net.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.Services.MusicService
import net.general.GlobalApp

/**
 * Copyright (C) 2021,2021/12/7, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (GlobalApp.sharedpreferences!!.getString(GlobalApp.SLEEP_TIMER, "false").equals("true")) {
            if (GlobalApp.isServiceRunning(MusicService::class.java.name, context)) {
                context.sendBroadcast(Intent(GlobalApp.TIME_UP))
            }
        }
    }
}
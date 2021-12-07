package net.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.KeyEvent
import net.Services.MusicService
import net.general.GlobalApp

class NotificationBroadcast : BroadcastReceiver() {
    var playIntent: Intent? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (!GlobalApp.isServiceRunning(MusicService::class.java.name, context)) {
            playIntent = Intent(context, MusicService::class.java)
            context.startService(playIntent)
        }
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(context)
        }
        if (intent.action == Intent.ACTION_MEDIA_BUTTON) {
            val keyEvent = intent.extras!![Intent.EXTRA_KEY_EVENT] as KeyEvent?
            if (keyEvent != null) {
                if (keyEvent.action != KeyEvent.ACTION_DOWN) return
                when (keyEvent.keyCode) {
                    KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> context.sendBroadcast(
                        Intent(GlobalApp.BROADCAST_PLAYPAUSE)
                    )
                    KeyEvent.KEYCODE_MEDIA_PLAY -> {
                    }
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    }
                    KeyEvent.KEYCODE_MEDIA_STOP -> {
                        try {
                            val editor: SharedPreferences.Editor =
                                GlobalApp.sharedpreferences!!.edit()
                            editor.putBoolean(GlobalApp.IS_SHUFFLE, false)
                            editor.putBoolean(GlobalApp.IS_REPEAT, false)
                            editor.commit()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        if (MusicService.mNotifyManager == null) {
                            MusicService.mNotifyManager =
                                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            MusicService.mNotifyManager.cancel(MusicService.NOTIFICATION_ID)
                        }
                        val musicService = MusicService()
                        musicService.stopForeground(true)
                        musicService.stopSelf()
                    }
                    KeyEvent.KEYCODE_MEDIA_NEXT -> context.sendBroadcast(Intent(GlobalApp.BROADCAST_NEXT))
                    KeyEvent.KEYCODE_MEDIA_PREVIOUS -> context.sendBroadcast(Intent(GlobalApp.BROADCAST_PREV))
                }
            } else {
                try {
                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                    editor.putBoolean(GlobalApp.IS_SHUFFLE, false)
                    editor.putBoolean(GlobalApp.IS_REPEAT, false)
                    editor.commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (MusicService.mNotifyManager == null) {
                    MusicService.mNotifyManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    MusicService.mNotifyManager.cancel(MusicService.NOTIFICATION_ID)
                }
                val musicService = MusicService()
                musicService.stopForeground(true)
                musicService.stopSelf()
            }
        } else {
            if (intent.action == MusicService.NOTIFY_PLAY) {
                context.sendBroadcast(Intent(GlobalApp.BROADCAST_PLAYPAUSE))
            } else if (intent.action == MusicService.NOTIFY_PAUSE) {
                context.sendBroadcast(Intent(GlobalApp.BROADCAST_PAUSE))
            } else if (intent.action == MusicService.NOTIFY_NEXT) {
                context.sendBroadcast(Intent(GlobalApp.BROADCAST_NEXT))
            } else if (intent.action == MusicService.NOTIFY_DELETE) {
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                try {
                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                    editor.putBoolean(GlobalApp.IS_SHUFFLE, false)
                    editor.putBoolean(GlobalApp.IS_REPEAT, false)
                    editor.commit()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (MusicService.mNotifyManager == null) {
                    MusicService.mNotifyManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                }
                val i = Intent(context, MusicService::class.java)
                context.stopService(i)
                MusicService.stopService()
            } else if (intent.action == MusicService.OPEN_AUDIO_PLAYER) {
            } else if (intent.action == MusicService.NOTIFY_PREVIOUS) {
                context.sendBroadcast(Intent(GlobalApp.BROADCAST_PREV))
            }
        }
    }

    fun ComponentName(): String? {
        return this.javaClass.name
    }
}
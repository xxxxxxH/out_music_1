package net.general

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.*
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Html
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import net.Controls.MusicPlayerControls
import net.DataBase.OpenHelper
import net.Services.MusicService
import net.adapter.PlaylistDetailAdapter
import net.adapter.VideoAdapter
import net.basicmodel.BuildConfig
import net.basicmodel.R
import net.basicmodel.VideoPlayerActivity
import net.event.MessageEvent
import net.model.*
import net.utils.VideoUtils
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

object GlobalApp {

    val MAIN_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/MyMusicPlayer"
    var downloadPath = MAIN_DIRECTORY + "Whatsapp Downloaded Status"
    var statuesPath =
        Environment.getExternalStoragePublicDirectory("/WhatsApp/Media/.Statuses").absolutePath
    //----------------- equilizer ------------------------------------------------------------------

    //----------------- equilizer ------------------------------------------------------------------
    const val BAND1 = "com.myphoto.musicplayer.band1"
    const val BAND2 = "com.myphoto.musicplayer.band2"
    const val BAND3 = "com.myphoto.musicplayer.band3"
    const val BAND4 = "com.myphoto.musicplayer.band4"
    const val BAND5 = "com.myphoto.musicplayer.band5"
    const val BAND6 = "com.myphoto.musicplayer.band6"
    const val BAND7 = "com.myphoto.musicplayer.band7"
    const val BAND8 = "com.myphoto.musicplayer.band8"
    const val BASS_BOOST = "com.myphoto.musicplayer.bass_boost"
    const val IS_EQUALIZER = "com.myphoto.musicplayer.is_equalizer"

    //------------------- shared object-------------------------------------------------------------

    //------------------- shared object-------------------------------------------------------------
    const val TIME_UP = "com.music.time_up"
    const val ACTION_SLEEP_TIMER_CODE = 1234

    const val BROADCAST_PLAYPAUSE = "musicplayer.action.BROADCAST_PLAYPAUSE"
    const val BROADCAST_PREV = "musicplayer.action.BROADCAST_PREV"
    const val BROADCAST_PAUSE = "musicplayer.action.BROADCAST_PAUSE"
    const val BROADCAST_NEXT = "musicplayer.action.BROADCAST_NEXT"


    var SONGNUMBER = "songnumber"
    var SLEEP_TIMER = "sleeptimer"
    var SLEEP_HOUR = "sleephour"
    var SLEEP_MINIT = "sleepminit"
    var IS_REPEAT = "is_repeat"
    var IS_SHUFFLE = "is_shuffle"
    var sharedpreferences: SharedPreferences? = null
    var PREFREANCE_LAST_SONG_KEY = "SONGNUMBER"
    var PREFREANCE_MAIN_IMAGE_BACKGROUND = "MAINCUSTOMEBACKGROUND"
    var PREFREANCE_MAIN_DEFAULT_BACKGROUND = "MAINBACKGROUND"

    var TRANCPARENT_COLOR = "TRANCPARENTCOLOR"
    var TRANCPARENT_COLOR_DEFAULT_VALUE = 0 * 0x03000000 + 0x000000 // full transparent

    var TRANCPARENT_COLOR_SEEKBAR_POS = "TRANCPARENTCOLORSEEKBARPOS"
    var BLUR_SEEKBAR_POS = "BLURSEEKBARPOS"
    var ADSCOUNT = "adscount"
    var RATE_US = "rate_us"
    var APPUSEDCOUNT = "appusedcount"
    var LAST_SONG: String? = null


    var break_insert_queue = false
    var ads_count = 0

    var ads_total_count = 10
    var video_ads_count = 2

    var is_grid = false


    var mediaItemsArrayList: ArrayList<SongsModel> = ArrayList<SongsModel>()
    var integerArrayList = ArrayList<Int>()
    var flakeArrayList = ArrayList<Int>()
    var integerArrayList_small = ArrayList<Int>()
    var songsArrayList: ArrayList<SongsModel> = ArrayList<SongsModel>()
    var generArrayList: ArrayList<GenersModel> = ArrayList<GenersModel>()
    var ringArrayList: ArrayList<SongsModel> = ArrayList<SongsModel>()
    var recordingArrayList: ArrayList<SongsModel> = ArrayList<SongsModel>()
    var artistArrayList: List<ArtistModel> = ArrayList<ArtistModel>()
    var albumArrayList: List<AlbumModel> = ArrayList<AlbumModel>()

    var str_array: IntArray? = null


    var activity: Activity? = null
    var global: GlobalApp? = null
    var openHelper: OpenHelper? = null
    fun sharedInstance(ac: Activity?): GlobalApp? {
        activity = ac
        if (global == null) {
            global = GlobalApp
        }
        return global
    }


    fun getImgUri(album_id: Long): Uri? {
        return try {
            ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                album_id
            )
        } catch (e: Exception) {
            null
        }
    }

    fun getDuration(milliseconds: Long): String? {
        val sec = milliseconds / 1000 % 60
        val min = milliseconds / (60 * 1000) % 60
        val hour = milliseconds / (60 * 60 * 1000)
        val s = if (sec < 10) "0$sec" else "" + sec
        val m = if (min < 10) "0$min" else "" + min
        val h = "" + hour
        var time = ""
        time = if (hour > 0) {
            "$h:$m:$s"
        } else {
            "$m:$s"
        }
        return time
    }


    fun currentVersionSupportLockScreenControls(): Boolean {
        val sdkVersion = Build.VERSION.SDK_INT
        return sdkVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
    }

    fun isServiceRunning(serviceName: String, context: Context): Boolean {
        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceName == service.service.className) {
                    return true
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
        return false
    }

    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var totalDuration = totalDuration
        var currentDuration = 0
        totalDuration = (totalDuration / 1000)
        currentDuration = (progress.toDouble() / 100 * totalDuration).toInt()
        // return current duration in milliseconds
        return currentDuration * 1000
    }


    fun showPopUp(view: View?, context: Context, activity: Activity, mediaItems: SongsModel) {
        val popupMenu = PopupMenu(activity, view)
        try {
            val fields = popupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[popupMenu]
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val menuInflater = popupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_menu_music, popupMenu.menu)
        popupMenu.show()
        val openHelper: OpenHelper = OpenHelper.sharedInstance(context)
        val ifPlaylistsongExist: Boolean = openHelper.isPlaylist(mediaItems.song_id)
        val ifQueuesongExist: Boolean = openHelper.isQueuelist(mediaItems.song_id)
        val fileSize: String = mediaItems.size
        if (ifPlaylistsongExist == false) {
            popupMenu.menu.getItem(1).title = activity.resources.getString(R.string.add_to_playlist)
        } else {
            popupMenu.menu.getItem(1).title =
                activity.resources.getString(R.string.remove_from_playlist)
        }
        if (ifQueuesongExist == false) {
            popupMenu.menu.getItem(0).title = activity.resources.getString(R.string.add_to_queue)
        } else {
            popupMenu.menu.getItem(0).title =
                activity.resources.getString(R.string.remove_from_queue)
        }
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.title == activity.resources.getString(R.string.add_to_queue)) {
                openHelper.insertQueue(
                    mediaItems.song_id,
                    mediaItems.img_uri.toString() + "",
                    mediaItems.title,
                    mediaItems.path,
                    mediaItems.artist,
                    mediaItems.size
                )
                mediaItemsArrayList.add(mediaItems)
//                SongsMainFragment.fillQueueAdapter()
                EventBus.getDefault().post(MessageEvent("fillQueueAdapter"))
                if (mediaItemsArrayList.size == 1) {
                    if (MusicService.player != null) {
                        MusicService.player.reset()
                    }
                    MusicPlayerControls.startSongsWithQueue(
                        context,
                        mediaItemsArrayList,
                        0,
                        "addqueue"
                    )
                }
            } else if (item.title == activity.resources.getString(R.string.remove_from_queue)) {
                val pos =
                    mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].song_id
                if (mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].song_id != mediaItems.song_id) {
                    openHelper.deleteQueueSong(mediaItems.song_id)
                    mediaItemsArrayList = openHelper.getQueueData(context)
//                    SongsMainFragment.queueAdapter.notifyDataSetChanged()

//                    SongsMainFragment.viewpageSwipePagerAdapter.notifyDataSetChanged()
                    EventBus.getDefault().post(MessageEvent("notifyDataSetChanged"))
                    try {
                        if (pos > 0) {
                            var i = 0
                            for (mediaItem in mediaItemsArrayList) {
                                if (mediaItem.song_id == pos) {
                                    MusicPlayerControls.SONG_NUMBER = i
                                    val editor = sharedpreferences!!.edit()
                                    editor.putString(
                                        PREFREANCE_LAST_SONG_KEY,
                                        MusicPlayerControls.SONG_NUMBER.toString() + ""
                                    )
                                    editor.putString(
                                        "songId",
                                        mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].toString() + ""
                                    )
                                    editor.commit()
                                    val editor1 = sharedpreferences!!.edit()
                                    editor1.putInt(
                                        SONGNUMBER,
                                        MusicPlayerControls.SONG_NUMBER
                                    )
                                    editor1.commit()
                                    break
                                }
                                i++
                            }
                        } else {
                            MusicPlayerControls.SONG_NUMBER = 0
//                            SongsMainFragment.changeUi(MusicPlayerControls.SONG_NUMBER) // preference nakhva nu baki 6.
                            EventBus.getDefault().post(MessageEvent("changeUi"))
                            val editor = sharedpreferences!!.edit()
                            editor.putString(
                                PREFREANCE_LAST_SONG_KEY,
                                MusicPlayerControls.SONG_NUMBER.toString() + ""
                            )
                            editor.putString(
                                "songId",
                                mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].toString() + ""
                            )
                            editor.commit()
                            val editor1 = sharedpreferences!!.edit()
                            editor1.putInt(SONGNUMBER, MusicPlayerControls.SONG_NUMBER)
                            editor1.commit()
                        }
//                        SongsMainFragment.queueAdapter.clearSelection()
                        EventBus.getDefault().post(MessageEvent("clearSelection"))
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                    if (mediaItemsArrayList.size == 0) {
                        try {
//                            if (SongsMainFragment.playerSlidingUpPanelLayout != null) {
//                                SongsMainFragment.playerSlidingUpPanelLayout.setPanelState(
//                                    SlidingUpPanelLayout.PanelState.HIDDEN
//                                )
//                            }
                            EventBus.getDefault().post(MessageEvent("setPanelState"))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    Toast.makeText(context, "Song removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "song currently playing", Toast.LENGTH_SHORT).show()
                }
            } else if (item.title == activity.resources.getString(R.string.proparty)) {
                propertyDialog(activity, mediaItems.path, fileSize)
            } else if (item.title == activity.resources.getString(R.string.add_to_playlist)) {
                GeneralFunction.songAddToPlaylist(activity, mediaItems)
            } else if (item.title == activity.resources.getString(R.string.remove_from_playlist)) {
                openHelper.deletePlayListSong(mediaItems.song_id)
                if (PlaylistDetailAdapter.dataSet != null) {
//                    PlaylistDetailAdapter.dataSet =
//                        openHelper.getPlayListData(PlaylistDetaillFragment.playlistId, context)
//                    PlaylistDetaillFragment.playlistDetailAdapter.notifyDataSetChanged()
                    EventBus.getDefault().post(MessageEvent("PlaylistDetaillFragment"))
                }
            } else if (item.title == activity.resources.getString(R.string.ring_tone_cutter)) {
                if (isServiceRunning(MusicService::class.java.name, context)) {
                    Handler().postDelayed({
                        if (MusicService.player != null) {
                            if (MusicService.isPng()) {
                                try {
                                    MusicService.player.pause()
                                } catch (e: Exception) {
                                }
                            }
                        }
                    }, 100)
                }
                val intent = Intent(
                    "android.intent.action.EDIT", Uri.parse( /*"file:///" + */
                        mediaItems.path
                    )
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val equals = intent.action == "android.intent.action.GET_CONTENT"
                intent.putExtra("was_get_content_intent", equals)
                intent.setClassName(
                    BuildConfig.APPLICATION_ID,
                    "com.ringdroid.RingdroidEditActivity"
                )
                context.startActivity(intent)
            } else if (item.title == activity.resources.getString(R.string.share)) {
                val share = Intent(Intent.ACTION_SEND)
                share.type = "audio/*"
                share.putExtra(
                    Intent.EXTRA_STREAM, Uri.parse( /*"file:///" +*/
                        mediaItems.path
                    )
                )
                activity.startActivity(Intent.createChooser(share, "Share Music"))
            } else if (item.title == activity.resources.getString(R.string.set_ring_tone)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(context)) {
                        // Do stuff here
                        setRingTone(context, mediaItems, activity)
                    } else {
                        showManageSettingPermissionDialog(activity)
                    }
                }
            }
            false
        }
    }

    fun propertyDialog(activity: Activity, path: String?, size: String) {
        val inflater = LayoutInflater.from(activity.applicationContext)
        val view: View = inflater.inflate(R.layout.poperty_alert_dialog, null, false)
        val txt_path = view.findViewById<View>(R.id.txt_path) as TextView
        val txt_size = view.findViewById<View>(R.id.txt_size) as TextView
        txt_path.text = path
        try {
            txt_size.text = toNumInUnits(size.toLong())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        val propertyAlertBuilder = AlertDialog.Builder(activity)
        propertyAlertBuilder.setTitle("Property")
        propertyAlertBuilder.setView(view)
        propertyAlertBuilder.setPositiveButton(Html.fromHtml("<font color='black'>OK</font>"), null)
        propertyAlertBuilder.show()
    }

    fun toNumInUnits(bytes: Long): String? {
        var bytes = bytes
        var u = 0
        while (bytes > 1024 * 1024) {
            u++
            bytes = bytes shr 10
        }
        if (bytes > 1024) u++
        return String.format("%.1f %cB", bytes / 1024f, " kMGTPE"[u])
    }

    fun changeStatusBarColor(activity: Activity, color: Int) {
        val window = activity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = color
        }
    }

    //----------------set sleeper time----------------------------------

    //----------------set sleeper time----------------------------------
    fun savePrefrence(context: Context) {
        sharedpreferences = context.getSharedPreferences("preference", Context.MODE_PRIVATE)
    }

    var fragmentTransaction: FragmentTransaction? = null

    fun fragmentReplaceTransition(fragment: Fragment?, fragment_name: String?, activity: Activity) {
        fragmentTransaction =
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.framlayout_main, fragment!!, fragment_name)
        fragmentTransaction!!.addToBackStack(fragment_name)
        fragmentTransaction!!.commit()
    }

    fun fragmentReplaceTransitionSetting(
        fragment: Fragment?,
        fragment_name: String?,
        activity: Activity
    ) {
        fragmentTransaction =
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.setting_fragment, fragment!!, fragment_name)
        if (fragment_name != null) {
            fragmentTransaction!!.addToBackStack(fragment_name)
        }
        fragmentTransaction!!.commit()
    }

    fun fragmentReplaceTransitionVideo(
        fragment: Fragment?,
        fragment_name: String?,
        activity: Activity
    ) {
        fragmentTransaction =
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.video_fragment, fragment!!, fragment_name)
        if (fragment_name != null) {
            fragmentTransaction!!.addToBackStack(fragment_name)
        }
        fragmentTransaction!!.commit()
    }

    fun fragmentReplaceTransitionCommon(
        fragment: Fragment?,
        fragment_name: String?,
        activity: Activity
    ) {
        fragmentTransaction =
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
        fragmentTransaction!!.replace(R.id.framelayout, fragment!!, fragment_name)
        fragmentTransaction!!.commit()
    }

    fun isConnectingToInternet(context: Context): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }

    fun setRingTone(mContext: Context, mediaItems: SongsModel, activity: Activity) {
        val ringtoneFile: File = File(mediaItems.path)
        val content = ContentValues()
        content.put("_data", ringtoneFile.absolutePath)
        content.put("title", mediaItems.title)
        content.put("mime_type", "audio/*")
        content.put("duration", mediaItems.duration)
        content.put("is_ringtone", java.lang.Boolean.valueOf(true))
        content.put("is_notification", java.lang.Boolean.valueOf(true))
        content.put("is_alarm", java.lang.Boolean.valueOf(true))
        content.put("is_music", java.lang.Boolean.valueOf(true))
        val uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.absolutePath)
        mContext.contentResolver.delete(uri!!, "_data=\"" + ringtoneFile.absolutePath + "\"", null)
        RingtoneManager.setActualDefaultRingtoneUri(
            mContext.applicationContext, 1, mContext.contentResolver.insert(
                uri, content
            )
        )
        Toast.makeText(activity.applicationContext, "Ringtone set successfully", Toast.LENGTH_SHORT)
            .show()
    }


    fun showManageSettingPermissionDialog(activity: Activity) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Permission Dialog")
        builder.setMessage("For set ringtone we require ACTION_MANAGE_WRITE_SETTINGS permission, so Please give permission once and try again!")
        builder.setCancelable(false)
        builder.setPositiveButton(
            Html.fromHtml("<font color='black'>OK</font>")
        ) { dialog, which -> // positive button logic
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + activity.packageName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)
        }
        builder.setNegativeButton(
            Html.fromHtml("<font color='black'>CANCEL</font>")
        ) { dialog, which ->
            // negative button logic
        }
        val dialog = builder.create()
        dialog.show()
    }

    //// ------------------ video converter -------------------------

    //// ------------------ video converter -------------------------
    fun convert(miliSeconds: Long): String? {
        val hrs = TimeUnit.MILLISECONDS.toHours(miliSeconds).toInt() % 24
        val min = TimeUnit.MILLISECONDS.toMinutes(miliSeconds).toInt() % 60
        val sec = TimeUnit.MILLISECONDS.toSeconds(miliSeconds).toInt() % 60
        return String.format("%02d:%02d:%02d", hrs, min, sec)
    }

    fun filesize(file_path: String?): String? {
        val file = File(file_path)
        val length = file.length().toDouble()

        //   length = length / 1024;
        return formatFileSize(length)
    }

    fun formatFileSize(size: Double): String? {
        var hrSize: String? = null
        val k = size / 1024.0
        val m = k / 1024.0
        val g = m / 1024.0
        val t = g / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (t > 1) {
            dec.format(t) + " TB"
        } else if (g > 1) {
            dec.format(g) + " GB"
        } else if (m > 1) {
            dec.format(m) + " MB"
        } else if (k > 1) {
            dec.format(k) + " KB"
        } else {
            dec.format(size) + " Bytes"
        }
        return hrSize
    }

    fun popupWindow(
        view: View?,
        act: Activity,
        vidModel: Videos,
        pos: Int,
        callFrom: String,
        videoAdapter: VideoAdapter
    ) {
        val mPopupMenu = PopupMenu(act, view, Gravity.BOTTOM)
        try {
            val fields = mPopupMenu.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field[mPopupMenu]
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon",
                        Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val menuInflater = mPopupMenu.menuInflater
        menuInflater.inflate(R.menu.popup_menu_video, mPopupMenu.menu)
        mPopupMenu.show()
        mPopupMenu.setOnMenuItemClickListener { item ->
            if (item.title == "Watch") {
                val intent = Intent(act, VideoPlayerActivity::class.java)
                intent.putExtra("url", vidModel.filePath)
                intent.putExtra("name", vidModel.title)
                act.startActivity(intent)
            } else if (item.title == "Delete") {
                deleteVideo(act, vidModel, callFrom, videoAdapter)
            } else if (item.title == "Property") {
                PropertyDialog(act, vidModel)
            } else if (item.title == "Rename") {
                try {
                    renameVideo(vidModel, act, callFrom, videoAdapter)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (item.title == "Share") {
                val videoURI = FileProvider.getUriForFile(
                    act, BuildConfig.APPLICATION_ID.toString() + ".provider",
                    File(vidModel.filePath)
                )
                val share = Intent(Intent.ACTION_SEND)
                share.type = "video/*"
                share.putExtra(Intent.EXTRA_STREAM, videoURI)
                act.startActivity(Intent.createChooser(share, "Share Video"))
            }
            false
        }
    }

    fun PropertyDialog(activity: Activity, videoModel: Videos) {
        val inflater = LayoutInflater.from(activity.applicationContext)
        val view: View = inflater.inflate(R.layout.property_video_dialog, null, false)
        val txt_path = view.findViewById<View>(R.id.text_path_value) as TextView
        val txt_size = view.findViewById<View>(R.id.text_video_size_value) as TextView
        val txt_resolution = view.findViewById<View>(R.id.text_resolution_value) as TextView
        val txt_date = view.findViewById<View>(R.id.text_date_value) as TextView
        val txt_duration = view.findViewById<View>(R.id.text_duration) as TextView
        txt_path.text = videoModel.filePath
        txt_date.text = dateformate(videoModel.date)
        txt_resolution.text = videoModel.resolution
        txt_size.text = VideoUtils.filesize(videoModel.filePath)
        txt_duration.text = convert(videoModel.duration.toLong())
        val propertyAlertBuilder = androidx.appcompat.app.AlertDialog.Builder(activity)
        propertyAlertBuilder.setTitle("Property")
        propertyAlertBuilder.setView(view)
        propertyAlertBuilder.setPositiveButton("OK", null)
        propertyAlertBuilder.show()
    }

    fun deleteVideo(
        ctx: Activity?,
        videoModel: Videos,
        call_from: String?,
        videoAdapter: VideoAdapter
    ) {
        val adb = androidx.appcompat.app.AlertDialog.Builder(
            ctx!!
        )
        val deleteBody = "Delete File"
        adb.setTitle(deleteBody)
        adb.setMessage(
            Html.fromHtml(
                "The following video will be deleted permanentily <br><br>" + "<font color='#515151' size='12px'>" + videoModel.filePath.substring(
                    videoModel.filePath.lastIndexOf("/") + 1
                ) + "</font>"
            )
        ) //"The Following video will be deleted permanentily" + "\n\n" +videoModel.filePath);
        adb.setPositiveButton(
            "Delete"
        ) { dialog, which ->
            try {
                val result: Boolean =
                    VideoUtils.removeMedia(ctx, videoModel._id, videoModel.filePath)
                if (result) {
                    if (VideoAdapter.dataSet.contains(videoModel)) {
                        val i: Int = VideoAdapter.dataSet.indexOf(videoModel)
                        VideoAdapter.dataSet.remove(videoModel)
                        videoAdapter.notifyDataSetChanged()
                    }
                    Toast.makeText(ctx, "video remove successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(ctx, "video can't delete!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(ctx, "video can't delete!", Toast.LENGTH_SHORT).show()
            }
        }
        adb.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        val alert = adb.create()
        alert.show()
    }

    fun renameVideo(
        track: Videos,
        activity: Activity,
        call_from: String,
        videoAdapter: VideoAdapter
    ) {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.rename_video_dialog, null)
        val inputTitle = view.findViewById<View>(R.id.titleEdit) as EditText
        try {
            val title: String = track.title
            inputTitle.setText(title)
            inputTitle.setSelection(title.length)
        } catch (e: Exception) {
        }
        val alert = androidx.appcompat.app.AlertDialog.Builder(activity)
        alert.setTitle("Rename File")
        alert.setPositiveButton("Rename",
            DialogInterface.OnClickListener { dialog, whichButton ->
                val newTitle = inputTitle.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(newTitle)) {
                    Toast.makeText(activity, "Enter Text", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
                val result = renameVideo1(activity, track, call_from, newTitle, videoAdapter)
                if (result) {
                    Toast.makeText(activity, "rename successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "video can't rename", Toast.LENGTH_SHORT).show()
                }
            })
        alert.setNegativeButton(
            "Cancel"
        ) { dialog, whichButton -> dialog.cancel() }
        alert.setView(view)
        alert.show()
    }

    fun renameVideo1(
        ctx: Context,
        track: Videos,
        call_from: String,
        newTitle: String,
        videoAdapter: VideoAdapter
    ): Boolean {
        var newTitle = newTitle
        try {
            if (!TextUtils.isEmpty(newTitle)) {
                try {
                    newTitle = newTitle.replace(
                        newTitle.substring(newTitle.lastIndexOf(".")).toRegex(),
                        ""
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val selArg = arrayOf<String>(track._id)
                if (File(track.filePath).exists()) {
                    val currentFileName: String = track.filePath.substring(
                        track.filePath.lastIndexOf("/"),
                        track.filePath.length
                    )
                    val filePath: File = File(track.filePath)
                    val dir = filePath.parentFile
                    val from = File(dir, currentFileName)
                    val final_name =
                        newTitle + currentFileName.substring(currentFileName.lastIndexOf("."))
                    val to = File(dir, final_name)
                    from.renameTo(to)
                    val resolver = ctx.contentResolver
                    val valuesMedia = ContentValues()
                    valuesMedia.put(MediaStore.Video.Media.TITLE, final_name)
                    valuesMedia.put(MediaStore.Video.Media.DISPLAY_NAME, final_name)
                    valuesMedia.put(MediaStore.Video.Media.DATA, to.absolutePath)
                    resolver.update(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        valuesMedia,
                        MediaStore.Video.Media._ID + " = ?",
                        selArg
                    )
                    if (call_from == "folder") {
                        /*if (Folder_video_Adapter.dataSet.contains(track));
                        {
                            int i = Folder_video_Adapter.dataSet.indexOf(track);
                            track.title = final_name;
                            Folder_video_Adapter.dataSet.get(i).title = final_name;
                            Folder_video_Adapter.dataSet.get(i).filePath = to.getAbsolutePath();
                            FolderVideoActivity.folderVideoAdapter.notifyItemChanged(i);
                        }*/
                    } else if (call_from == "search") {
                        /*if (Search_Adapter.dataSet.contains(track)) ;
                        {
                            int i = Search_Adapter.dataSet.indexOf(track);
                            track.title = final_name;
                            Search_Adapter.dataSet.get(i).title = final_name;
                            Search_Adapter.dataSet.get(i).filePath = to.getAbsolutePath();
                            SearchActivity.searchAdapter.notifyItemChanged(i);
                        }*/
                    } else {
                        if (videoAdapter.getData().contains(track)) {
                            val i: Int = videoAdapter.getData().indexOf(track)
                            track.title = final_name
                            videoAdapter.getData()[i]!!.title = final_name
                            videoAdapter.getData()[i]!!.filePath = to.absolutePath
                            videoAdapter.notifyItemChanged(i)
                        }
                    }
                }
                return true
            }
        } catch (e: Exception) {
            Log.e("exception..rename", e.message + "..")
            e.printStackTrace()
        }
        return false
    }

    fun dateformate(str_date: String): String? {
        try {
            val millisecond = str_date.toLong() * 1000
            // or you already have long value of date, use this instead of milliseconds variable.
            return DateFormat.format("dd-MM-yyyy", Date(millisecond)).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    //---------------------------------------------------------------------------------
    fun removeTheElement(arr: Array<File?>?, index: Int): Array<File?>? {
        // If the array is empty
        // or the index is not in array range
        // return the original array
        if (arr == null || index < 0 || index >= arr.size) {
            return arr
        }

        // Create another array of size one less
        val anotherArray = arrayOfNulls<File>(arr.size - 1)

        // Copy the elements except the index
        // from original array to the other array
        var i = 0
        var k = 0
        while (i < arr.size) {


            // if the index is
            // the removal element index
            if (i == index) {
                i++
                continue
            }

            // if the index is not
            // the removal element index
            anotherArray[k++] = arr[i]
            i++
        }

        // return the resultant array
        return anotherArray
    }

    fun scanMediaFile(file: File?, activity: Activity) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        activity.sendBroadcast(mediaScanIntent)
    }
}
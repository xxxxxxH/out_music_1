package net.fragment

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import net.DataBase.OpenHelper
import net.basicmodel.Privacy_Policy
import net.basicmodel.R
import net.general.GlobalApp
import net.receiver.AlarmReceiver
import net.utils.PreferencesUtility
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (C) 2021,2021/12/7, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class SettingFragment : Fragment(), TimePickerDialog.OnTimeSetListener {
    var txt_version: TextView? = null
    var txt_notication: TextView? = null
    var ll_clear_cache: LinearLayout? = null
    var ll_check_cache: LinearLayout? = null
    var ll_notification: LinearLayout? = null
    var ll_clear_history: LinearLayout? = null
    var ll_change_theme: LinearLayout? = null
    var ll_privacy: LinearLayout? = null
    var ll_relvant_ads: LinearLayout? = null
    var view_relevant: View? = null
    var checkbox1: CheckBox? = null

    // Toolbar toolbar;
    var openHelper: OpenHelper? = null
    private val calendar: Calendar? = null
    var rel_set_timer: RelativeLayout? = null
    var txt_timer: TextView? = null
    var checkbox_time: CheckBox? = null
    var is_time_set: String = "false"
    var hourOfDay: String? = null
    var minut: String? = null
    var tdp: TimePickerDialog? = null
    var txt_general: TextView? = null
    var txt_advance: TextView? = null
    var txt_musicplayer: TextView? = null
    private var editor: SharedPreferences.Editor? = null
    private var sp: SharedPreferences? = null
    var appBarLayout: AppBarLayout? = null
    var str_ads: String? = null
    var mPreferences: PreferencesUtility? = null
    var is_click_gdpr = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        mPreferences = PreferencesUtility.getInstance(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)
        setHasOptionsMenu(true)
        editor = sp!!.edit()
        Initialize(view)
        val toolbar = view!!.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar.title = requireActivity().resources.getString(R.string.settings)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(false)
        is_time_set =
            GlobalApp.sharedpreferences!!.getString(GlobalApp.SLEEP_TIMER, "false").toString()
        hourOfDay = GlobalApp.sharedpreferences!!.getString(GlobalApp.SLEEP_HOUR, "00")
        minut = GlobalApp.sharedpreferences!!.getString(GlobalApp.SLEEP_MINIT, "00")
        if (is_time_set == "true") {
            checkbox_time!!.isChecked = true
            txt_timer!!.text = timeFormate(hourOfDay!!.toInt(), minut!!.toInt())
        } else {
            checkbox_time!!.isChecked = false
            txt_timer!!.text = "unset"
        }
        openHelper = OpenHelper.sharedInstance(context)
        try {
            txt_version!!.text =
                requireActivity().packageManager.getPackageInfo(
                    requireContext().packageName,
                    0
                ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        ll_clear_cache!!.setOnClickListener { // TODO Auto-generated method stub
            str_ads = "clear_cache"
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show()
//            } else {
            GlobalApp.is_grid = true
            deleteCache(context)
            Toast.makeText(context, "cache has been cleared", Toast.LENGTH_SHORT).show()
//            }
        }
        ll_check_cache!!.setOnClickListener { // TODO Auto-generated method stub
            initializeCache()
        }
//        ll_relvant_ads!!.setOnClickListener {
//            if (form != null) {
//                form.load()
//            }
//        }
        ll_change_theme!!.setOnClickListener { // TODO Auto-generated method stub
            str_ads = "creative"
            GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransitionSetting(
                ThemeFragment(),
                "ThemeFragment",
                requireActivity()
            )
        }
        ll_notification!!.setOnClickListener { // TODO Auto-generated method stub
            if (checkbox1!!.isChecked) {
                checkbox1!!.isChecked = false
                txt_notication!!.text = "Disable"
            } else {
                checkbox1!!.isChecked = true
                txt_notication!!.text = "Enable"
            }
        }
        ll_clear_history!!.setOnClickListener { deleteHistoryDialog(activity) }
        ll_privacy!!.setOnClickListener {
            val intent = Intent(context, Privacy_Policy::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        rel_set_timer!!.setOnClickListener {
            if (is_time_set == "false") {
                ShowTimePickerDialog()
            } else {
                val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor.putString(GlobalApp.SLEEP_TIMER, "false")
                editor.putString(GlobalApp.SLEEP_HOUR, "00" + "")
                editor.putString(GlobalApp.SLEEP_MINIT, "00" + "")
                editor.commit()
                checkbox_time!!.isChecked = false
                is_time_set = "false"
                txt_timer!!.text = "unset"
                SetSleepTimer(context, 0, 0)
            }
        }
        toolbar.menu.clear()
        return view
    }

    private fun Initialize(view: View) {
        ll_clear_cache = view.findViewById<View>(R.id.ll_clear_cache) as LinearLayout
        ll_check_cache = view.findViewById<View>(R.id.ll_check_cache) as LinearLayout
        ll_notification = view.findViewById<View>(R.id.ll_notification) as LinearLayout
        ll_clear_history = view.findViewById<View>(R.id.ll_clear_history) as LinearLayout
        ll_change_theme = view.findViewById<View>(R.id.ll_change_theme) as LinearLayout
        ll_privacy = view.findViewById<View>(R.id.ll_privacy) as LinearLayout
        ll_relvant_ads = view.findViewById<View>(R.id.ll_relvant_ads) as LinearLayout
        view_relevant = view.findViewById(R.id.view_relevant)
        checkbox1 = view.findViewById<View>(R.id.checkBox1) as CheckBox
        txt_notication = view.findViewById<View>(R.id.txt_notification) as TextView
        txt_version = view.findViewById<View>(R.id.txt_version) as TextView
        rel_set_timer = view.findViewById<View>(R.id.rel_set_timer) as RelativeLayout
        txt_timer = view.findViewById<View>(R.id.txt_timer) as TextView
        checkbox_time = view.findViewById<View>(R.id.checkbox_time) as CheckBox
        txt_general = view.findViewById<View>(R.id.txt_general) as TextView
        txt_advance = view.findViewById<View>(R.id.txt_advance) as TextView
        txt_musicplayer = view.findViewById<View>(R.id.txt_musicplayer) as TextView
        appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
    }

    private fun initializeCache() {
        var size: Long = 0
        size += getDirSize(requireActivity().cacheDir)
        size += getDirSize(requireActivity().externalCacheDir)
        val cacheSIzeAlert = AlertDialog.Builder(
            requireContext()
        )
        cacheSIzeAlert.setTitle("Cache usage")
        cacheSIzeAlert.setMessage(toNumInUnits(size))
        val cacheDialog = cacheSIzeAlert.create()
        cacheSIzeAlert.setPositiveButton(
            "Ok"
        ) { dialog, which -> cacheDialog.dismiss() }
        cacheDialog.show()
    }

    fun getDirSize(dir: File?): Long {
        var size: Long = 0
        try {
            for (file in dir!!.listFiles()) {
                if (file != null && file.isDirectory) {
                    size += getDirSize(file)
                } else if (file != null && file.isFile) {
                    size += file.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    private fun ShowTimePickerDialog() {
        val now = Calendar.getInstance()
        tdp = TimePickerDialog.newInstance(
            this,
            now[Calendar.HOUR_OF_DAY],
            now[Calendar.MINUTE], false
        )
        tdp!!.show((activity as AppCompatActivity?)!!.fragmentManager, "TimePickerDialog")
    }

    fun timeFormate(hourOfDay: Int, minut: Int): String {
        var timein12Format = ""
        try {
            val sdf = SimpleDateFormat("H:mm")
            val dateObj = sdf.parse("$hourOfDay:$minut")
            timein12Format = SimpleDateFormat("K:mm a").format(dateObj)
            val f1: DateFormat = SimpleDateFormat("HH:mm") //HH for hour of the day (0 - 23)
            val d = f1.parse("$hourOfDay:$minut")
            val f2: DateFormat = SimpleDateFormat("h:mm a")
            timein12Format = f2.format(d).toUpperCase() // "12:18am"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return timein12Format
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, second: Int) {
        checkbox_time!!.isChecked = true
        is_time_set = "true"
        SetSleepTimer(context, hourOfDay, minute)
        val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
        editor.putString(GlobalApp.SLEEP_HOUR, hourOfDay.toString() + "")
        editor.putString(GlobalApp.SLEEP_MINIT, minute.toString() + "")
        editor.commit()
        txt_timer!!.text = timeFormate(hourOfDay, minute)
    }

    fun deleteHistoryDialog(activity: Activity?) {
        val builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("Clear History")
        builder.setMessage("Are you sure to clear history ?")
        builder.setCancelable(false)
        builder.setPositiveButton(
            Html.fromHtml("<font color='black'>Yes</font>")
        ) { dialog, which ->
            openHelper!!.deleteHistory()
            Toast.makeText(context, "History clear successfully", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(
            Html.fromHtml("<font color='black'>No</font>")
        ) { dialog, which ->
            // negative button logic
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }


    companion object {
        fun toNumInUnits(bytes: Long): String {
            var bytes = bytes
            var u = 0
            while (bytes > 1024 * 1024) {
                u++
                bytes = bytes shr 10
            }
            if (bytes > 1024) u++
            return String.format("%.1f %cB", bytes / 1024f, " kMGTPE"[u])
        }

        fun deleteCache(context: Context?) {
            try {
                val dir = context!!.cacheDir
                deleteDir(dir)
                val cacheSIzeAlert = AlertDialog.Builder(
                    context
                )
                cacheSIzeAlert.setTitle("Cache Cleared")
                cacheSIzeAlert.setMessage("Cache has been cleared")
                val cacheDialog = cacheSIzeAlert.create()
                cacheSIzeAlert.setPositiveButton(
                    "OK"
                ) { dialog, which -> cacheDialog.dismiss() }
                cacheDialog.show()
            } catch (e: Exception) {
            }
        }

        fun deleteDir(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success = deleteDir(
                        File(
                            dir,
                            children[i]
                        )
                    )
                    if (!success) {
                        return false
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) dir.delete() else {
                false
            }
        }

        fun SetSleepTimer(context: Context?, hour: Int, minute: Int) {
            val cal = Calendar.getInstance()
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                GlobalApp.ACTION_SLEEP_TIMER_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            cal[Calendar.HOUR_OF_DAY] = hour
            cal[Calendar.MINUTE] = minute
            cal[Calendar.SECOND] = 0
            if (hour == 0 && minute == 0) {
                alarmMgr.cancel(pendingIntent)
                val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor.putString(GlobalApp.SLEEP_TIMER, "false")
                editor.commit()
            } else {
                alarmMgr[AlarmManager.RTC_WAKEUP, cal.timeInMillis] = pendingIntent
                val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor.putString(GlobalApp.SLEEP_TIMER, "true")
                editor.commit()
            }
        }
    }
}

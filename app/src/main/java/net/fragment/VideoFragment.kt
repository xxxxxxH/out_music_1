package net.fragment

import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import net.Listner.OnItemClickListener
import net.MyApplication
import net.adapter.VideoAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.Videos
import net.utils.PreferencesUtility
import java.util.*

/**
 * Copyright (C) 2021,2021/12/7, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class VideoFragment : Fragment(), OnItemClickListener {
    var navigationView: NavigationView? = null
    var app: MyApplication? = null
    var frameLayout: FrameLayout? = null
    var drawer: DrawerLayout? = null
    var mProgressBar: ProgressBar? = null
    var mPreferencesUtility: PreferencesUtility? = null
    var videoAdapter: VideoAdapter? = null
    var videoRecyclerView: RecyclerView? = null
    var onItemClickListener: OnItemClickListener? = null
    var videolist: ArrayList<Videos?>? = null
    var menu: Menu? = null
    var selectedrdbtn = "_display_name"
    var selectedchckbx = "ASC"
    var toolbar: Toolbar? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_video, container, false)
        setHasOptionsMenu(true)
        app = requireActivity().application as MyApplication
        mPreferencesUtility = PreferencesUtility.getInstance(app!!.instance)
        onItemClickListener = this
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        mProgressBar = view.findViewById(R.id.progressBar)
        videoRecyclerView = view.findViewById(R.id.video_recycler)
        videoRecyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(app!!.instance)
        videoRecyclerView!!.layoutManager = layoutManager
        FetchVideoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null as Void?)
        return view
    }

    override fun onClick(view: View, position: Int) {
        when (view.id) {
            R.id.lin_menu ->                 // do your code
                GlobalApp.popupWindow(
                    view, requireActivity(),
                    videolist!![position]!!, position, "video", videoAdapter!!
                )
            else -> {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.videomenu, menu)
        if (mPreferencesUtility!!.viewType.equals("files")) {
            menu.findItem(R.id.file).isChecked = true
        } else {
            menu.findItem(R.id.folder).isChecked = true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.file) {
            menu!!.findItem(R.id.file).isChecked = true
            mPreferencesUtility!!.viewType = "files"
            GlobalApp.fragmentReplaceTransitionVideo(VideoFragment(), null, requireActivity())
        } else if (id == R.id.folder) {
            menu!!.findItem(R.id.folder).isChecked = true
            mPreferencesUtility!!.viewType = "folders"
            GlobalApp.fragmentReplaceTransitionVideo(FolderFragment(), null, requireActivity())
        } else if (id == R.id.action_short) {
            dialog_sort()
        } else if (id == R.id.action_search_video) {
            GlobalApp.fragmentReplaceTransitionVideo(
                SearchVideoFragment(),
                "SearchVideoFragment",
                requireActivity()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    fun dialog_sort() {
        val inflater = LayoutInflater.from(app)
        val alertView: View = inflater.inflate(R.layout.sort_dialog, null)
        val sortRadioGroup = alertView.findViewById<View>(R.id.sortRadioGroup) as RadioGroup
        val rdbtn_title = alertView.findViewById<View>(R.id.rdbtn_title) as RadioButton
        val rdbtn_size = alertView.findViewById<View>(R.id.rdbtn_size) as RadioButton
        val rdbtn_duration = alertView.findViewById<View>(R.id.rdbtn_duration) as RadioButton
        val rdbtn_date = alertView.findViewById<View>(R.id.rdbtn_date) as RadioButton
        val chkbx_asce = alertView.findViewById<View>(R.id.chkbx_asce) as CheckBox
        val chkbx_desc = alertView.findViewById<View>(R.id.chkbx_desc) as CheckBox
        if (mPreferencesUtility!!.sortby.equals("ASC")) {
            selectedchckbx = "ASC"
            chkbx_asce.isChecked = true
        } else if (mPreferencesUtility!!.sortby.equals("DESC")) {
            selectedchckbx = "DESC"
            chkbx_desc.isChecked = true
        }
        if (mPreferencesUtility!!.sortcolumn.equals("_size")) {
            selectedrdbtn = "_size"
            rdbtn_size.isChecked = true
        } else if (mPreferencesUtility!!.sortcolumn.equals("duration")) {
            selectedrdbtn = "duration"
            rdbtn_duration.isChecked = true
        } else if (mPreferencesUtility!!.sortcolumn.equals("date_added")) {
            selectedrdbtn = "date_added"
            rdbtn_date.isChecked = true
        } else if (mPreferencesUtility!!.sortcolumn.equals("_display_name")) {
            selectedrdbtn = "_display_name"
            rdbtn_title.isChecked = true
        }
        sortRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdbtn_size -> selectedrdbtn = "_size"
                R.id.rdbtn_duration -> selectedrdbtn = "duration"
                R.id.rdbtn_date -> selectedrdbtn = "date_added"
                R.id.rdbtn_title -> selectedrdbtn = "_display_name"
            }
        }
        chkbx_asce.setOnCheckedChangeListener { buttonView, isChecked ->
            chkbx_desc.isChecked = !isChecked
            selectedchckbx = "ASC"
        }
        chkbx_desc.setOnCheckedChangeListener { buttonView, isChecked ->
            chkbx_asce.isChecked = !isChecked
            selectedchckbx = "DESC"
        }

        // Creating and Building the Dialog
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        builder.setTitle(Html.fromHtml("<font style='font-family:simplefont'>" + "Sort By" + "</font>")) //"The Following video will be deleted permanentily" + "\n\n" +videoModel.filePath);
        builder.setView(alertView)
        builder.setPositiveButton(
            "Ok"
        ) { dialog, which ->
            mPreferencesUtility!!.sortby = selectedchckbx
            mPreferencesUtility!!.sortcolumn = selectedrdbtn
            FetchVideoList().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,
                null as Void?
            )
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> }
        builder.show()
    }

    inner class FetchVideoList :
        AsyncTask<Void?, Void?, ArrayList<Videos?>?>() {
        override fun doInBackground(vararg arg0: Void?): ArrayList<Videos?> {
            videolist = ArrayList<Videos?>()
            var cursor: Cursor? = null
            try {
                videolist!!.clear()
                val orderBy: String = mPreferencesUtility!!.sortcolumn
                    .toString() + " " + mPreferencesUtility!!.sortby
                cursor = app!!.instance.contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(
                        MediaStore.Video.Media._ID,
                        MediaStore.MediaColumns.DATA,
                        MediaStore.Video.VideoColumns.DURATION,
                        MediaStore.Video.VideoColumns.RESOLUTION,
                        MediaStore.Video.VideoColumns.DATE_TAKEN,
                        MediaStore.Video.VideoColumns.DATE_ADDED,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME
                    ),
                    null,
                    null,
                    orderBy
                )
                if (cursor!!.moveToFirst()) {
                    do {
                        val videoModel = Videos()
                        videoModel._id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                        videoModel.filePath =
                            cursor.getString(cursor.getColumnIndexOrThrow("_data"))
                        videoModel.title =
                            cursor.getString(cursor.getColumnIndexOrThrow("_display_name"))
                        videoModel.duration =
                            cursor.getString(cursor.getColumnIndexOrThrow("duration"))
                        videoModel.resolution =
                            cursor.getString(cursor.getColumnIndexOrThrow("resolution"))
                        videoModel.date =
                            cursor.getString(cursor.getColumnIndexOrThrow("date_added"))
                        videolist!!.add(videoModel)
                    } while (cursor.moveToNext())
                }
            } catch (e: SQLiteException) {
            } finally {
            }
            return videolist as ArrayList<Videos?>
        }

        override fun onPostExecute(result: ArrayList<Videos?>?) {
            super.onPostExecute(result)
            mProgressBar!!.visibility = View.GONE
            if (result!!.size > 0) {
                val resId: Int = R.anim.layout_animation_from_bottom
                val animation = AnimationUtils.loadLayoutAnimation(
                    context, resId
                )
                videoRecyclerView!!.layoutAnimation = animation
                videoAdapter = VideoAdapter(app!!.instance, result, onItemClickListener)
                videoRecyclerView!!.adapter = videoAdapter
                app!!.videolist = result
            }
        }
    }
}

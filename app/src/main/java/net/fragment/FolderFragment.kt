package net.fragment

import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.Listner.OnItemClickListener
import net.MyApplication
import net.adapter.FolderVideoAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.FolderVideo
import net.utils.PreferencesUtility
import java.util.*

/**
 * Copyright (C) 2021,2021/12/7, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class FolderFragment : Fragment(), OnItemClickListener {
    var bucketIds: MutableList<Long> = ArrayList()
    var mProgressBar: ProgressBar? = null
    var mPreferencesUtility: PreferencesUtility? = null
    var videoRecyclerView: RecyclerView? = null
    var app: MyApplication? = null
    var onItemClickListener: OnItemClickListener? = null
    var folderAdapter: FolderVideoAdapter? = null
    var toolbar: Toolbar? = null
    var menu: Menu? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        if (app!!.folderVideos != null && app!!.folderVideos.size > 0) {
            val resId: Int = R.anim.layout_animation_from_bottom
            val animation = AnimationUtils.loadLayoutAnimation(
                context, resId
            )
            videoRecyclerView!!.layoutAnimation = animation
            folderAdapter = FolderVideoAdapter(
                app!!.instance,
                activity,
                app!!.folderVideos,
                onItemClickListener
            )
            videoRecyclerView!!.adapter = folderAdapter
            mProgressBar!!.visibility = View.GONE
        } else {
            FetchFolderList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null as Void?)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.videomenu, menu)
        menu.findItem(R.id.action_short).isVisible = false
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
        } else if (id == R.id.action_search_video) {
            GlobalApp.fragmentReplaceTransitionVideo(
                SearchVideoFragment(),
                "SearchVideoFragment",
                requireActivity()
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View, position: Int) {
        var intent: Intent
        when (view.id) {
            R.id.relative_folder -> {
                val bundle = Bundle()
                bundle.putLong("bucket_id", app!!.folderVideos[position].bucketId)
                val fragment = FolderVideoFragment()
                fragment.arguments = bundle
                val transaction = requireFragmentManager().beginTransaction()
                transaction.replace(R.id.video_fragment, fragment)
                transaction.addToBackStack("fragment")
                transaction.commit()
            }
            else -> {
            }
        }
    }

    inner class FetchFolderList :
        AsyncTask<Void?, Void?, List<FolderVideo>>() {
        override fun doInBackground(vararg arg0: Void?): List<FolderVideo> {
            val folderVideoArrayList: MutableList<FolderVideo> = ArrayList<FolderVideo>()
            bucketIds.clear()
            val PROJECTION_BUCKET = arrayOf(
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATA
            )
            val BUCKET_ORDER_BY = MediaStore.Video.Media.DATE_TAKEN
            val images = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val cur: Cursor? = app!!.contentResolver.query(
                images, PROJECTION_BUCKET,
                null, null, BUCKET_ORDER_BY
            )
            var album: FolderVideo
            if (cur!!.moveToFirst()) {
                var bucket: String?
                var date: String?
                var data: String?
                var bucketId: Long
                val bucketColumn = cur.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val dateColumn = cur.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)
                val dataColumn = cur.getColumnIndex(MediaStore.Video.Media.DATA)
                val bucketIdColumn = cur.getColumnIndex(MediaStore.Video.Media.BUCKET_ID)
                do {
                    bucket = cur.getString(bucketColumn)
                    date = cur.getString(dateColumn)
                    data = cur.getString(dataColumn)
                    bucketId = cur.getInt(bucketIdColumn).toLong()
                    if (bucketIds.contains(bucketId)) {
                        continue
                    } else {
                        album = FolderVideo()
                        album.bucketId = bucketId
                        album.bucketName = bucket
                        album.dateTaken = date
                        album.data = data
                        album.totalCount = photoCountByAlbum(bucketId)
                        folderVideoArrayList.add(album)
                        bucketIds.add(bucketId)
                    }
                } while (cur.moveToNext())
            }
            cur.close()
            return folderVideoArrayList
        }

        override fun onPostExecute(result: List<FolderVideo>) {
            super.onPostExecute(result)
            if (result.isNotEmpty()) {
                mProgressBar!!.visibility = View.GONE
                val resId: Int = R.anim.layout_animation_from_bottom
                val animation = AnimationUtils.loadLayoutAnimation(
                    context, resId
                )
                videoRecyclerView!!.layoutAnimation = animation
                folderAdapter = FolderVideoAdapter(
                    app!!.instance,
                    activity,
                    result,
                    onItemClickListener
                )
                videoRecyclerView!!.adapter = folderAdapter
                app!!.folderVideos = result
            }
        }
    }

    private fun photoCountByAlbum(bucketId: Long): Int {
        try {
            val orderBy = MediaStore.Video.Media.DATE_TAKEN
            var searchParams: String? = null
            searchParams = "bucket_id = \"$bucketId\""
            val mPhotoCursor: Cursor? = app!!.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
                searchParams, null, "$orderBy DESC"
            )
            if (mPhotoCursor!!.count > 0) {
                return mPhotoCursor.count
            }
            mPhotoCursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}

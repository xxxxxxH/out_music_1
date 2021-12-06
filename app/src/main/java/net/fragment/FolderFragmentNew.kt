package net.fragment

import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.folders_fragment.*
import net.Listner.OnItemClickListener
import net.MyApplication
import net.adapter.FolderAudioAdapter
import net.basicmodel.R
import net.model.FolderSongs
import net.utils.PreferencesUtility
import java.util.*

class FolderFragmentNew : Fragment(), OnItemClickListener {
    var bucketIds: MutableList<Long> = ArrayList()
    var mPreferencesUtility: PreferencesUtility? = null
    var app: MyApplication? = null
    var onItemClickListener: OnItemClickListener? = null
    var folderAdapter: FolderAudioAdapter? = null
    var toolbar: Toolbar? = null

    var mProgressBar: ProgressBar? = null
    var videoRecyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.folders_fragment, container, false)
        app = requireActivity().application as MyApplication
        mPreferencesUtility = PreferencesUtility.getInstance(app!!.instance)
        onItemClickListener = this
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        mProgressBar = view.findViewById(R.id.progressBar)
        videoRecyclerView = view.findViewById(R.id.recyclerview)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        videoRecyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(app!!.instance)
        videoRecyclerView!!.layoutManager = layoutManager
        FetchFolderList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null as Void?)
        toolbar!!.setNavigationOnClickListener { (activity as AppCompatActivity).supportFragmentManager.popBackStack() }
        return view
    }

    override fun onClick(view: View, position: Int) {
        var intent: Intent
        when (view.id) {
            R.id.relative_folder -> {
                // do your code
                val bundle = Bundle()
                bundle.putLong("bucket_id", app!!.folderSongs[position].bucketId)
                val fragment = FolderMusicFragment()
                fragment.arguments = bundle
                val transaction = requireFragmentManager().beginTransaction()
                transaction.replace(R.id.framlayout_main, fragment)
                transaction.addToBackStack("fragment")
                transaction.commit()
            }
            else -> {
            }
        }
    }

    inner class FetchFolderList :
        AsyncTask<Void?, Void?, List<FolderSongs>>() {
        override fun doInBackground(vararg arg0: Void?): List<FolderSongs> {
            val folderVideoArrayList: MutableList<FolderSongs> = ArrayList<FolderSongs>()
            bucketIds.clear()
            val PROJECTION_BUCKET = arrayOf(
                MediaStore.Audio.AudioColumns.BUCKET_ID,
                MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DATE_TAKEN,
                MediaStore.Audio.AudioColumns.DATA
            )
            val BUCKET_ORDER_BY = MediaStore.Audio.AudioColumns.DATE_TAKEN
            val images = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val cur: Cursor = app!!.contentResolver.query(
                images, PROJECTION_BUCKET,
                null, null, BUCKET_ORDER_BY
            )!!
            var album: FolderSongs
            if (cur.moveToFirst()) {
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
                        album = FolderSongs()
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

        override fun onPostExecute(result: List<FolderSongs>) {
            super.onPostExecute(result)
            if (result.isNotEmpty()) {
                mProgressBar!!.visibility = View.GONE
                folderAdapter = FolderAudioAdapter(
                    app!!.instance,
                    activity,
                    result,
                    onItemClickListener
                )
                videoRecyclerView!!.adapter = folderAdapter
                app!!.folderSongs = result
            }
        }
    }

    private fun photoCountByAlbum(bucketId: Long): Int {
        try {
            val orderBy = MediaStore.Audio.Media.DATE_TAKEN
            var searchParams: String? = null
            searchParams = "bucket_id = \"$bucketId\""
            val mPhotoCursor: Cursor = app!!.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                searchParams, null, "$orderBy DESC"
            )!!
            if (mPhotoCursor.count > 0) {
                return mPhotoCursor.count
            }
            mPhotoCursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }
}

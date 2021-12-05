package net.fragment

import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.folders_fragment.*
import net.Listner.OnItemClickListener
import net.adapter.SongsAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.PreferencesUtility
import java.util.*

class FolderMusicFragment : Fragment(), OnItemClickListener {
    var mPreferencesUtility: PreferencesUtility? = null
    var songsAdapter: SongsAdapter? = null
    var onItemClickListener: OnItemClickListener? = null
    var bucket_id: Long = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.folders_fragment, container, false)
        mPreferencesUtility = PreferencesUtility.getInstance(context)
        onItemClickListener = this
        bucket_id = requireArguments().getLong("bucket_id")
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutManager
        FetchVideoList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null as Void?)
        return view
    }

    override fun onClick(view: View, position: Int) {
        var intent: Intent
        when (view.id) {
            R.id.lin_menu -> {
            }
            R.id.relative_folder ->                 // do your code
                Toast.makeText(context, "Click on Folder", Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
    }

    inner class FetchVideoList :
        AsyncTask<Void?, Void?, ArrayList<SongsModel>>() {
        override fun doInBackground(vararg arg0: Void?): ArrayList<SongsModel> {
            val videolist: ArrayList<SongsModel> = ArrayList<SongsModel>()
            var cursor: Cursor? = null
            videolist.clear()
            val searchParams = "bucket_id = \"$bucket_id\""
            val projection = arrayOf(
                "_id",
                "title",
                "artist",
                "album",
                "duration",
                "track",
                "artist_id",
                "album_id",
                "_data",
                "_size"
            )
            val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
            var songsModel: SongsModel
            cursor = activity!!.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                searchParams,
                null,
                sortOrder
            )
            if (cursor!!.moveToFirst()) {
                do {
                    val id = cursor.getLong(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val album = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val trackNumber = cursor.getInt(5)
                    val artistId = cursor.getInt(6).toLong()
                    val albumId = cursor.getLong(7)
                    val data = cursor.getString(8)
                    val size = cursor.getString(9)
                    songsModel = SongsModel(
                        song_id = id,
                        album = album,
                        albumId = albumId,
                        artist = artist,
                        duration = duration.toLong(),
                        img_uri = GlobalApp.getImgUri(albumId),
                        path = data,
                        title = title,
                        size = size,
                        trackNumber = trackNumber,
                        artistId = artistId
                    )
                    videolist.add(songsModel)
                } while (cursor.moveToNext())
            }
            return videolist
        }

        override fun onPostExecute(result: ArrayList<SongsModel>) {
            super.onPostExecute(result)
            if (result.size > 0) {
                progressBar.visibility = View.GONE
                songsAdapter = SongsAdapter(result, requireContext(), requireActivity())
                recyclerview.adapter = songsAdapter
            }
        }
    }
}

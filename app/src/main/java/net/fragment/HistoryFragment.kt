package net.fragment

import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration
import net.DataBase.OpenHelper
import net.adapter.HistoryAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.HistoryModel
import net.model.SongsModel
import java.util.*

class HistoryFragment : Fragment() {
    var historyRecyclerview: RecyclerView? = null
    var mediaItemArrayList: ArrayList<SongsModel>? = null
    var adapter: HistoryAdapter? = null
    var historyArrayList: ArrayList<HistoryModel>? = null
    var openHelper: OpenHelper? = null
    var mediaItem: SongsModel? = null
    var headersDecor: StickyRecyclerHeadersDecoration? = null
    private var sp: SharedPreferences? = null
    var appBarLayout: AppBarLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.history_activity, container, false)
        initialize(view)
        val toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.title = requireActivity().resources.getString(R.string.my_history)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
        mediaItemArrayList = ArrayList()
        historyArrayList = ArrayList()
        mediaItemArrayList!!.clear()
        openHelper = OpenHelper(context)
        mediaItem = SongsModel()
        historyArrayList = openHelper!!.history
        for (i in historyArrayList!!.indices) {
            try {
                mediaItemArrayList!!.add(
                    fatchHistorySongs(
                        historyArrayList!![i].songId,
                        historyArrayList!![i].historyDate
                    )!!
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        adapter = HistoryAdapter(mediaItemArrayList, requireActivity(), requireContext())
        historyRecyclerview!!.adapter = adapter
        headersDecor = StickyRecyclerHeadersDecoration(adapter)
        historyRecyclerview!!.addItemDecoration(headersDecor!!)
        toolbar.menu.clear()
        toolbar.setNavigationOnClickListener { (activity as AppCompatActivity).supportFragmentManager.popBackStack() }
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initialize(view: View) {
        historyRecyclerview = view.findViewById<View>(R.id.historyRecyclerview) as RecyclerView
        historyRecyclerview!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        historyRecyclerview!!.layoutManager = layoutManager
        appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
    }

    private fun fatchHistorySongs(_id: String, historyDate: String): SongsModel? {
        var cursor: Cursor? = null
        var songData: SongsModel? = null
        try {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val c = requireContext().contentResolver.query(
                uri,
                null,
                MediaStore.MediaColumns._ID + "='" + _id + "'",
                null,
                null
            )
            if (c != null && c.count > 0) {
                c.moveToFirst()
                songData = SongsModel()
                val id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID))
                val title = c.getString(c.getColumnIndex("title"))
                val artist = c.getString(c.getColumnIndex("artist"))
                val album = c.getString(c.getColumnIndex("album"))
                val duration = c.getLong(c.getColumnIndex("duration"))
                val data = c.getString(c.getColumnIndex("_data"))
                val albumId = c.getLong(c.getColumnIndex("album_id"))
                val size = c.getString(c.getColumnIndex("_size"))
                songData.song_id = id
                songData.title = title
                songData.album = album
                songData.artist = artist
                songData.duration = duration
                songData.path = data
                songData.albumId = albumId
                songData.size = size
                songData.history_date = historyDate
                songData.img_uri = GlobalApp.getImgUri(java.lang.Long.valueOf(albumId))
            } else {
                openHelper!!.deleteFavoriteSong(_id)
            }
        } catch (e: SQLiteException) {
            Log.e("Error..Favourite", e.message + "..")
        } finally {
            if (cursor != null) {
                cursor.close()
                cursor = null
            }
        }
        return songData
    }
}
package net.fragment

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.album_detail.*
import kotlinx.android.synthetic.main.album_detail.toolbar
import kotlinx.android.synthetic.main.app_bar_main_music.*
import net.Decoration.ItemOffsetDecoration
import net.adapter.GenresDetailAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import java.util.*

class GenresDetailFragment : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
    var play: ArrayList<SongsModel>? = null
    private var animation: Animation? = null
    private var sp: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.album_detail, container, false)
        initialization()
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        mediaItemsArrayList = ArrayList<SongsModel>()
        play = ArrayList<SongsModel>()
        Picasso.get().load(GlobalApp.getImgUri(albumid)).placeholder(R.drawable.musicgenericon)
            .error(R.drawable.musicgenericon).into(header)
        loadArtists().execute("")
        appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    framlayout_album.startAnimation(animation)
                    isShow = true
                } else if (isShow) {
                    framlayout_album.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
        animation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                framlayout_album.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        return view
    }

    inner class loadArtists :
        AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg params: String?): String {
            if (activity != null) {
                val songList: ArrayList<SongsModel?> = ArrayList<SongsModel?>()
                val cursor = requireActivity().contentResolver.query(
                    MediaStore.Audio.Genres.Members.getContentUri(
                        "external",
                        genresid
                    ),
                    arrayOf(
                        "title",
                        "_data",
                        "duration",
                        "_id",
                        "album",
                        "artist",
                        "album_id",
                        "_size"
                    ),
                    null,
                    null,
                    null
                )
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            val MediaItems = SongsModel()
                            val _id = cursor.getLong(cursor.getColumnIndex("_id"))
                            val title = cursor.getString(cursor.getColumnIndex("title"))
                            val duration = cursor.getLong(cursor.getColumnIndex("duration"))
                            val artist = cursor.getString(cursor.getColumnIndex("artist"))
                            val data = cursor.getString(cursor.getColumnIndex("_data"))
                            val albumId = cursor.getLong(cursor.getColumnIndex("album_id"))
                            val size = cursor.getString(cursor.getColumnIndex("_size"))
                            MediaItems.song_id = _id
                            MediaItems.title = title
                            MediaItems.album = albumName!!
                            MediaItems.artist = artist
                            MediaItems.duration = duration
                            MediaItems.path = data
                            MediaItems.img_uri = GlobalApp.getImgUri(albumId)
                            MediaItems.albumId = albumId
                            MediaItems.size = size
                            mediaItemsArrayList!!.add(MediaItems)
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                    //  setCollapsingToolbarLayoutTitle(albumName);
                }
            }
            return "Executed"
        }

        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            setCollapsingToolbarLayoutTitle(albumName)
            progressbar!!.visibility = View.GONE
            val artistDetailAdapter =
                GenresDetailAdapter(mediaItemsArrayList, requireContext(), requireActivity())
            albumDetailRecyclerview!!.adapter = artistDetailAdapter
        }
    }

    private fun setCollapsingToolbarLayoutTitle(title: String?) {
        collapsing_toolbar!!.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun initialization() {
        progressbar.visibility = View.VISIBLE
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        albumDetailRecyclerview!!.setHasFixedSize(true)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        val layoutManager = LinearLayoutManager(activity)
        albumDetailRecyclerview!!.layoutManager = layoutManager
        albumDetailRecyclerview!!.addItemDecoration(itemDecoration)
        animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
    }

    companion object {
        var genresid: Long = 0
        var albumid: Long = 0
        var albumName: String? = null
        fun newInstance(Id: Long, type: String?, albumId: Long): GenresDetailFragment {
            val fragment = GenresDetailFragment()
            albumName = type
            genresid = Id
            try {
                albumid = albumId
            } catch (e: Exception) {
                albumid = 0
                e.printStackTrace()
            }
            return fragment
        }
    }
}

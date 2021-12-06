package net.fragment

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Picasso
import net.Decoration.ItemOffsetDecoration
import net.adapter.GenresDetailAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import java.util.*

class GenresDetailFragment(var Id: Long, var type: String?,var  albumId: Long) : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
    var play: ArrayList<SongsModel>? = null
    private var animation: Animation? = null
    private var sp: SharedPreferences? = null

    var albumDetailRecyclerview: RecyclerView? = null
//    var genresid: Long = 0
//    var albumid: Long = 0
    var header: ImageView? = null
    var header_img:android.widget.ImageView? = null
//    var albumName: String? = null
    var collapsing_toolbar: CollapsingToolbarLayout? = null
    var frameLayout: FrameLayout? = null
    var appBarLayout: AppBarLayout? = null
    var toolbar: Toolbar? = null
    var progressbar: ProgressBar? = null
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
        initialization(view)
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        mediaItemsArrayList = ArrayList<SongsModel>()
        play = ArrayList<SongsModel>()
        Picasso.get().load(GlobalApp.getImgUri(albumId)).placeholder(R.drawable.musicgenericon)
            .error(R.drawable.musicgenericon).into(header)
        loadArtists().execute("")
        appBarLayout!!.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    frameLayout!!.startAnimation(animation)
                    isShow = true
                } else if (isShow) {
                    frameLayout!!.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
        animation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                frameLayout!!.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })

        toolbar!!.setNavigationOnClickListener { (activity as AppCompatActivity).supportFragmentManager.popBackStack() }
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
                        Id
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
                            MediaItems.album = type!!
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
            setCollapsingToolbarLayoutTitle(type!!)
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

    fun initialization(view: View) {
        progressbar = view.findViewById(R.id.progressbar)
        progressbar!!.visibility = View.VISIBLE
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        header = view.findViewById<View>(R.id.header) as ImageView
        header_img = view.findViewById<View>(R.id.img_header) as ImageView
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar!!.title = ""
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

        collapsing_toolbar =
            view.findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout

        albumDetailRecyclerview =
            view.findViewById<View>(R.id.albumDetailRecyclerview) as RecyclerView
        albumDetailRecyclerview!!.setHasFixedSize(true)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        val layoutManager = LinearLayoutManager(activity)
        albumDetailRecyclerview!!.layoutManager = layoutManager
        albumDetailRecyclerview!!.addItemDecoration(itemDecoration)

        appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
        animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
        frameLayout = view.findViewById<View>(R.id.framlayout_album) as FrameLayout
    }

//    companion object {
//        var genresid: Long = 0
//        var albumid: Long = 0
//        var albumName: String? = null
////        fun newInstance(Id: Long, type: String?, albumId: Long): GenresDetailFragment {
////            val fragment = GenresDetailFragment()
////            albumName = type
////            genresid = Id
////            try {
////                albumid = albumId
////            } catch (e: Exception) {
////                albumid = 0
////                e.printStackTrace()
////            }
////            return fragment
////        }
//    }
}

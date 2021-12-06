package net.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
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
import net.adapter.ArtistDetailAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.ArtistSongLoader
import java.util.*

class ArtistDetailFragment(var Id: Long, var type: String) : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
    private var animation: Animation? = null
    var global: GlobalApp? = null
    private var sp: SharedPreferences? = null

    var artistDetailRecyclerview: RecyclerView? = null
    var _albtmID:kotlin.Long = 0
    var header: ImageView? = null
    var header_img:android.widget.ImageView? = null
    var collapsing_toolbar: CollapsingToolbarLayout? = null
    var toolbar: Toolbar? = null
    var appBarLayout: AppBarLayout? = null
    var frameLayout: FrameLayout? = null
//    fun newInstance(?): ArtistDetailFragment {
//        val fragment = ArtistDetailFragment()
//        val args = Bundle()
//        args.putLong("artist_id", Id)
//        args.putString("artist_name", type)
//        fragment.arguments = args
//        return fragment
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.artist_detail, container, false)
        global = GlobalApp.global
        initialization(view)
//        if (arguments != null) {
            val where = MediaStore.Audio.Media.ARTIST_ID + "=?"
            val whereVal = arrayOf(Id.toString() + "")
            val orderBy1 = MediaStore.Audio.Media.TITLE
            val c1 = requireContext().contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf("album_id"),
                where,
                whereVal,
                orderBy1
            )
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    _albtmID = c1.getLong(c1.getColumnIndex("album_id"))
                }
            }
//        }
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        mediaItemsArrayList = ArrayList<SongsModel>()
        Picasso.get().load(GlobalApp.getImgUri(_albtmID)).placeholder(R.drawable.musicartisticon)
            .error(R.drawable.musicartisticon).into(header)
        setUpSongs()
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

    private fun setUpSongs() {
        setCollapsingToolbarLayoutTitle(type)
        mediaItemsArrayList = ArtistSongLoader.getSongsForArtist(requireContext(), Id)
        val artistDetailAdapter =
            ArtistDetailAdapter(mediaItemsArrayList, requireContext(), requireActivity())
        artistDetailRecyclerview!!.adapter = artistDetailAdapter
    }

    private fun setCollapsingToolbarLayoutTitle(title: String?) {
        collapsing_toolbar!!.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initialization(view: View) {
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        collapsing_toolbar =
            view.findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        header = view.findViewById<View>(R.id.header) as ImageView
        header_img = view.findViewById<View>(R.id.header_img) as ImageView
        frameLayout = view.findViewById<View>(R.id.framlayout_artist) as FrameLayout
        animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar!!.title = ""
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

        appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
        artistDetailRecyclerview =
            view.findViewById<View>(R.id.artistDetailRecyclerview) as RecyclerView
        artistDetailRecyclerview!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity as AppCompatActivity?)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        artistDetailRecyclerview!!.layoutManager = layoutManager
        artistDetailRecyclerview!!.addItemDecoration(itemDecoration)
    }

    companion object {
        var artistname: String? = null
        var artistID: Long = 0
        var _albtmID: Long = 0
    }
}

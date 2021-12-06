package net.fragment

import android.os.Bundle
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
import net.adapter.AlbumDetailAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.AlbumSongLoader
import java.util.*

class AlbumDetailFragment(var Id: Long, var type: String) : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
//    var albumid: Long = 0
    var play: ArrayList<SongsModel>? = null
//    var albumName: String? = null
    private var animation: Animation? = null

    var albumDetailRecyclerview: RecyclerView? = null
    var header: ImageView? = null
    var img_header: android.widget.ImageView? = null
    var collapsing_toolbar: CollapsingToolbarLayout? = null
    var frameLayout: FrameLayout? = null
    var appBarLayout: AppBarLayout? = null
    var toolbar: Toolbar? = null
//    fun newInstance(Id: Long, type: String?): AlbumDetailFragment {
//        val fragment = AlbumDetailFragment()
//        val args = Bundle()
//        args.putLong("album_id", Id)
//        args.putString("album_name", type)
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
    ): View {
        val view = inflater.inflate(R.layout.album_detail, container, false)
        initialization(view)
        mediaItemsArrayList = ArrayList<SongsModel>()
        play = ArrayList<SongsModel>()
        Picasso.get().load(GlobalApp.getImgUri(Id)).placeholder(R.drawable.musicalbumicon)
            .error(R.drawable.musicalbumicon).into(header)
        setUpAlbumSongs()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpAlbumSongs() {
        mediaItemsArrayList = AlbumSongLoader.getSongsForAlbum(requireContext(), Id)
        val artistDetailAdapter =
            AlbumDetailAdapter(mediaItemsArrayList, requireContext(), requireActivity())
        albumDetailRecyclerview!!.adapter = artistDetailAdapter
        setCollapsingToolbarLayoutTitle(type)
    }

    private fun setCollapsingToolbarLayoutTitle(title: String?) {
        collapsing_toolbar!!.title = title
    }

    fun initialization(view: View) {
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        header = view.findViewById<View>(R.id.header) as ImageView
        img_header = view.findViewById<View>(R.id.img_header) as ImageView
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
}

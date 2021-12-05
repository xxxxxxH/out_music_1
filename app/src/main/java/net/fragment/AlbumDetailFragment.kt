package net.fragment

import android.os.Bundle
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
import net.adapter.AlbumDetailAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.AlbumSongLoader
import java.util.*

class AlbumDetailFragment : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
    var albumid: Long = 0
    var play: ArrayList<SongsModel>? = null
    var albumName: String? = null
    private var animation: Animation? = null
    fun newInstance(Id: Long, type: String?): AlbumDetailFragment {
        val fragment = AlbumDetailFragment()
        val args = Bundle()
        args.putLong("album_id", Id)
        args.putString("album_name", type)
        fragment.arguments = args
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
        if (arguments != null) {
            albumid = requireArguments().getLong("album_id")
            albumName = requireArguments().getString("album_name")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.album_detail, container, false)
        initialization()
        mediaItemsArrayList = ArrayList<SongsModel>()
        play = ArrayList<SongsModel>()
        Picasso.get().load(GlobalApp.getImgUri(albumid)).placeholder(R.drawable.musicalbumicon)
            .error(R.drawable.musicalbumicon).into(header)
        setUpAlbumSongs()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setUpAlbumSongs() {
        mediaItemsArrayList = AlbumSongLoader.getSongsForAlbum(requireContext(), albumid)
        val artistDetailAdapter =
            AlbumDetailAdapter(mediaItemsArrayList, requireContext(), requireActivity())
        albumDetailRecyclerview!!.adapter = artistDetailAdapter
        setCollapsingToolbarLayoutTitle(albumName)
    }

    private fun setCollapsingToolbarLayoutTitle(title: String?) {
        collapsing_toolbar!!.title = title
    }

    fun initialization() {
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
}

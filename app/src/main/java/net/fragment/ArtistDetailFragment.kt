package net.fragment

import android.content.SharedPreferences
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
import kotlinx.android.synthetic.main.app_bar_main_music.*
import kotlinx.android.synthetic.main.artist_detail.*
import kotlinx.android.synthetic.main.artist_detail.toolbar
import net.Decoration.ItemOffsetDecoration
import net.adapter.ArtistDetailAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.ArtistSongLoader
import java.util.*

class ArtistDetailFragment : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
    private var animation: Animation? = null
    var global: GlobalApp? = null
    private var sp: SharedPreferences? = null
    fun newInstance(Id: Long, type: String?): ArtistDetailFragment {
        val fragment = ArtistDetailFragment()
        val args = Bundle()
        args.putLong("artist_id", Id)
        args.putString("artist_name", type)
        fragment.arguments = args
        return fragment
    }

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
        initialization()
        if (arguments != null) {
            artistID = requireArguments().getLong("artist_id")
            artistname = requireArguments().getString("artist_name")
            val where = MediaStore.Audio.Media.ARTIST_ID + "=?"
            val whereVal = arrayOf(artistID.toString() + "")
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
        }
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        mediaItemsArrayList = ArrayList<SongsModel>()
        Picasso.get().load(GlobalApp.getImgUri(_albtmID)).placeholder(R.drawable.musicartisticon)
            .error(R.drawable.musicartisticon).into(header)
        setUpSongs()
        appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    framlayout_artist.startAnimation(animation)
                    isShow = true
                } else if (isShow) {
                    framlayout_artist.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
        animation!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                framlayout_artist.visibility = View.GONE
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        return view
    }

    private fun setUpSongs() {
        setCollapsingToolbarLayoutTitle(artistname)
        mediaItemsArrayList = ArtistSongLoader.getSongsForArtist(requireContext(), artistID)
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

    private fun initialization() {
        animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        artistDetailRecyclerview!!.setHasFixedSize(true)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        artistDetailRecyclerview!!.layoutManager = LinearLayoutManager(activity)
        artistDetailRecyclerview!!.addItemDecoration(itemDecoration)
    }

    companion object {
        var artistname: String? = null
        var artistID: Long = 0
        var _albtmID: Long = 0
    }
}

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
import net.DataBase.OpenHelper
import net.Decoration.ItemOffsetDecoration
import net.adapter.PlaylistDetailAdapter
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GlobalApp
import net.model.SongsModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class PlaylistDetaillFragment : Fragment() {
    var playlistsongArrayList: ArrayList<SongsModel>? = null
    private var animation: Animation? = null
    var playlistId: String? = null
    var imageUrl: String? = null
    var playlistName: String? = null
    var openHelper: OpenHelper? = null
    var playlistDetailAdapter: PlaylistDetailAdapter? = null
    fun newInstance(
        Id: String,
        image_uri: String,
        playlist_name: String
    ): PlaylistDetaillFragment? {
        val fragment = PlaylistDetaillFragment()
        playlistId = Id
        imageUrl = image_uri
        playlistName = playlist_name
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
    ): View {
        val view = inflater.inflate(R.layout.album_detail, container, false)
        EventBus.getDefault().register(this)
        initialization()

        //  this.sp = getActivity().getSharedPreferences(getString(R.string.preference_file_key), 0);
        openHelper = OpenHelper.sharedInstance(activity)
        playlistsongArrayList = ArrayList<SongsModel>()
        setCollapsingToolbarLayoutTitle(playlistName!!)
        Picasso.get().load(imageUrl)
            .placeholder(R.drawable.musicplaylisticon).error(R.drawable.musicplaylisticon)
            .into(header)
        playlistsongArrayList =
            openHelper!!.getPlayListData(playlistId, context)
        playlistDetailAdapter =
            PlaylistDetailAdapter(playlistsongArrayList, requireContext(), requireActivity())
        albumDetailRecyclerview.adapter = playlistDetailAdapter
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


    private fun setCollapsingToolbarLayoutTitle(title: String) {
        collapsing_toolbar.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun initialization() {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
        albumDetailRecyclerview.setHasFixedSize(true)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        val layoutManager = LinearLayoutManager(activity)
        albumDetailRecyclerview.layoutManager = layoutManager
        albumDetailRecyclerview.addItemDecoration(itemDecoration)
        animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event:MessageEvent){
        val msg = event.getMessage()
        when(msg[0]){
            "PlaylistDetaillFragment"->{
                PlaylistDetailAdapter.dataSet =
                    openHelper!!.getPlayListData(playlistId, context)
                playlistDetailAdapter!!.notifyDataSetChanged()
            }
        }
    }
}

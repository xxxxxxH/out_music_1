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

class PlaylistDetaillFragment(var playlistId: String?,var imageUrl: String?,var playlistName: String? ) : Fragment() {
    var playlistsongArrayList: ArrayList<SongsModel>? = null
    private var animation: Animation? = null
//    var playlistId: String? = null
//    var imageUrl: String? = null
//    var playlistName: String? = null
    var openHelper: OpenHelper? = null
    var playlistDetailAdapter: PlaylistDetailAdapter? = null
    var albumDetailRecyclerview: RecyclerView? = null
    var header: ImageView? = null
    var header_img:android.widget.ImageView? = null
    var collapsing_toolbar: CollapsingToolbarLayout? = null
    var frameLayout: FrameLayout? = null
    var appBarLayout: AppBarLayout? = null
    var toolbar: Toolbar? = null
//    fun newInstance(
//        Id: String,
//        image_uri: String,
//        playlist_name: String
//    ): PlaylistDetaillFragment {
//        val fragment = PlaylistDetaillFragment()
//        playlistId = Id
//        imageUrl = image_uri
//        playlistName = playlist_name
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
        EventBus.getDefault().register(this)
        initialization(view)

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
        albumDetailRecyclerview!!.adapter = playlistDetailAdapter
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


    private fun setCollapsingToolbarLayoutTitle(title: String) {
        collapsing_toolbar!!.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun initialization(view: View) {
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

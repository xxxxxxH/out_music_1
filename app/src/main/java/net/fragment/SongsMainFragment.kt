package net.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ToxicBakery.viewpager.transforms.DefaultTransformer
import com.google.android.material.navigation.NavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.footer_player.*
import kotlinx.android.synthetic.main.music_player_new.*
import kotlinx.android.synthetic.main.musicplayer_main.*
import me.tankery.lib.circularseekbar.CircularSeekBar
import me.tankery.lib.circularseekbar.CircularSeekBar.OnCircularSeekBarChangeListener
import net.Controls.MusicPlayerControls
import net.DataBase.OpenHelper
import net.Decoration.ItemOffsetDecoration
import net.Services.MusicService
import net.adapter.QueueAdapter
import net.adapter.SwipePagerAdapter
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GlobalApp
import net.utils.PreferencesUtility
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SongsMainFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    QueueAdapter.MyViewHolder.ClickListener {
    var mPreferences: PreferencesUtility? = null
    var queue_id = 0
    var openHelper: OpenHelper? = null

    // FoldersFragment fragment;
    var clickListener: QueueAdapter.MyViewHolder.ClickListener? = null
    var queueAdapter: QueueAdapter? = null
    var viewpageSwipePagerAdapter: SwipePagerAdapter? = null
    var isExpanded = false
    var isCollapsed = false
    var position_temp = 0
    var pos: Int = 0
    var temp_queue_pos = 0
    var main_default_background = 0

    var queueRecyclerview: RecyclerView? = null
    var playerSlidingUpPanelLayout: SlidingUpPanelLayout? = null
    var lin_lay_panel: LinearLayout? = null
    var linear_play: LinearLayout? = null
    var linear_queue: RelativeLayout? = null
    var playerSeekbar: SeekBar? = null
    var custom_progressBar: CircularSeekBar? = null
    var playerViewpager: ViewPager? = null
    var slidingpanel_header: LinearLayout? = null
    var lin_player_control: LinearLayout? = null

    var img_player_play_pause: ImageView? =
        null
    var img_player_next: android.widget.ImageView? = null
    var img_player_previous: android.widget.ImageView? = null
    var img_delete_list: ImageView? = null
    var img_player_menu: android.widget.ImageView? = null
    var img_player_background: android.widget.ImageView? = null
    var img_repeat_music_player: android.widget.ImageView? = null
    var img_shuffle_music_player: ImageView? = null
    var txt_player_starttime: TextView? = null
    var txt_player_endtime: TextView? = null
    var circlr_player_image: CircleImageView? = null
    var img_play_pause_footer: ImageView? = null
    var img_queue_list: android.widget.ImageView? = null
    var img_play_menu_footer: android.widget.ImageView? = null
    var nav_header: android.widget.ImageView? = null
    var txt_singername_footer: TextView? = null
    var txt_songname_footer: TextView? = null
    var txt_start_time_footer_layout: TextView? = null
    var txt_header: TextView? = null
    var drawer: DrawerLayout? = null
    var img_album_footer: CircleImageView? = null

    var seekbarTrack = true

    var menu: Menu? = null
    var str_temp: String? = null

    //===================== Equalizer ==================================
    var equalizer: Equalizer? = null
    var bassBoost: BassBoost? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.musicplayer_main, container, false)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
        mPreferences = PreferencesUtility.getInstance(activity)
        clickListener = this
        playerControlInit(view)
        replaceFragmentWithAnimation(MainFragment())
        val showRateDialog = GlobalApp.sharedpreferences!!.getString(GlobalApp.RATE_US, "yes")
        if (showRateDialog == "yes") {
            val appUsedCount = GlobalApp.sharedpreferences!!.getInt(GlobalApp.APPUSEDCOUNT, 0) + 1
            GlobalApp.sharedpreferences!!.edit().putInt(GlobalApp.APPUSEDCOUNT, appUsedCount)
                .apply()
        }

        playerSlidingUpPanelLayout!!.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if (slideOffset > 0.5) {
                    img_player_background!!.alpha = slideOffset
                } else {
                    img_player_background!!.alpha = 0.5f
                }
                if (slideOffset.toDouble() == 1.0) {
                    linear_queue!!.visibility = View.VISIBLE
                    linear_play!!.visibility = View.GONE
                } else if (slideOffset.toDouble() == 0.0) {
                    linear_play!!.visibility = View.VISIBLE
                    linear_queue!!.visibility = View.GONE
                }
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
                if (newState == PanelState.EXPANDED) {
                    queueAdapter!!.clearSelection()
                    queueAdapter!!.notifyDataSetChanged()
                    isExpanded = true
                    isCollapsed = false
                } else if (newState == PanelState.COLLAPSED) {
                    img_delete_list!!.visibility = View.GONE
                    img_queue_list!!.visibility = View.VISIBLE
                    isExpanded = false
                    isCollapsed = true
                } else if (newState == PanelState.ANCHORED) {
                    if (isExpanded) {
                        Handler().postDelayed({
                            playerSlidingUpPanelLayout!!.panelState = PanelState.COLLAPSED
                        }, 100)
                    } else if (isCollapsed) {
                        Handler().postDelayed({
                            playerSlidingUpPanelLayout!!.panelState = PanelState.EXPANDED
                        }, 100)
                    }
                }
                queueRecyclerview!!.visibility = View.GONE
            }
        })

        slidingpanel_header!!.setOnClickListener(View.OnClickListener {
            playerSlidingUpPanelLayout!!.isTouchEnabled = true
            if (isExpanded) {
                Handler().postDelayed({
                    playerSlidingUpPanelLayout!!.panelState = PanelState.COLLAPSED
                }, 100)
            } else {
                Handler().postDelayed({
                    playerSlidingUpPanelLayout!!.panelState = PanelState.EXPANDED
                }, 100)
            }
            queueRecyclerview!!.visibility = View.GONE
        })

        if (queueRecyclerview!!.visibility == View.GONE) {
            playerSlidingUpPanelLayout!!.isTouchEnabled = true
        }

        img_queue_list!!.setOnClickListener(View.OnClickListener {
            if (queueRecyclerview!!.visibility == View.GONE) {
                queueAdapter!!.clearSelection()
                GlobalApp.mediaItemsArrayList =
                    openHelper!!.getQueueData(requireContext())
                viewpageSwipePagerAdapter!!.notifyDataSetChanged()
                queueRecyclerview!!.scrollToPosition(MusicPlayerControls.SONG_NUMBER)
                queueRecyclerview!!.visibility = View.VISIBLE
                playerSlidingUpPanelLayout!!.isTouchEnabled = false
            } else {
                queueRecyclerview!!.visibility = View.GONE
                playerSlidingUpPanelLayout!!.isTouchEnabled = true
            }
        })

        img_play_pause_footer!!.setOnClickListener(View.OnClickListener {
            if (!GlobalApp.isServiceRunning(
                    MusicService::class.java.name,
                    requireContext()
                )
            ) {
                val musIntent = Intent(requireContext(), MusicService::class.java)
                requireContext().startService(musIntent)
                Handler().postDelayed({ MusicService.playSong() }, 200)
            } else {
                Handler().postDelayed(
                    { requireContext().sendBroadcast(Intent(GlobalApp.BROADCAST_PLAYPAUSE)) },
                    100
                )
            }
        })

        img_player_play_pause!!.setOnClickListener(View.OnClickListener {
            if (!GlobalApp.isServiceRunning(
                    MusicService::class.java.name,
                    requireContext()
                )
            ) {
                val musIntent = Intent(requireContext(), MusicService::class.java)
                requireActivity().startService(musIntent)
                Handler().postDelayed({ MusicService.playSong() }, 100)
            } else {
                Handler().postDelayed(
                    { requireContext().sendBroadcast(Intent(GlobalApp.BROADCAST_PLAYPAUSE)) },
                    100
                )
            }
        })

        img_player_next!!.setOnClickListener(View.OnClickListener {
            if (GlobalApp.isServiceRunning(
                    MusicService::class.java.name,
                    requireContext()
                )
            ) {
                requireContext().sendBroadcast(Intent(GlobalApp.BROADCAST_NEXT))
            }
        })

        img_player_previous!!.setOnClickListener(View.OnClickListener {
            if (GlobalApp.isServiceRunning(
                    MusicService::class.java.name,
                    requireContext()
                )
            ) {
                requireContext().sendBroadcast(Intent(GlobalApp.BROADCAST_PREV))
            }
        })

        playerViewpager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                pos = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == 1 || state == 2) {
                    position_temp = pos
                }
                if (state == 0 && position_temp != pos) {
                    MusicPlayerControls.SONG_NUMBER = pos
                    if (!GlobalApp.isServiceRunning(
                            MusicService::class.java.name,
                            requireContext()
                        )
                    ) {
                        val musIntent = Intent(requireContext(), MusicService::class.java)
                        requireActivity().startService(musIntent)
                        Handler().postDelayed({ MusicService.playSong() }, 100)
                    } else {
                        MusicService.playSong()
                    }
                }
            }
        })

        img_repeat_music_player!!.setOnClickListener(View.OnClickListener {
            if (MusicService.isRepeat()) {
                img_repeat_music_player!!.setImageResource(R.drawable.ic_repeat_off)
                Toast.makeText(requireContext(), "repeat off", Toast.LENGTH_SHORT).show()
            } else {
                img_repeat_music_player!!.setImageResource(R.drawable.ic_repeat_on)
                Toast.makeText(requireContext(), "repeat on", Toast.LENGTH_SHORT).show()
            }
            MusicService.setRepeat()
        })

        img_shuffle_music_player!!.setOnClickListener(View.OnClickListener {
            if (MusicService.isShuffle()) {
                img_shuffle_music_player!!.setImageResource(R.drawable.ic_shuffle_off)
                Toast.makeText(requireContext(), "shuffle off", Toast.LENGTH_SHORT).show()
            } else {
                img_shuffle_music_player!!.setImageResource(R.drawable.ic_shuffle_white_24dp)
                Toast.makeText(requireContext(), "shuffle on", Toast.LENGTH_SHORT).show()
            }
            MusicService.setShuffle()
        })

        img_delete_list!!.setOnClickListener(View.OnClickListener {
            val deletePermission = AlertDialog.Builder(requireContext())
            deletePermission.setTitle("Delete")
            deletePermission.setMessage("Are you sure?\nYou want to delete songs from Queue?")
            deletePermission.setPositiveButton(
                "yes"
            ) { dialog, which ->
                Toast.makeText(
                    requireContext(),
                    queueAdapter!!.selectedItemCount.toString() + " song " +
                            "deleted !",
                    Toast.LENGTH_SHORT
                ).show()
                val list: List<Int> =
                    queueAdapter!!.getSelectedItems2()
                for (i in list.size - 1 downTo 0) {
                    val pos = list[i]
                    val song_id: Long = GlobalApp.mediaItemsArrayList[pos].song_id
                    if (MusicPlayerControls.SONG_NUMBER == pos) {
                        temp_queue_pos = GlobalApp.mediaItemsArrayList[pos].queue_id
                    }
                    openHelper!!.deleteQueueSong(song_id)
                    GlobalApp.mediaItemsArrayList =
                        openHelper!!.getQueueData(requireContext())
                    queueAdapter!!.notifyDataSetChanged()
                    viewpageSwipePagerAdapter!!.notifyDataSetChanged()
                }
                if (GlobalApp.mediaItemsArrayList.size > 0) {
                    if (temp_queue_pos > 0) {
                        val pos = openHelper!!.getNext(temp_queue_pos)
                        if (pos > 0) {
                            var i = 0
                            for (mediaItem in GlobalApp.mediaItemsArrayList) {
                                if (mediaItem.queue_id == pos) {
                                    MusicPlayerControls.SONG_NUMBER = i
                                    val editor = GlobalApp.sharedpreferences!!.edit()
                                    editor.putString(
                                        GlobalApp.PREFREANCE_LAST_SONG_KEY,
                                        MusicPlayerControls.SONG_NUMBER.toString() + ""
                                    )
                                    editor.putString(
                                        "songId",
                                        GlobalApp.mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].toString() + ""
                                    )
                                    editor.commit()
                                    val editor1 = GlobalApp.sharedpreferences!!.edit()
                                    editor1.putInt(
                                        GlobalApp.SONGNUMBER,
                                        MusicPlayerControls.SONG_NUMBER
                                    )
                                    editor1.commit()
                                    songNext()
                                    temp_queue_pos = 0
                                    break
                                }
                                i++
                            }
                        } else {
                            MusicPlayerControls.SONG_NUMBER = 0
                            songNext()
                            val editor = GlobalApp.sharedpreferences!!.edit()
                            editor.putString(
                                GlobalApp.PREFREANCE_LAST_SONG_KEY,
                                MusicPlayerControls.SONG_NUMBER.toString() + ""
                            )
                            editor.putString(
                                "songId",
                                GlobalApp.mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].toString() + ""
                            )
                            editor.commit()
                            val editor1 = GlobalApp.sharedpreferences!!.edit()
                            editor1.putInt(
                                GlobalApp.SONGNUMBER,
                                MusicPlayerControls.SONG_NUMBER
                            )
                            editor1.commit()
                        }
                        //GlobalApp.SONG_NUMBER=pos;
                    } else {
                        val pos =
                            openHelper!!.getCurrent_song(queue_id) // queue id
                        if (pos > 0) {
                            var i = 0
                            for (mediaItem in GlobalApp.mediaItemsArrayList) {
                                if (mediaItem.queue_id == pos) {
                                    MusicPlayerControls.SONG_NUMBER = i
                                    val editor = GlobalApp.sharedpreferences!!.edit()
                                    editor.putString(
                                        GlobalApp.PREFREANCE_LAST_SONG_KEY,
                                        MusicPlayerControls.SONG_NUMBER.toString() + ""
                                    )
                                    editor.putString(
                                        "songId",
                                        GlobalApp.mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].toString() + ""
                                    )
                                    editor.commit()
                                    val editor1 = GlobalApp.sharedpreferences!!.edit()
                                    editor1.putInt(
                                        GlobalApp.SONGNUMBER,
                                        MusicPlayerControls.SONG_NUMBER
                                    )
                                    editor1.commit()
                                    break
                                }
                                i++
                            }
                        } else {
                            MusicPlayerControls.SONG_NUMBER = 0
                            changeUi(MusicPlayerControls.SONG_NUMBER)
                            val editor = GlobalApp.sharedpreferences!!.edit()
                            editor.putString(
                                GlobalApp.PREFREANCE_LAST_SONG_KEY,
                                MusicPlayerControls.SONG_NUMBER.toString() + ""
                            )
                            editor.putString(
                                "songId",
                                GlobalApp.mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER].toString() + ""
                            )
                            editor.commit()
                            val editor1 = GlobalApp.sharedpreferences!!.edit()
                            editor1.putInt(
                                GlobalApp.SONGNUMBER,
                                MusicPlayerControls.SONG_NUMBER
                            )
                            editor1.commit()
                        }
                    }
                }
                img_delete_list!!.visibility = View.GONE
                img_queue_list!!.visibility = View.VISIBLE
                queueAdapter!!.clearSelection()
                if (GlobalApp.mediaItemsArrayList.size == 0) {
                    if (GlobalApp.isServiceRunning(
                            MusicService::class.java.name,
                            requireContext()
                        )
                    ) {
                        MusicService.stopService()
                    }
                    requireActivity().runOnUiThread(Runnable {
                        playerSlidingUpPanelLayout!!.panelState = PanelState.HIDDEN
                    })
                }
            }
            deletePermission.setNegativeButton(
                "Cancel"
            ) { dialog, which -> queueAdapter!!.clearSelection() }
            deletePermission.show()
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_myfolders) {
            GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                FolderFragmentNew(),
                "FilesFragment", requireActivity()
            )
        } else if (id == R.id.nav_myplaylist) {
            str_temp = "playlist"
            GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
            val editor1 = GlobalApp.sharedpreferences!!.edit()
            editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
            editor1.commit()
            if (GlobalApp.ads_count >= GlobalApp.ads_total_count) {
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show()
//                    val editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                } else {
                    GlobalApp.sharedInstance(activity)!!
                        .fragmentReplaceTransition(
                            PlaylistFragment(),
                            "MyPlaylistFragment",
                            requireActivity()
                        )
//                }
            } else {
                GlobalApp.sharedInstance(activity)!!
                    .fragmentReplaceTransition(
                        PlaylistFragment(),
                        "MyPlaylistFragment",
                        requireActivity()
                    )
            }
            // Handle the camera action
        } else if (id == R.id.nav_ringtone) {
            try {
                EventBus.getDefault().post(MessageEvent("setCurrentItem",5))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else if (id == R.id.nav_equalizer) {
            if (equalizer != null) {
                equalizer!!.release()
            }
            if (bassBoost != null) {
                bassBoost!!.release()
            }
            str_temp = "equalizer"
            GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
            val editor1 = GlobalApp.sharedpreferences!!.edit()
            editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
            editor1.commit()
            if (GlobalApp.ads_count >= GlobalApp.ads_total_count) {
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show()
//                    val editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                } else {
                    GlobalApp.sharedInstance(activity)!!
                        .fragmentReplaceTransition(
                            EqualizerFragment(),
                            "EqualizerFragment",
                            requireActivity()
                        )
//                }
            } else {
                GlobalApp.sharedInstance(activity)!!
                    .fragmentReplaceTransition(
                        EqualizerFragment(),
                        "EqualizerFragment",
                        requireActivity()
                    )
            }
        } else if (id == R.id.nav_myhistory) {
            str_temp = "history"
            GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
            if (GlobalApp.sharedpreferences == null) {
                GlobalApp.savePrefrence(requireContext())
            }
            val editor1 = GlobalApp.sharedpreferences!!.edit()
            editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
            editor1.commit()
            if (GlobalApp.ads_count >= GlobalApp.ads_total_count) {
//                if (mInterstitialAd.isLoaded()) {
//                    val editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                    mInterstitialAd.show()
//                } else {
                    GlobalApp.sharedInstance(activity)!!
                        .fragmentReplaceTransition(
                            HistoryFragment(),
                            "HistoryFragment",
                            requireActivity()
                        )
//                }
            } else {
                GlobalApp.sharedInstance(activity)!!
                    .fragmentReplaceTransition(
                        HistoryFragment(),
                        "HistoryFragment",
                        requireActivity()
                    )
            }
        } else if (id == R.id.nav_share) {
            try {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_SUBJECT, "Music Player")
                var sAux = """
            
            ${resources.getString(R.string.share_app_msg)}
            
            
            """.trimIndent()
                sAux =
                    sAux + "https://play.google.com/store/apps/details?id=" + requireActivity().packageName
                i.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(i, "choose one"))
            } catch (e: java.lang.Exception) {
            }
        } else if (id == R.id.nav_rateus) {
            val i = Intent("android.intent.action.VIEW")
            i.data =
                Uri.parse("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
            startActivity(i)
        }


        drawer!!.closeDrawer(GravityCompat.START)
        return false
    }

    private fun playerControlInit(view: View) {
        drawer = view.findViewById<View>(R.id.drawer_layout) as DrawerLayout

        // Playe controle

        // Playe controle
        playerViewpager =
            view.findViewById<View>(R.id.viewpager_music_player) as ViewPager

        img_player_next =
            view.findViewById<View>(R.id.img_next_music_player) as ImageView
        img_player_previous =
            view.findViewById<View>(R.id.img_prev_music_player) as ImageView
        img_player_play_pause =
            view.findViewById<View>(R.id.img_play_music_player) as ImageView

        playerSeekbar =
            view.findViewById<View>(R.id.seekBar_music_player) as SeekBar
        custom_progressBar =
            view.findViewById<View>(R.id.custom_progressBar) as CircularSeekBar
        playerSeekbar!!.progressDrawable.setColorFilter(
            resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN
        )
        playerSeekbar!!.max = 99
        custom_progressBar!!.max = 99f

        txt_player_endtime =
            view.findViewById<View>(R.id.txt_end_time_music_player) as TextView
        txt_player_starttime =
            view.findViewById<View>(R.id.txt_start_time_music_player) as TextView

        slidingpanel_header =
            view.findViewById<View>(R.id.slidingpanel_header) as LinearLayout
        lin_player_control =
            view.findViewById<View>(R.id.lin_player_control) as LinearLayout


        //  Player footer controle


        //  Player footer controle
        img_album_footer =
            view.findViewById<View>(R.id.img_album_footer_layout) as CircleImageView
        img_play_menu_footer =
            view.findViewById<View>(R.id.img_play_menu_footer) as ImageView
        img_play_pause_footer =
            view.findViewById<View>(R.id.img_play_song_footer_layout) as ImageView
        txt_songname_footer =
            view.findViewById<View>(R.id.txt_song_name_footer_layout) as TextView
        txt_songname_footer!!.isSelected = true
        txt_singername_footer =
            view.findViewById<View>(R.id.txt_artist_name_footer_layout) as TextView
        txt_singername_footer!!.isSelected = true
        txt_start_time_footer_layout =
            view.findViewById<View>(R.id.txt_start_time_footer_layout) as TextView
        img_player_background =
            view.findViewById<View>(R.id.img_background_music_player) as ImageView
        circlr_player_image =
            view.findViewById<View>(R.id.circle_player_image) as CircleImageView
        img_shuffle_music_player =
            view.findViewById<View>(R.id.img_shuffle_music_player) as ImageView
        img_repeat_music_player =
            view.findViewById<View>(R.id.img_repeat_music_player) as ImageView
        img_queue_list = view.findViewById<View>(R.id.img_queue_list) as ImageView
        img_delete_list =
            view.findViewById<View>(R.id.img_delete_list) as ImageView


        //====Relative Layout ====


        //====Relative Layout ====
        playerSlidingUpPanelLayout =
            view.findViewById<View>(R.id.sliding_layout) as SlidingUpPanelLayout

        lin_lay_panel =
            view.findViewById<View>(R.id.footer_layout_music_player) as LinearLayout
        linear_play = view.findViewById<View>(R.id.linear_play) as LinearLayout
        linear_queue =
            view.findViewById<View>(R.id.linear_queue) as RelativeLayout

        queueRecyclerview =
            view.findViewById<View>(R.id.queueRecyclerview) as RecyclerView
        queueRecyclerview!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        queueRecyclerview!!.layoutManager = layoutManager
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        queueRecyclerview!!.addItemDecoration(itemDecoration)
        openHelper = OpenHelper.sharedInstance(requireContext())


        /*  toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)activity).setSupportActionBar(toolbar);*/
        drawer = view.findViewById<View>(R.id.drawer_layout) as DrawerLayout
        /* ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                ((AppCompatActivity)activity), drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/
        val navigationView = view.findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        nav_header = headerView.findViewById<View>(R.id.nav_header) as ImageView
        txt_header = headerView.findViewById<View>(R.id.txt_header) as TextView
        navigationView.setNavigationItemSelectedListener(this)

        val opacity_value = GlobalApp.sharedpreferences!!.getInt(
            GlobalApp.TRANCPARENT_COLOR,
            GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE
        ) // default transparancy

        try {
            slidingpanel_header!!.setBackgroundColor(opacity_value)
            custom_progressBar!!.circleProgressColor =
                resources.getColor(R.color.circle_progress_color)
            custom_progressBar!!.circleColor = resources.getColor(R.color.circle_color)
            custom_progressBar!!.pointerColor = resources.getColor(R.color.circle_pointer_color)
            custom_progressBar!!.pointerHaloColor =
                resources.getColor(R.color.circle_pointer_holo_color)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fillQueueAdapter()
        setPlayerUI()
    }

    fun fillQueueAdapter() {
        if (GlobalApp.mediaItemsArrayList != null) {
            queueAdapter = QueueAdapter(
                null,
                requireContext(),
                requireActivity(),
                clickListener!!
            )
            queueRecyclerview!!.adapter = queueAdapter
            viewpageSwipePagerAdapter = null
            viewpageSwipePagerAdapter =
                SwipePagerAdapter(requireContext())
            playerViewpager!!.adapter = viewpageSwipePagerAdapter
            playerViewpager!!.setPageTransformer(true, DefaultTransformer())
        }
    }

    fun setPlayerUI() {
        GlobalApp.LAST_SONG =
            GlobalApp.sharedpreferences!!.getString(GlobalApp.PREFREANCE_LAST_SONG_KEY, "")
        if (GlobalApp.LAST_SONG.equals("")) {
            playerSlidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        } else {
            playerSlidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            MusicPlayerControls.SONG_NUMBER = GlobalApp.LAST_SONG!!.toInt()
            playerSlidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

            //onresume to not playing song
            playerViewpager!!.setCurrentItem(MusicPlayerControls.SONG_NUMBER, true)
        }
    }

    fun songNext() {
        if (MusicPlayerControls.SONG_NUMBER < GlobalApp.mediaItemsArrayList.size - 1) {
            if (GlobalApp.sharedpreferences == null) {
                GlobalApp.savePrefrence(requireContext())
            }
            val editor = GlobalApp.sharedpreferences!!.edit()
            editor.putString(
                GlobalApp.PREFREANCE_LAST_SONG_KEY,
                MusicPlayerControls.SONG_NUMBER.toString() + ""
            )
            editor.commit()
            if (GlobalApp.isServiceRunning(
                    MusicService::class.java.name,
                    requireContext()
                )
            ) {
                if (MusicService.isPng()) {
                    MusicService.playSong()
                } else {
                    val intent = Intent(requireContext(), MusicService::class.java)
                    requireActivity().stopService(intent)
                    changeUi(MusicPlayerControls.SONG_NUMBER)
                }
            } else {
                val intent = Intent(requireContext(), MusicService::class.java)
                requireActivity().stopService(intent)
                changeUi(MusicPlayerControls.SONG_NUMBER)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun changeUi(index: Int) {
        try {
            main_default_background = GlobalApp.sharedpreferences!!.getString(
                GlobalApp.PREFREANCE_MAIN_DEFAULT_BACKGROUND,
                GlobalApp.integerArrayList[0].toString() + ""
            )!!
                .toInt()
            if (GlobalApp.mediaItemsArrayList.size > 0 && txt_song_name_footer_layout != null) {
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(requireContext())
                }
                val editor = GlobalApp.sharedpreferences!!.edit()
                editor.putString(GlobalApp.PREFREANCE_LAST_SONG_KEY, index.toString() + "")
                editor.commit()
                txt_song_name_footer_layout.text = GlobalApp.mediaItemsArrayList[index].title
                txt_artist_name_footer_layout.text =
                    "Artist: " + GlobalApp.mediaItemsArrayList[index].artist
                if (MusicService.isPng()) {
                    img_play_music_player.setImageResource(R.drawable.ic_pause_circle_outline)
                    img_play_song_footer_layout.setImageResource(R.drawable.ic_pause_circle_outline)
                } else {
                    img_play_music_player.setImageResource(R.drawable.ic_play_circle_outline)
                    img_play_song_footer_layout.setImageResource(R.drawable.ic_play_circle_outline)
                }
                playerViewpager!!.currentItem = index
                val img_uri: Uri? = GlobalApp.mediaItemsArrayList[index].img_uri
                txt_header!!.text = GlobalApp.mediaItemsArrayList[index].title
                Picasso.get().load(img_uri)
                    .transform(BlurTransformation(requireContext(), 25, 1))
                    .error(main_default_background)
                    .placeholder(main_default_background)
                    .into(img_background_music_player)
                Picasso.get().load(img_uri).error(R.mipmap.splash_icon)
                    .placeholder(R.mipmap.splash_icon).into(nav_header)
                Picasso.get().load(img_uri).resize(70, 70).error(R.mipmap.splash_icon)
                    .placeholder(R.mipmap.splash_icon).into(img_album_footer_layout)
                Picasso.get().load(img_uri).resize(500, 500).error(R.mipmap.splash_icon)
                    .placeholder(R.mipmap.splash_icon).into(circle_player_image)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun changeButton() {
        try {
            if (GlobalApp.isServiceRunning(
                    MusicService::class.java.name,
                    requireContext()
                )
            ) {
                if (MusicService.isPng()) {
                    img_play_music_player.setImageResource(R.drawable.ic_pause_circle_outline)
                    img_play_song_footer_layout.setImageResource(R.drawable.ic_pause_circle_outline)
                } else {
                    img_play_music_player.setImageResource(R.drawable.ic_play_circle_outline)
                    img_play_song_footer_layout.setImageResource(R.drawable.ic_play_circle_outline)
                }
            } else {
                img_play_music_player.setImageResource(R.drawable.ic_play_circle_outline)
                img_play_song_footer_layout.setImageResource(R.drawable.ic_play_circle_outline)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun replaceFragmentWithAnimation(fragment: Fragment?) {
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.framlayout_main, fragment!!)
        transaction.commit()
    }

    fun togggle() {
        if (queueAdapter!!.selectedItemCount > 0) {
            img_delete_list!!.visibility = View.VISIBLE
            img_queue_list!!.visibility = View.GONE
        } else {
            img_queue_list!!.visibility = View.VISIBLE
            img_delete_list!!.visibility = View.GONE
        }
    }

    private fun toggleSelection(position: Int) {
        queueAdapter!!.toggleSelection(position)
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onItemClicked(position: Int) {
        if (queueAdapter!!.selectedItemCount > 0) {
            toggleSelection(position)
        }
    }

    override fun onItemLongClicked(position: Int): Boolean {
        toggleSelection(position)
        return false
    }

    override fun onResume() {
        super.onResume()
        GlobalApp.LAST_SONG =
            GlobalApp.sharedpreferences!!.getString(GlobalApp.PREFREANCE_LAST_SONG_KEY, "")

        if (GlobalApp.LAST_SONG.equals("") || GlobalApp.mediaItemsArrayList.size <= 0) {
            playerSlidingUpPanelLayout!!.panelState = PanelState.HIDDEN
        } else {
            MusicPlayerControls.SONG_NUMBER = GlobalApp.LAST_SONG!!.toInt()
            playerSlidingUpPanelLayout!!.panelState = PanelState.COLLAPSED
            changeUi(MusicPlayerControls.SONG_NUMBER)
            playerViewpager!!.currentItem = MusicPlayerControls.SONG_NUMBER
            queue_id =
                openHelper!!.getCurrent_song(MusicPlayerControls.SONG_NUMBER)
        }

        MusicPlayerControls.PROGRESSBAR_HANDLER = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                val i = msg.obj as Array<Int>
                txt_player_starttime!!.text = GlobalApp.getDuration(i[0].toLong())
                txt_player_endtime!!.text = GlobalApp.getDuration(i[1].toLong())
                if (seekbarTrack) {
                    playerSeekbar!!.progress = i[2]
                    custom_progressBar!!.progress = i[2].toFloat()
                }
            }
        }

        playerSeekbar!!.setOnSeekBarChangeListener(object :
            OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekbarTrack = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (MusicService.isPng()) {
                    try {
                        val totalDuration = MusicService.getDur()
                        val currentPosition = GlobalApp.progressToTimer(
                            seekBar.progress,
                            totalDuration
                        )
                        MusicService.seek(currentPosition)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                seekbarTrack = true
            }
        })

        custom_progressBar!!.setOnSeekBarChangeListener(object :
            OnCircularSeekBarChangeListener {
            override fun onProgressChanged(
                circularSeekBar: CircularSeekBar, progress: Float,
                fromUser: Boolean
            ) {
            }

            override fun onStopTrackingTouch(seekBar: CircularSeekBar) {
                if (MusicService.isPng()) {
                    try {
                        val totalDuration = MusicService.getDur()
                        val currentPosition = GlobalApp.progressToTimer(
                            seekBar.progress.toInt(),
                            totalDuration
                        )
                        MusicService.seek(currentPosition)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                seekbarTrack = true
            }

            override fun onStartTrackingTouch(seekBar: CircularSeekBar) {
                seekbarTrack = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val msg = event.getMessage()
        when (msg[0]) {
            "queue_id" -> {
                this.queue_id = msg[1] as Int
            }
            "RecyclerviewGone" -> {
                queueRecyclerview!!.visibility = View.GONE
            }
            "initialization" -> {
                EventBus.getDefault().post(MessageEvent("drawer", drawer_layout))
            }
            "togggle" -> {
                togggle()
            }
            "fillQueueAdapter" -> {
                fillQueueAdapter()
            }
            "notifyDataSetChanged" -> {
                queueAdapter!!.notifyDataSetChanged()
                viewpageSwipePagerAdapter!!.notifyDataSetChanged()
            }
            "changeUi" -> {
                changeUi(MusicPlayerControls.SONG_NUMBER)
            }
            "clearSelection" -> {
                queueAdapter!!.clearSelection()
            }
            "setPanelState" -> {
                if (playerSlidingUpPanelLayout!! != null) {
                    playerSlidingUpPanelLayout!!.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
                }
            }
            "img_player_background" -> {
                img_background_music_player.setImageResource(
                    msg[1] as Int
                )
            }
            "setCurrentItem" -> {
                playerViewpager!!.currentItem = msg[1] as Int
            }
            "playSong" -> {
                if (playerSlidingUpPanelLayout!! != null && playerSlidingUpPanelLayout!!.panelState == PanelState.HIDDEN) {
                    playerSlidingUpPanelLayout!!.panelState = PanelState.COLLAPSED
                }
            }
            "changeButton" -> {
                changeButton()
            }
            "ActionBarDrawerToggle" -> {
                val toggle = ActionBarDrawerToggle(
                    activity as AppCompatActivity?,
                    drawer, msg[1] as Toolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close
                )
                drawer!!.addDrawerListener(toggle)
                toggle.syncState()
            }
        }
    }


}
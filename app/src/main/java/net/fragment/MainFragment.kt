package net.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import net.adapter.ViewPagerAdapter
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GlobalApp
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainFragment : Fragment() {
    private var sp: SharedPreferences? = null
    var trans_value = 0
    var adapter: ViewPagerAdapter? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.act_main_music, container, false)
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        trans_value =
            sp!!.getInt(GlobalApp.TRANCPARENT_COLOR, GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE)

        initialization(view)
        GlobalApp.savePrefrence(requireActivity())
        setupViewPager(this.viewPager!!)
        tabLayout!!.setupWithViewPager(this.viewPager!!)
        return view
    }

    private fun initialization(view:View) {
        EventBus.getDefault().post(MessageEvent("initialization"))
        tabLayout = view.findViewById<View>(R.id.tablayout) as TabLayout
        viewPager = view.findViewById<View>(R.id.viewpager) as ViewPager
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        EventBus.getDefault().post(MessageEvent("ActionBarDrawerToggle",toolbar))
        viewPager!!.offscreenPageLimit = 1
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = ViewPagerAdapter(childFragmentManager)
        adapter!!.addFrag(SongsFragment(), "SONGS")
        adapter!!.addFrag(ArtistFragment(), "ARTISTS")
        adapter!!.addFrag(AlbumFragment(), "ALBUMS")
        adapter!!.addFrag(GenresFragment(), "GENRES")
        adapter!!.addFrag(RingFragment(), "RING TONES")
//        adapter!!.addFrag(CallRecordFragment(), "CALL RECORDS")
        viewPager.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val msg = event.getMessage()
        when (msg[0]) {
            "drawer" -> {
                val drawer: DrawerLayout = msg[1] as DrawerLayout
                try {
                    val toggle = ActionBarDrawerToggle(
                        activity as AppCompatActivity?,
                        drawer,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                    )
                    drawer.addDrawerListener(toggle)
                    toggle.syncState()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                viewPager!!.offscreenPageLimit = 1
            }
            "setCurrentItem" -> {
                viewPager!!.currentItem = 5
            }
        }
    }
}
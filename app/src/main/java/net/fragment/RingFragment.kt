package net.fragment

import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.Decoration.ItemOffsetDecoration
import net.adapter.RingToneAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.utils.PreferencesUtility
import net.utils.RingLoader
import net.utils.SortOrder

class RingFragment : Fragment() {
    var adapter: RingToneAdapter? = null
    private var mPreferences: PreferencesUtility? = null
    var sp: SharedPreferences? = null
    var menu: Menu? = null


    var songsRecyclerview: RecyclerView? = null
    var progressbar: ProgressBar? = null
    var txt_not_found: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferences = PreferencesUtility.getInstance(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.songs_fragment, container, false)
        setHasOptionsMenu(true)
        songsRecyclerview = view.findViewById<View>(R.id.songsRecyclerview) as RecyclerView
        progressbar = view.findViewById<View>(R.id.progressbar) as ProgressBar
        txt_not_found = view.findViewById<View>(R.id.txt_not_found) as TextView
        txt_not_found!!.text = requireActivity().resources.getString(R.string.ring_not_found)
        songsRecyclerview!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        songsRecyclerview!!.layoutManager = layoutManager
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        songsRecyclerview!!.addItemDecoration(itemDecoration)
        loadSongs().execute("")
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.ringmenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_by_az -> {
                mPreferences!!.songSortOrder = SortOrder.SongSortOrder.SONG_A_Z
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_za -> {
                mPreferences!!.songSortOrder = SortOrder.SongSortOrder.SONG_Z_A
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_artist -> {
                mPreferences!!.songSortOrder = SortOrder.SongSortOrder.SONG_ARTIST
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_album -> {
                mPreferences!!.songSortOrder = SortOrder.SongSortOrder.SONG_ALBUM
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_year -> {
                mPreferences!!.songSortOrder = SortOrder.SongSortOrder.SONG_YEAR
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_duration -> {
                mPreferences!!.songSortOrder = SortOrder.SongSortOrder.SONG_DURATION
                reloadAdapter()
                return true
            }
            R.id.action_search -> {
                GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                    SearchFragment(),
                    "SearchFragment",
                    requireActivity()
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reloadAdapter() {
        Handler().postDelayed({ loadSongs().execute("") }, 50)
    }

    inner class loadSongs : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg p0: String?): String {
            if (activity != null) GlobalApp.ringArrayList = RingLoader.getAllSongs(activity)
            return "Executed"
        }

        override fun onPostExecute(result: String) {
            val resId: Int = R.anim.layout_animation_from_bottom
            val animation = AnimationUtils.loadLayoutAnimation(context, resId)
            songsRecyclerview!!.layoutAnimation = animation
            if (GlobalApp.ringArrayList.size > 0) {
                txt_not_found!!.visibility = View.GONE
                adapter = RingToneAdapter(
                    GlobalApp.ringArrayList,
                    requireContext(),
                    requireActivity()
                )
                songsRecyclerview!!.adapter = adapter
            } else {
                txt_not_found!!.visibility = View.VISIBLE
            }
            if (progressbar != null) progressbar!!.visibility = View.GONE
        }

        override fun onPreExecute() {}
    }

}
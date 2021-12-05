package net.fragment

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.Decoration.ItemOffsetDecoration
import net.adapter.AlbumAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.AlbumModel
import net.utils.AlbumLoader
import net.utils.PreferencesUtility
import net.utils.SortOrder

class AlbumFragment : Fragment() {
    var adapter: AlbumAdapter? = null
    var itemDecoration: ItemOffsetDecoration? = null
    private var mPreferences: PreferencesUtility? = null
    private var isGrid = false
    var layoutManager: GridLayoutManager? = null
    var menu: Menu? = null

    var artistRecyclerview: RecyclerView? = null
    var progressbar: ProgressBar? = null
    var txt_not_found: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferences = PreferencesUtility.getInstance(activity)
        isGrid = mPreferences!!.isAlbumsInGrid
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.artist_fragment, container, false)
        setHasOptionsMenu(true)
        progressbar = view.findViewById<View>(R.id.progressbar) as ProgressBar
        txt_not_found = view.findViewById<View>(R.id.txt_not_found) as TextView
        artistRecyclerview = view.findViewById<View>(R.id.artistRecyclerview) as RecyclerView
        txt_not_found!!.text = requireActivity().resources.getString(R.string.album_not_found)
        artistRecyclerview!!.setHasFixedSize(true)
        itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        artistRecyclerview!!.addItemDecoration(itemDecoration!!)
        setLayoutManager()
        if (activity != null) {
            if (GlobalApp.albumArrayList.size > 0) {
                Handler().postDelayed({
                    txt_not_found!!.visibility = View.GONE
                    adapter =
                        AlbumAdapter(GlobalApp.albumArrayList, requireContext(), requireActivity())
                    artistRecyclerview!!.adapter = adapter
                    if (progressbar != null) progressbar!!.visibility = View.GONE
                }, 50)
            } else {
                Handler().postDelayed({ loadAlbums().execute("") }, 50)
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.albummenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_by_az -> {
                mPreferences!!.albumSortOrder = SortOrder.AlbumSortOrder.ALBUM_A_Z
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_za -> {
                mPreferences!!.albumSortOrder = SortOrder.AlbumSortOrder.ALBUM_Z_A
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_year -> {
                mPreferences!!.albumSortOrder = SortOrder.AlbumSortOrder.ALBUM_YEAR
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_artist -> {
                mPreferences!!.albumSortOrder = SortOrder.AlbumSortOrder.ALBUM_ARTIST
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_number_of_songs -> {
                mPreferences!!.albumSortOrder = SortOrder.AlbumSortOrder.ALBUM_NUMBER_OF_SONGS
                reloadAdapter()
                return true
            }
            R.id.menu_show_as_list -> {
                mPreferences!!.isAlbumsInGrid = false
                isGrid = false
                updateLayoutManager(1)
                return true
            }
            R.id.menu_show_as_grid -> {
                mPreferences!!.isAlbumsInGrid = true
                isGrid = true
                updateLayoutManager(2)
                return true
            }
            R.id.action_search -> {
                GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                    SearchFragment(), "SearchFragment",
                    requireActivity()
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLayoutManager() {
        layoutManager = if (isGrid) {
            GridLayoutManager(activity, 2)
        } else {
            GridLayoutManager(activity, 1)
        }
        artistRecyclerview!!.layoutManager = layoutManager
    }

    private fun updateLayoutManager(column: Int) {
        adapter = AlbumAdapter(GlobalApp.albumArrayList, requireContext(), requireActivity())
        artistRecyclerview!!.adapter = adapter
        layoutManager!!.spanCount = column
        layoutManager!!.requestLayout()
        progressbar!!.visibility = View.GONE
    }

    private fun reloadAdapter() {
        if (activity != null) loadAlbums().execute("")
    }

    inner class loadAlbums :
        AsyncTask<String?, Void?, List<AlbumModel>>() {
        override fun doInBackground(vararg p0: String?): List<AlbumModel> {
            if (activity != null) GlobalApp.albumArrayList =
                AlbumLoader.getAllAlbums(activity)
            return GlobalApp.albumArrayList
        }

        override fun onPostExecute(result: List<AlbumModel>) {
            if (result.isNotEmpty()) {
                txt_not_found!!.visibility = View.GONE
                adapter =
                    AlbumAdapter(GlobalApp.albumArrayList, requireContext(), requireActivity())
                artistRecyclerview!!.adapter = adapter
            } else {
                txt_not_found!!.visibility = View.VISIBLE
            }
            progressbar!!.visibility = View.GONE
        }

        override fun onPreExecute() {}
    }

}
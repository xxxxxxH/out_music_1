package net.fragment


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
import net.adapter.ArtistAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.utils.ArtistLoader
import net.utils.PreferencesUtility
import net.utils.SortOrder

class ArtistFragment : Fragment() {
    var adapter: ArtistAdapter? = null
    var itemDecoration: ItemOffsetDecoration? = null
    private var mPreferences: PreferencesUtility? = null
    private var isGrid = false
    var menu: Menu? = null
    var layoutManager: GridLayoutManager? = null

    var artistRecyclerview: RecyclerView? = null
    var progressbar: ProgressBar? = null
    var txt_not_found: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferences = PreferencesUtility.getInstance(requireActivity())
        isGrid = mPreferences!!.isArtistsInGrid
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
        txt_not_found!!.text = requireActivity().getString(R.string.artist_not_found)
        artistRecyclerview!!.setHasFixedSize(true)
        itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)

        artistRecyclerview!!.addItemDecoration(itemDecoration!!)
        setLayoutManager()
        if (GlobalApp.artistArrayList.isNotEmpty()) {
            Handler().postDelayed({
                txt_not_found!!.visibility = View.GONE
                adapter = ArtistAdapter(
                    GlobalApp.artistArrayList,
                    requireContext(),
                    requireActivity()
                )
                artistRecyclerview!!.adapter = adapter
                if (progressbar != null) progressbar!!.visibility = View.GONE
            }, 50)
        } else {
            Handler().postDelayed({
                loadArtists()
                    .execute("")
            }, 50)
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.artistmenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun reloadAdapter() {
        loadArtists().execute("")
    }

    private fun updateLayoutManager(column: Int) {
        adapter = ArtistAdapter(GlobalApp.artistArrayList, requireContext(), requireActivity())
        artistRecyclerview!!.adapter = adapter
        layoutManager!!.spanCount = column
        layoutManager!!.requestLayout()
        progressbar!!.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_by_az -> {
                mPreferences!!.artistSortOrder = SortOrder.ArtistSortOrder.ARTIST_A_Z
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_za -> {
                mPreferences!!.artistSortOrder = SortOrder.ArtistSortOrder.ARTIST_Z_A
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_number_of_songs -> {
                mPreferences!!.artistSortOrder = SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_SONGS
                reloadAdapter()
                return true
            }
            R.id.menu_sort_by_number_of_albums -> {
                mPreferences!!.artistSortOrder = SortOrder.ArtistSortOrder.ARTIST_NUMBER_OF_ALBUMS
                reloadAdapter()
                return true
            }
            R.id.menu_show_as_list -> {
                mPreferences!!.isArtistsInGrid = false
                isGrid = false
                updateLayoutManager(1)
                return true
            }
            R.id.menu_show_as_grid -> {
                mPreferences!!.isArtistsInGrid = true
                isGrid = true
                updateLayoutManager(2)
                return true
            }
            R.id.action_search -> {
                GlobalApp.sharedInstance(requireActivity())!!
                    .fragmentReplaceTransition(
                        SearchFragment(),
                        "SearchFragment",
                        requireActivity()
                    )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setLayoutManager() {
        layoutManager = if (isGrid) {
            GridLayoutManager(requireActivity(), 2)
        } else {
            GridLayoutManager(requireActivity(), 1)
        }
        artistRecyclerview!!.layoutManager = layoutManager
    }

    inner class loadArtists :
        AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg p0: String?): String {
            GlobalApp.artistArrayList = ArtistLoader.getAllArtists(requireActivity())
            return "Executed"
        }

        override fun onPostExecute(result: String) {
            if (requireActivity() != null) {
                if (GlobalApp.artistArrayList.isNotEmpty()) {
                    txt_not_found!!.visibility = View.GONE
                    adapter = ArtistAdapter(GlobalApp.artistArrayList, context!!, requireActivity())
                    artistRecyclerview!!.adapter = adapter
                } else {
                    txt_not_found!!.visibility = View.VISIBLE
                }
                progressbar!!.visibility = View.GONE
            }
        }

        override fun onPreExecute() {}

    }
}
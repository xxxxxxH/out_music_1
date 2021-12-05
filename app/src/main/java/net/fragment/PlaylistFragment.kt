package net.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.playlist_fragment.*
import net.DataBase.OpenHelper
import net.Decoration.ItemOffsetDecoration
import net.adapter.PlaylistlAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.AlertPlayList
import java.util.*

class PlaylistFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var alertPlayListArrayList: ArrayList<AlertPlayList>? = null
    var openHelper: OpenHelper? = null
    var artistDetailAdapter: PlaylistlAdapter? = null
    var appBarLayout: AppBarLayout? = null
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
        val view = inflater.inflate(R.layout.playlist_fragment, container, false)
        alertPlayListArrayList = ArrayList<AlertPlayList>()
        openHelper = OpenHelper.sharedInstance(context)
        initialization()
        alertPlayListArrayList = openHelper!!.playlist
        if (alertPlayListArrayList!!.size > 0) {
            artistDetailAdapter =
                PlaylistlAdapter(alertPlayListArrayList!!, requireContext(), requireActivity())
            recyclerView!!.adapter = artistDetailAdapter
            txt_no_found!!.visibility = View.GONE
        } else {
            txt_no_found!!.visibility = View.VISIBLE
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun initialization() {
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            requireActivity().resources.getString(R.string.my_playlist)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = GridLayoutManager(context, 2)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        recyclerView!!.addItemDecoration(itemDecoration)
    }
}

package net.fragment

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
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

    var txt_no_found: TextView? = null
    var toolbar: Toolbar? = null
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
        initialization(view)
        alertPlayListArrayList = openHelper!!.playlist
        if (alertPlayListArrayList!!.size > 0) {
            artistDetailAdapter =
                PlaylistlAdapter(alertPlayListArrayList!!, requireContext(), requireActivity())
            recyclerView!!.adapter = artistDetailAdapter
            txt_no_found!!.visibility = View.GONE
        } else {
            txt_no_found!!.visibility = View.VISIBLE
        }
        toolbar!!.setNavigationOnClickListener { (activity as AppCompatActivity).supportFragmentManager.popBackStack() }

        toolbar!!.menu.clear()
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun initialization(view:View) {
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar!!.title = requireActivity().resources.getString(R.string.my_playlist)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)

        recyclerView = view.findViewById<View>(R.id.recyclerview) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = GridLayoutManager(context, 2)
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        recyclerView!!.addItemDecoration(itemDecoration)

        appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
        txt_no_found = view.findViewById<View>(R.id.txt_no_found) as TextView
    }
}

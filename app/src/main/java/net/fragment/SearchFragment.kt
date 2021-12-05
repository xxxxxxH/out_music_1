package net.fragment

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import net.Decoration.ItemOffsetDecoration
import net.adapter.SearchAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import java.util.*

class SearchFragment : Fragment() {
    var mediaItemsArrayList: ArrayList<SongsModel>? = null
    var searchAdapter: SearchAdapter? = null


    var searchRecyclerView: RecyclerView? = null
    var ed_search: EditText? = null
    var toolbar: Toolbar? = null
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
    ): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        setHasOptionsMenu(true)
        mediaItemsArrayList = ArrayList<SongsModel>()

        searchRecyclerView = view.findViewById<View>(R.id.searchRecyclerview) as RecyclerView
        ed_search = view.findViewById<View>(R.id.ed_search) as EditText
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        searchRecyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        searchRecyclerView!!.layoutManager = layoutManager
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        searchRecyclerView!!.addItemDecoration(itemDecoration)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        searchAdapter = SearchAdapter(mediaItemsArrayList!!, requireActivity(), requireContext())
        searchRecyclerView!!.adapter = searchAdapter
        ed_search!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s1 = ed_search!!.text.toString().trim { it <= ' ' }
                getParticularSearchSongs(s1 + "", context)
            }

            override fun afterTextChanged(s: Editable) {
                val s1 = ed_search!!.text.toString().trim { it <= ' ' }
                if (s1.length == 0) {
                    try {
                        mediaItemsArrayList!!.clear()
                        searchAdapter!!.notifyDataSetChanged()
                    } catch (e: Exception) {
                        searchAdapter = SearchAdapter(
                            mediaItemsArrayList!!,
                            requireActivity(),
                            requireContext()
                        )
                        searchRecyclerView!!.adapter = searchAdapter
                        e.printStackTrace()
                    }
                }
            }
        })

        toolbar!!.setNavigationOnClickListener {
            if (view != null) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
        }
        return view
    }

    fun getParticularSearchSongs(searchTitle: String, context: Context?) {
        mediaItemsArrayList!!.clear()
        val c = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Audio.Media.TITLE + " like ? ",
            arrayOf(
                "%$searchTitle%"
            ),
            null
        )
        if (c!!.moveToFirst() && c.count > 0) {
            do {
                val mediaItem = SongsModel()
                val _id = c.getLong(c.getColumnIndex("_id"))
                val title = c.getString(c.getColumnIndex("title"))
                val albumName = c.getString(c.getColumnIndex("album"))
                val duration = c.getLong(c.getColumnIndex("duration"))
                val artist = c.getString(c.getColumnIndex("artist"))
                val data = c.getString(c.getColumnIndex("_data"))
                val albumId = c.getLong(c.getColumnIndex("album_id"))
                val artistId = c.getLong(c.getColumnIndex("artist_id"))
                val size = c.getString(c.getColumnIndex("_size"))
                mediaItem.song_id = _id
                mediaItem.title = title
                mediaItem.album = albumName
                mediaItem.artist = artist
                mediaItem.duration = duration
                mediaItem.path = data
                mediaItem.img_uri = GlobalApp.getImgUri(artistId)
                mediaItem.albumId = albumId
                mediaItem.size = size
                if (c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)) != "audio/amr") {
                    mediaItemsArrayList!!.add(mediaItem)
                }
            } while (c.moveToNext())
            try {
                if (mediaItemsArrayList!!.size > 0) {
                    searchAdapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            searchAdapter =
                SearchAdapter(mediaItemsArrayList!!, requireActivity(), requireContext())
            searchRecyclerView!!.adapter = searchAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }
}

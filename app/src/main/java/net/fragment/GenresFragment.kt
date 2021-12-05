package net.fragment

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.Decoration.ItemOffsetDecoration
import net.adapter.GenresAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.GenersModel
import net.utils.PreferencesUtility
import java.util.*

class GenresFragment : Fragment() {
    var adapter: GenresAdapter? = null
    var itemDecoration: ItemOffsetDecoration? = null
    private var isGrid = false
    var layoutManager: GridLayoutManager? = null
    private var mPreferences: PreferencesUtility? = null
    var menu: Menu? = null

    var artistRecyclerview: RecyclerView? = null
    var progressbar: ProgressBar? = null
    var txt_not_found: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreferences = PreferencesUtility.getInstance(activity)
        isGrid = mPreferences!!.isGenerInGrid

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.artist_fragment, container, false)
        setHasOptionsMenu(true)
        progressbar = view.findViewById<View>(R.id.progressbar) as ProgressBar
        txt_not_found = view.findViewById<View>(R.id.txt_not_found) as TextView
        artistRecyclerview = view.findViewById<View>(R.id.artistRecyclerview) as RecyclerView
        txt_not_found!!.text = requireActivity().resources.getString(R.string.gener_not_found)
        artistRecyclerview!!.setHasFixedSize(true)
        itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        artistRecyclerview!!.addItemDecoration(itemDecoration!!)
        setLayoutManager()
        if (activity != null) {
            if (GlobalApp.generArrayList.size > 0) {
                Handler().postDelayed({
                    txt_not_found!!.visibility = View.GONE
                    adapter =
                        GenresAdapter(GlobalApp.generArrayList, requireContext(), requireActivity())
                    artistRecyclerview!!.adapter = adapter
                    if (progressbar != null) progressbar!!.visibility = View.GONE
                }, 50)
            } else {
                Handler().postDelayed({
                    FetchGenerList().executeOnExecutor(
                        AsyncTask.THREAD_POOL_EXECUTOR,
                        null as Void?
                    )
                }, 50)
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        this.menu = menu
        inflater.inflate(R.menu.genermenu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_show_as_list -> {
                mPreferences!!.isGenerInGrid = false
                isGrid = false
                updateLayoutManager(1)
                return true
            }
            R.id.menu_show_as_grid -> {
                mPreferences!!.isGenerInGrid = true
                isGrid = true
                updateLayoutManager(2)
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

    private fun setLayoutManager() {
        layoutManager = if (isGrid) {
            GridLayoutManager(activity, 2)
        } else {
            GridLayoutManager(activity, 1)
        }
        artistRecyclerview!!.layoutManager = layoutManager
    }

    private fun updateLayoutManager(column: Int) {
        adapter = GenresAdapter(GlobalApp.generArrayList, requireContext(), requireActivity())
        artistRecyclerview!!.adapter = adapter
        layoutManager!!.spanCount = column
        layoutManager!!.requestLayout()
        progressbar!!.visibility = View.GONE
    }

    inner class FetchGenerList :
        AsyncTask<Void?, Void?, ArrayList<GenersModel>>() {
        override fun doInBackground(vararg p0: Void?): ArrayList<GenersModel> {
            var albumIDLong = 0.toLong()
            var generId: Long
            var song_count = 0
            val cursor = context!!.contentResolver.query(
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID),
                null,
                null,
                ""
            )
            try {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            try {
                                val genres = GenersModel()
                                genres.generName = cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                        MediaStore.Audio.Genres.NAME
                                    )
                                )
                                genres.generId = cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                        MediaStore.Audio.Genres._ID
                                    )
                                ).toLong()

                                generId =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID))
                                        .toLong()
                                val c = context!!.contentResolver.query(
                                    MediaStore.Audio.Genres.Members.getContentUri(
                                        "external",
                                        generId
                                    ),
                                    arrayOf(
                                        "title",
                                        "_data",
                                        "duration",
                                        "_id",
                                        "album",
                                        "artist",
                                        "album_id"
                                    ),
                                    null,
                                    null,
                                    null
                                )
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        albumIDLong = c.getLong(c.getColumnIndex("album_id"))
                                        val img_uri: Uri = GlobalApp.getImgUri(albumIDLong)!!
                                        song_count = c.count
                                        genres.generUri = img_uri
                                        genres.albumId = albumIDLong
                                        genres.songCount = song_count
                                    }
                                }
                                if (albumIDLong != 0L && song_count > 0) {
                                    GlobalApp.generArrayList.add(genres)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                }
            } finally {
                cursor?.close()
            }
            return GlobalApp.generArrayList
        }

        override fun onPostExecute(result: ArrayList<GenersModel>) {
            super.onPostExecute(result)
            if (result.size > 0) {
                txt_not_found!!.visibility = View.GONE
                adapter =
                    GenresAdapter(GlobalApp.generArrayList, requireContext(), requireActivity())
                artistRecyclerview!!.adapter = adapter
            } else {
                txt_not_found!!.visibility = View.VISIBLE
            }
            if (progressbar != null) progressbar!!.visibility = View.GONE
        }
    }
}
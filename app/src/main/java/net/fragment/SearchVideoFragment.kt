package net.fragment

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
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
import net.Listner.OnItemClickListener
import net.adapter.VideoAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.model.Videos
import java.util.*

/**
 * Copyright (C) 2021,2021/12/7, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
class SearchVideoFragment : Fragment(),
    OnItemClickListener {
    var mediaItemsArrayList: ArrayList<Videos?>? = null
    var searchRecyclerView: RecyclerView? = null
    var ed_search: EditText? = null
    var videoAdapter: VideoAdapter? = null
    var toolbar: Toolbar? = null
    private var sp: SharedPreferences? = null
    var appBarLayout: AppBarLayout? = null
    var onItemClickListener: OnItemClickListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.search_video_fragment, container, false)
        setHasOptionsMenu(true)
        onItemClickListener = this
        mediaItemsArrayList = ArrayList<Videos?>()
        searchRecyclerView = view!!.findViewById<View>(R.id.searchRecyclerview) as RecyclerView
        searchRecyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        searchRecyclerView!!.layoutManager = layoutManager
        val itemDecoration = ItemOffsetDecoration(requireContext(), R.dimen.item_offset)
        searchRecyclerView!!.addItemDecoration(itemDecoration)
        ed_search = view.findViewById<View>(R.id.ed_search) as EditText
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar!!.title = "Search"
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        videoAdapter = VideoAdapter(requireContext(), mediaItemsArrayList!!, onItemClickListener)
        searchRecyclerView!!.adapter = videoAdapter
        ed_search!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s1 = ed_search!!.text.toString().trim { it <= ' ' }
                getParticularSearchVideo(s1 + "", context)
            }

            override fun afterTextChanged(s: Editable) {
                val s1 = ed_search!!.text.toString().trim { it <= ' ' }
                if (s1.isEmpty()) {
                    try {
                        mediaItemsArrayList!!.clear()
                        videoAdapter!!.notifyDataSetChanged()
                    } catch (e: Exception) {
                        videoAdapter =
                            VideoAdapter(
                                requireContext(),
                                mediaItemsArrayList!!,
                                onItemClickListener
                            )
                        searchRecyclerView!!.adapter = videoAdapter
                        e.printStackTrace()
                    }
                }
            }
        })
        toolbar!!.setNavigationOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            (activity as AppCompatActivity?)!!.supportFragmentManager.popBackStack()
        }
        return view
    }

    fun getParticularSearchVideo(searchTitle: String, context: Context?) {
        mediaItemsArrayList!!.clear()
        var cursor: Cursor? = null
        cursor = if (searchTitle != "") {
            requireContext().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.RESOLUTION,
                    MediaStore.Video.VideoColumns.DATE_TAKEN,
                    MediaStore.Video.VideoColumns.DATE_ADDED
                ),
                MediaStore.Audio.Media.DISPLAY_NAME + " like ? ",
                arrayOf(
                    "%$searchTitle%"
                ),
                null
            )
        } else {
            requireContext().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Video.Media._ID,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Video.VideoColumns.DURATION,
                    MediaStore.Video.VideoColumns.RESOLUTION,
                    MediaStore.Video.VideoColumns.DATE_TAKEN,
                    MediaStore.Video.VideoColumns.DATE_ADDED,
                    MediaStore.Video.VideoColumns.DISPLAY_NAME
                ),
                null,
                null,
                null
            )
        }
        if (cursor!!.moveToFirst()) {
            do {
                val newVVI = Videos()
                newVVI._id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
                newVVI.filePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"))
                newVVI.title = cursor.getString(cursor.getColumnIndexOrThrow("_display_name"))
                newVVI.duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"))
                newVVI.resolution = cursor.getString(cursor.getColumnIndexOrThrow("resolution"))
                newVVI.date = cursor.getString(cursor.getColumnIndexOrThrow("datetaken"))
                mediaItemsArrayList!!.add(newVVI)
            } while (cursor.moveToNext())
            try {
                if (mediaItemsArrayList!!.size > 0) {
                    videoAdapter!!.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            videoAdapter =
                VideoAdapter(requireContext(), mediaItemsArrayList!!, onItemClickListener)
            searchRecyclerView!!.adapter = videoAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onClick(view: View, position: Int) {
        when (view.id) {
            R.id.lin_menu -> GlobalApp.popupWindow(
                view, requireActivity(),
                mediaItemsArrayList!![position]!!, position, "video", videoAdapter!!
            )
            else -> {
            }
        }
    }
}

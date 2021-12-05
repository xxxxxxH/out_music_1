package net.fragment

import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.Decoration.ItemOffsetDecoration
import net.adapter.SongsAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.utils.PreferencesUtility
import net.utils.SongLoader

class SongsFragment : Fragment() {
    var adapter: SongsAdapter? = null
    private var mPreferences: PreferencesUtility? = null

    //===================== Equalizer ==================================
    var equalizer: Equalizer? = null
    var isEqualizer = false
    var band1Pos = 0
    var band2Pos = 0
    var band3Pos = 0
    var band4Pos = 0
    var band5Pos = 0
    var band6Pos = 0
    var band7Pos = 0
    var band8Pos = 0
    var bassBoost: BassBoost? = null
    var bassBoostPos = 0
    var numBands = 0
    var maxLevel = 100
    var minLevel = 0
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
    ): View {
        val view: View = inflater.inflate(R.layout.songs_fragment, container, false)
        setHasOptionsMenu(true)
        songsRecyclerview = view.findViewById<View>(R.id.songsRecyclerview) as RecyclerView
        progressbar = view.findViewById<View>(R.id.progressbar) as ProgressBar
        txt_not_found = view.findViewById<View>(R.id.txt_not_found) as TextView
        txt_not_found!!.text = requireActivity().resources.getString(R.string.song_not_found)
        songsRecyclerview!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        songsRecyclerview!!.layoutManager = layoutManager
        val itemDecoration = ItemOffsetDecoration(requireActivity(), R.dimen.item_offset)
        songsRecyclerview!!.addItemDecoration(itemDecoration)
        setEqualizer()
        if (GlobalApp.songsArrayList.size == 0) {
            loadSongs().execute("")
        } else {
            Handler().postDelayed({
                val resId: Int = R.anim.layout_animation_from_bottom
                val animation = AnimationUtils.loadLayoutAnimation(context, resId)
                songsRecyclerview!!.layoutAnimation = animation
                txt_not_found!!.visibility = View.GONE
                adapter = SongsAdapter(
                    GlobalApp.songsArrayList,
                    (context as AppCompatActivity?)!!, (activity as AppCompatActivity?)!!
                )
                songsRecyclerview!!.adapter = adapter
                if (progressbar != null) progressbar!!.visibility = View.GONE
            }, 100)
        }
        return view
    }

    fun setEqualizer() {
        sp = requireContext().getSharedPreferences(
            requireContext().getString(R.string.preference_file_key),
            0
        )
        isEqualizer = sp!!.getBoolean(GlobalApp.IS_EQUALIZER, false)
        bassBoostPos = sp!!.getInt(GlobalApp.BASS_BOOST, 0)
        band1Pos = sp!!.getInt(GlobalApp.BAND1, 50)
        band2Pos = sp!!.getInt(GlobalApp.BAND2, 50)
        band3Pos = sp!!.getInt(GlobalApp.BAND3, 50)
        band4Pos = sp!!.getInt(GlobalApp.BAND4, 50)
        band5Pos = sp!!.getInt(GlobalApp.BAND5, 50)
        band6Pos = sp!!.getInt(GlobalApp.BAND6, 50)
        band7Pos = sp!!.getInt(GlobalApp.BAND7, 50)
        band8Pos = sp!!.getInt(GlobalApp.BAND8, 50)
        numBands = 0
        if (isEqualizer) {
            equalizer = Equalizer(0, 0)
            if (equalizer != null) {
                equalizer!!.enabled = isEqualizer
                numBands = equalizer!!.numberOfBands.toInt()
                val r = equalizer!!.bandLevelRange
                minLevel = r[0].toInt()
                maxLevel = r[1].toInt()
                bassBoost = BassBoost(0, 0)
                if (bassBoost != null) {
                    bassBoost!!.enabled = bassBoostPos > 0
                    bassBoost!!.setStrength(bassBoostPos.toShort())
                }
                for (i in 0 until numBands) {
                    var level = 0
                    if (i == 0) {
                        level = band1Pos
                    } else if (i == 1) {
                        level = band2Pos
                    } else if (i == 2) {
                        level = band3Pos
                    } else if (i == 3) {
                        level = band4Pos
                    } else if (i == 4) {
                        level = band5Pos
                    } else if (i == 5) {
                        level = band6Pos
                    } else if (i == 6) {
                        level = band7Pos
                    } else if (i == 7) {
                        level = band8Pos
                    }
                    equalizer!!.setBandLevel(
                        i.toShort(),
                        (minLevel + (maxLevel - minLevel) * level / 100).toShort()
                    )
                }
            }
        } else {
            if (bassBoost != null) {
                bassBoost!!.enabled = false
            }
        }
    }

    inner class loadSongs : AsyncTask<String?, Void?, String>() {

        override fun onPostExecute(result: String) {
            val resId: Int = R.anim.layout_animation_from_bottom
            val animation = AnimationUtils.loadLayoutAnimation(context, resId)
            songsRecyclerview!!.layoutAnimation = animation
            if (GlobalApp.songsArrayList.size > 0) {
                txt_not_found!!.visibility = View.GONE
                adapter = SongsAdapter(
                    GlobalApp.songsArrayList,
                    (context as AppCompatActivity?)!!, (activity as AppCompatActivity?)!!
                )
                songsRecyclerview!!.adapter = adapter
            } else {
                txt_not_found!!.visibility = View.VISIBLE
            }
            if (progressbar != null) progressbar!!.visibility = View.GONE
        }

        override fun onPreExecute() {}
        override fun doInBackground(vararg p0: String?): String {
            if (activity != null) GlobalApp.songsArrayList = SongLoader.getAllSongs(activity)
            return "Executed"
        }
    }
}
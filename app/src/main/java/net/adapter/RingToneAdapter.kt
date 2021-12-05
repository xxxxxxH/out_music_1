package net.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.Controls.MusicPlayerControls
import net.DataBase.OpenHelper
import net.Services.MusicService
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import java.util.*

class RingToneAdapter(data: ArrayList<SongsModel>?, context: Context, activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val dataSet: ArrayList<SongsModel>?
    var context: Context
    var openHelper: OpenHelper
    var activity: Activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_song_item, parent, false)
            return VHItem(view)
        } else if (viewType == TYPE_HEADER) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_fragment_header, parent, false)
            return VHHeader(view)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VHItem) {
            val dataItem: SongsModel = getItem(position)
            //cast holder to VHItem and set data
            holder.txt_song_name.setText(dataItem.title)
            holder.txt_song_artist.setText(dataItem.artist)
            Picasso.get().load(dataItem.img_uri).placeholder(R.drawable.musicalicon)
                .error(R.drawable.musicalicon).into(
                    holder.img_song
                )
            holder.cardView.setOnClickListener {
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                if (dataSet != null) {
                    if (MusicService.player != null) {
                        MusicService.player.reset()
                    }
                    MusicPlayerControls.startSongsWithQueue(context, dataSet, position - 1, "songs")
                }
                GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
            }
            holder.img_song.setOnClickListener {
                if (dataSet != null) {
                    MusicPlayerControls.startSongsWithQueue(context, dataSet, position, "songs")
                }
                GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
            }
            holder.linear_more.setOnClickListener {
                GlobalApp.showPopUp(
                    holder.img_more,
                    context,
                    activity,
                    dataItem
                )
            }
        } else if (holder is VHHeader) {
            //cast holder to VHHeader and set data for header.
            holder.txt_totalSongs.text = (itemCount - 1).toString() + ""
        }
    }

    override fun getItemCount(): Int {
        return dataSet!!.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) TYPE_HEADER else TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun getItem(position: Int): SongsModel {
        return dataSet!![position - 1]
    }

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_song_name: TextView
        var txt_song_artist: TextView
        var img_song: ImageView
        var cardView: CardView
        var linear_more: LinearLayout
        var img_more: ImageView

        init {
            txt_song_name = itemView.findViewById<View>(R.id.txt_song_name) as TextView
            txt_song_artist = itemView.findViewById<View>(R.id.txt_songs_artist) as TextView
            img_song = itemView.findViewById<View>(R.id.img_song) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as CardView
            linear_more = itemView.findViewById<View>(R.id.linear_more) as LinearLayout
            img_more = itemView.findViewById<View>(R.id.img_more) as ImageView
        }
    }

    internal inner class VHHeader(finalRow: View) : RecyclerView.ViewHolder(finalRow) {
        var txt_totalSongs: TextView

        init {
            txt_totalSongs = finalRow.findViewById<View>(R.id.txt_totalSongs) as TextView
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    init {
        dataSet = data
        this.context = context
        openHelper = OpenHelper(context)
        this.activity = activity
    }
}


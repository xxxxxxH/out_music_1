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
//import com.google.android.gms.ads.InterstitialAd
import com.squareup.picasso.Picasso
import net.Controls.MusicPlayerControls
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import java.util.*

class PlaylistDetailAdapter(data: ArrayList<SongsModel>?, context: Context, activity: Activity) :
    RecyclerView.Adapter<PlaylistDetailAdapter.MyViewHolder>() {
    var context: Context
    var activity: Activity
//    var mInterstitialAd: InterstitialAd? = null
    var temp_pos = 0
    var temp_holder: RecyclerView.ViewHolder? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_song_name: TextView
        var txt_song_artist: TextView
        var img_song: ImageView
        var cardView: CardView
        var linear_more: LinearLayout

        init {
            txt_song_name = itemView.findViewById<View>(R.id.txt_song_name) as TextView
            txt_song_artist = itemView.findViewById<View>(R.id.txt_songs_artist) as TextView
            img_song = itemView.findViewById<View>(R.id.img_song) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as CardView
            linear_more = itemView.findViewById<View>(R.id.linear_more) as LinearLayout
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_song_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, listPosition: Int) {
        try {
            holder.txt_song_name.text = dataSet!![listPosition].title
        } catch (e: Exception) {
            holder.txt_song_name.text = "Untitiled Song"
            e.printStackTrace()
        }
        try {
            holder.txt_song_artist.text = dataSet!![listPosition].artist
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            Picasso.get().load(dataSet!![listPosition].img_uri)
                .placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon)
                .into(holder.img_song)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.cardView.setOnClickListener {
            if (dataSet != null) {
                GlobalApp.ads_count =
                    GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
                MusicPlayerControls.startSongsWithQueue(
                    context,
                    dataSet,
                    listPosition,
                    "playlistdetail"
                )
            }
        }
        holder.img_song.setOnClickListener {
            if (dataSet != null) {
                GlobalApp.ads_count =
                    GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
                MusicPlayerControls.startSongsWithQueue(
                    context,
                    dataSet,
                    listPosition,
                    "playlistdetail"
                )
            }
        }
        holder.linear_more.setOnClickListener {
            GlobalApp.showPopUp(
                holder.linear_more, context, activity,
                dataSet!![listPosition]
            )
        }
    }

    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    companion object {
        var dataSet: ArrayList<SongsModel>? = null
    }

    init {
        dataSet = data
        this.context = context
        this.activity = activity
    }
}

package net.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.Controls.MusicPlayerControls
import net.Services.MusicService
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GlobalApp
import net.model.SongsModel
import org.greenrobot.eventbus.EventBus
import java.util.*

class QueueAdapter(
    data: ArrayList<SongsModel>?,
    var context: Context,
    var activity: Activity,
    private val clickListener: MyViewHolder.ClickListener
) :
    SelectableAdapter<QueueAdapter.MyViewHolder?>() {
    class MyViewHolder(itemView: View, private val listener: ClickListener?) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
        var txt_song_name: TextView
        var txt_song_artist: TextView
        var img_song: ImageView
        var cardView: CardView
        var linear_more: LinearLayout
        var selectedOverlay: View
        override fun onClick(v: View) {
            listener?.onItemClicked(adapterPosition)
        }

        override fun onLongClick(v: View): Boolean {
            return listener?.onItemLongClicked(adapterPosition) ?: false
        }

        interface ClickListener {
            fun onItemClicked(position: Int)
            fun onItemLongClicked(position: Int): Boolean
        }

        init {
            txt_song_name = itemView.findViewById<View>(R.id.txt_song_name) as TextView
            txt_song_artist = itemView.findViewById<View>(R.id.txt_songs_artist) as TextView
            img_song = itemView.findViewById<View>(R.id.img_song) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as CardView
            linear_more = itemView.findViewById<View>(R.id.linear_more) as LinearLayout
            selectedOverlay = itemView.findViewById(R.id.selected_overlay) as View
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_song_item, parent, false)
        return MyViewHolder(
            view,
            clickListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, listPosition: Int) {
        holder.txt_song_name.text = GlobalApp.mediaItemsArrayList[listPosition].title
        holder.txt_song_artist.text = GlobalApp.mediaItemsArrayList[listPosition].artist
        Picasso.get().load(GlobalApp.mediaItemsArrayList[listPosition].img_uri)
            .placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(holder.img_song)
        if (MusicPlayerControls.SONG_NUMBER === listPosition) {
            val queue_id: Int = GlobalApp.mediaItemsArrayList[listPosition].queue_id
            EventBus.getDefault().post(MessageEvent("queue_id", queue_id))
            holder.cardView.setBackgroundColor(context.resources.getColor(R.color.queue_current_play))
        } else {
            holder.cardView.setBackgroundColor(Color.parseColor("#00000000"))
        }
        holder.cardView.setOnClickListener {
            GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
            if (GlobalApp.sharedpreferences == null) {
                GlobalApp.savePrefrence(context)
            }
            val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
            editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
            editor1.commit()
            if (getSelectedItems() == 0) {
                if (!GlobalApp.isServiceRunning(MusicService::class.java.name, context)) {
                    val musIntent = Intent(context, MusicService::class.java)
                    context.startService(musIntent)
                }
                MusicPlayerControls.SONG_NUMBER = listPosition
                Handler().postDelayed({ MusicService.playSong() }, 100)
                notifyDataSetChanged()
                holder.cardView.setBackgroundColor(context.resources.getColor(R.color.queue_current_play))
                //SongsMainFragment.queueRecyclerview.setVisibility(View.GONE)
                EventBus.getDefault().post(MessageEvent("RecyclerviewGone"))
            } else {
                toggleSelection(listPosition)
            }
        }
        holder.linear_more.setOnClickListener {
            GlobalApp.showPopUp(
                holder.linear_more,
                context,
                activity,
                GlobalApp.mediaItemsArrayList.get(listPosition)
            )
        }
        holder.selectedOverlay.visibility =
            if (isSelected(listPosition)) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount(): Int {
        return GlobalApp.mediaItemsArrayList.size
    }
}
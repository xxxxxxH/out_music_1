package net.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.Controls.MusicPlayerControls
import net.DataBase.OpenHelper
import net.Services.MusicService
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GeneralFunction
import net.general.GlobalApp
import net.general.GlobalApp.mediaItemsArrayList
import net.model.SongsModel
import org.greenrobot.eventbus.EventBus
import java.util.*

class SearchAdapter(data: ArrayList<SongsModel>, activity: Activity, context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Context
    var activity: Activity

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_song_name: TextView
        var txt_song_artist: TextView
        var img_song: ImageView
        var linear_more: LinearLayout
        var cardView: CardView

        init {
            txt_song_name = itemView.findViewById<View>(R.id.txt_song_name) as TextView
            txt_song_artist = itemView.findViewById<View>(R.id.txt_songs_artist) as TextView
            img_song = itemView.findViewById<View>(R.id.img_song) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as CardView
            linear_more = itemView.findViewById<View>(R.id.linear_more) as LinearLayout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_song_item, parent, false)
            return MyViewHolder(view)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, listPosition: Int) {
        if (holder is MyViewHolder && dataSet!!.size > 0) {
            if (dataSet!![listPosition] != null) {
                holder.txt_song_name.text = dataSet!![listPosition]!!.title
                holder.txt_song_artist.text = dataSet!![listPosition]!!.artist
                Picasso.get().load(dataSet!![listPosition]!!.img_uri)
                    .placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(
                        holder.img_song
                    )
            }
            holder.linear_more.setOnClickListener {
                showPopUp(
                    holder.linear_more, context, activity,
                    dataSet!![listPosition]
                )
            }
            holder.cardView.setOnClickListener {
                GlobalApp.ads_count =
                    GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
                if (dataSet != null) {
                    if (MusicService.player != null) {
                        MusicService.player.reset()
                    }
                    MusicPlayerControls.startSongsWithQueue(
                        context,
                        dataSet,
                        listPosition,
                        "search"
                    )
                }
            }
            holder.img_song.setOnClickListener {
                GlobalApp.ads_count =
                    GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
                if (dataSet != null) {
                    if (MusicService.player != null) {
                        MusicService.player.reset()
                    }
                    MusicPlayerControls.startSongsWithQueue(
                        context,
                        dataSet,
                        listPosition,
                        "search"
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataSet!!.size > 0) {
            dataSet!!.size
        } else 0
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    companion object {
        private var dataSet: ArrayList<SongsModel>? = null
        private const val TYPE_ITEM = 1
        fun showPopUp(view: View?, context: Context?, activity: Activity, mediaItems: SongsModel?) {
            val popupMenu = PopupMenu(
                activity,
                view!!
            )
            val menuInflater = popupMenu.menuInflater
            menuInflater.inflate(R.menu.popup_menu_music, popupMenu.menu)
            popupMenu.show()
            val openHelper: OpenHelper = OpenHelper.sharedInstance(context)
            val ifPlaylistsongExist: Boolean = openHelper.isPlaylist(mediaItems!!.song_id)
            val ifQueuesongExist: Boolean = openHelper.isQueuelist(mediaItems.song_id)
            if (!ifPlaylistsongExist) {
                popupMenu.menu.getItem(1).title = "Add to Playlist"
            } else {
                popupMenu.menu.getItem(1).title = "Remove from Playlist"
            }
            if (!ifQueuesongExist) {
                popupMenu.menu.getItem(0).title = "Add to Queue"
            } else {
                popupMenu.menu.getItem(0).title = "Remove from Queue"
            }
            popupMenu.setOnMenuItemClickListener { item ->
                if (item.title == "Add to Queue") {
                    openHelper.insertQueue(
                        mediaItems.song_id,
                        mediaItems.img_uri.toString() + "",
                        mediaItems.title,
                        mediaItems.path,
                        mediaItems.artist,
                        mediaItems.size
                    )
                    mediaItemsArrayList.add(mediaItems)
//                    SongsMainFragment.fillQueueAdapter()
                    EventBus.getDefault().post(MessageEvent("fillQueueAdapter"))
                } else if (item.title == "Remove from Queue") {
                    openHelper.deleteQueueSong(mediaItems.song_id)
                    if (mediaItemsArrayList[MusicPlayerControls.SONG_NUMBER]
                            .song_id != mediaItems.song_id
                    ) {
                        mediaItemsArrayList = openHelper.getQueueData(context)
//                        SongsMainFragment.queueAdapter.notifyDataSetChanged()
//                        SongsMainFragment.viewpageSwipePagerAdapter.notifyDataSetChanged()
                        EventBus.getDefault().post(MessageEvent("notifyDataSetChanged"))
                    } else {
                        Toast.makeText(context, "song currently playing", Toast.LENGTH_SHORT).show()
                    }
                } else if (item.title == "Property") {
                    propertyDialog(
                        activity,
                        mediaItems.path,
                        mediaItems.size
                    )
                } else if (item.title == "Add to Playlist") {
                    GeneralFunction.songAddToPlaylist(activity, mediaItems)
                } else if (item.title == "Remove from Playlist") {
                    openHelper.deletePlayListSong(mediaItems.song_id)
                    if (PlaylistDetailAdapter.dataSet != null) {
//                        PlaylistDetailAdapter.dataSet =
//                            openHelper.getPlayListData(PlaylistDetaillFragment.playlistId, context)
//                        PlaylistDetaillFragment.playlistDetailAdapter.notifyDataSetChanged()
                        EventBus.getDefault().post(MessageEvent("PlaylistDetaillFragment"))
                    }
                } else if (item.title == "Share") {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "audio"
                    share.putExtra(
                        Intent.EXTRA_STREAM,
                        Uri.parse("file:///" + mediaItems.path)
                    )
                    activity.startActivity(Intent.createChooser(share, "Share Music"))
                }
                false
            }
        }

        fun propertyDialog(activity: Activity, path: String?, size: String) {
            val inflater = LayoutInflater.from(activity.applicationContext)
            val view: View = inflater.inflate(R.layout.poperty_alert_dialog, null, false)
            val txt_path = view.findViewById<View>(R.id.txt_path) as TextView
            val txt_size = view.findViewById<View>(R.id.txt_size) as TextView
            txt_path.text = path
            txt_size.text = GlobalApp.toNumInUnits(size.toLong())
            val propertyAlertBuilder = AlertDialog.Builder(activity)
            propertyAlertBuilder.setTitle("Property")
            propertyAlertBuilder.setView(view)
            propertyAlertBuilder.setPositiveButton("ok", null)
            propertyAlertBuilder.show()
        }
    }

    init {
        dataSet = data
        this.context = context
        this.activity = activity
    }
}

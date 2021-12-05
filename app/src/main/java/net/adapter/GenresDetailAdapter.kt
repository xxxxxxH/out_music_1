package net.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.Controls.MusicPlayerControls
import net.basicmodel.R
import net.fragment.GenresDetailFragment.Companion.genresid
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.GenerAlbumLoader
import java.util.*

class GenresDetailAdapter(data: ArrayList<SongsModel>?, context: Context, activity: Activity) :
    RecyclerView.Adapter<GenresDetailAdapter.MyViewHolder>() {
    private val dataSet: ArrayList<SongsModel>?
    var context: Context
    var activity: Activity

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_song_name: TextView
        var txt_song_artist: TextView
        var img_song: ImageView
        var cardView: CardView
        var linear_more: LinearLayout
        var albumsRecyclerView: RecyclerView

        init {
            txt_song_name = itemView.findViewById<View>(R.id.txt_song_name) as TextView
            txt_song_artist = itemView.findViewById<View>(R.id.txt_songs_artist) as TextView
            img_song = itemView.findViewById<View>(R.id.img_song) as ImageView
            cardView = itemView.findViewById<View>(R.id.card_view) as CardView
            linear_more = itemView.findViewById<View>(R.id.linear_more) as LinearLayout
            albumsRecyclerView =
                itemView.findViewById<View>(R.id.recycler_view_album) as RecyclerView
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return if (viewType == 0) {
            val v0: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.artist_detail_albums_header, null)
            MyViewHolder(v0)
        } else {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_song_item, parent, false)
            MyViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, listPosition: Int) {
        if (getItemViewType(listPosition) == 0) {
            //nothing
            setUpAlbums(holder.albumsRecyclerView)
        } else {
            holder.txt_song_name.text = dataSet!![listPosition - 1].title
            holder.txt_song_artist.text = dataSet[listPosition - 1].artist
            Picasso.get().load(dataSet[listPosition - 1].img_uri)
                .placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon)
                .into(holder.img_song)
            holder.cardView.setOnClickListener {
                MusicPlayerControls.startSongsWithQueue(
                    context,
                    dataSet,
                    listPosition - 1,
                    "genresdetail"
                )
            }
            holder.img_song.setOnClickListener {
                MusicPlayerControls.startSongsWithQueue(
                    context,
                    dataSet,
                    listPosition - 1,
                    "genresdetail"
                )
            }
            holder.linear_more.setOnClickListener {
                GlobalApp.showPopUp(
                    holder.linear_more, context, activity,
                    dataSet[listPosition - 1]
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet!!.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        val viewType: Int
        viewType = if (position == 0) {
            0
        } else 1
        return viewType
    }

    private fun setUpAlbums(albumsRecyclerview: RecyclerView) {
        albumsRecyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        albumsRecyclerview.setHasFixedSize(true)
        albumsRecyclerview.isNestedScrollingEnabled = false
        val mAlbumAdapter =
            ArtistAlbumAdapter(activity, GenerAlbumLoader.getAlbumsForGener(context, genresid))
        albumsRecyclerview.adapter = mAlbumAdapter
    }

    init {
        dataSet = data
        this.context = context
        this.activity = activity
    }
}

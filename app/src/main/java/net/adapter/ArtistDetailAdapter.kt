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
import net.fragment.ArtistDetailFragment
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.ArtistAlbumLoader
import java.util.*

class ArtistDetailAdapter(
    private val dataSet: ArrayList<SongsModel>?,
    var context: Context,
    var activity: Activity
) :
    RecyclerView.Adapter<ArtistDetailAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_song_name: TextView?=null
        var txt_song_artist: TextView?=null
        var txt_artist: TextView?=null
        var img_song: ImageView?=null
        var cardView: CardView?=null
        var linear_more: LinearLayout?=null
        var albumsRecyclerView: RecyclerView?=null

        init {
            txt_song_name = itemView.findViewById<TextView>(R.id.txt_song_name)
            txt_artist = itemView.findViewById<TextView>(R.id.txt_artist)
            txt_song_artist = itemView.findViewById<TextView>(R.id.txt_songs_artist)
            img_song = itemView.findViewById<ImageView>(R.id.img_song)
            cardView = itemView.findViewById<CardView>(R.id.card_view)
            linear_more = itemView.findViewById<LinearLayout>(R.id.linear_more)
            albumsRecyclerView =
                itemView.findViewById<RecyclerView>(R.id.recycler_view_album)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
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
            setUpAlbums(holder.albumsRecyclerView!!)
        } else {
            val songsModel = dataSet!![listPosition - 1]
            holder.txt_artist!!.text = "Album: "
            holder.txt_song_name!!.text = songsModel.title
            holder.txt_song_artist!!.text = songsModel.album
            Picasso.get().load(songsModel.img_uri).placeholder(R.drawable.musicalicon)
                .error(R.drawable.musicalicon).into(holder.img_song)
            holder.cardView!!.setOnClickListener {
                if (dataSet != null) {
                    MusicPlayerControls.startSongsWithQueue(
                        context,
                        dataSet,
                        listPosition - 1,
                        "artistdetail"
                    )
                }
            }
            holder.img_song!!.setOnClickListener {
                MusicPlayerControls.startSongsWithQueue(
                    context,
                    dataSet,
                    listPosition - 1,
                    "artistdetail"
                )
            }
            holder.linear_more!!.setOnClickListener {
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
        val mAlbumAdapter = ArtistAlbumAdapter(
            activity, ArtistAlbumLoader.getAlbumsForArtist(
                context, ArtistDetailFragment.artistID
            )
        )
        albumsRecyclerview.adapter = mAlbumAdapter
    }
}
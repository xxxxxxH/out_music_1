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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import net.Controls.MusicPlayerControls
import net.basicmodel.R
import net.general.GlobalApp
import net.model.SongsModel
import net.utils.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(data: ArrayList<SongsModel>?, activity: Activity, context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyRecyclerHeadersAdapter<HistoryAdapter.Header_DataObjectHolder> {
    private val dataSet: ArrayList<SongsModel>?
    var context: Context
    var activity: Activity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_song_item, parent, false)
            return MyViewHolder(view)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, listPosition: Int) {
        if (holder is MyViewHolder) {
            try {
                holder.txt_song_name.text = dataSet!![listPosition].title
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                holder.txt_song_artist.text = dataSet!![listPosition].artist
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                Picasso.get().load(dataSet!![listPosition].img_uri)
                    .placeholder(R.drawable.musicalicon).error(R.drawable.musicalicon).into(
                        holder.img_song
                    )
            } catch (e: Exception) {
                Picasso.get().load(R.drawable.musicalicon).placeholder(R.drawable.musicalicon)
                    .error(R.drawable.musicalicon).into(
                        holder.img_song
                    )
                e.printStackTrace()
            }
            holder.cardView.setOnClickListener {
                if (dataSet != null) {
                    MusicPlayerControls.startSongsWithQueue(
                        context,
                        dataSet,
                        listPosition,
                        "history"
                    )
                }
                GlobalApp.ads_count =
                    GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
            }
            holder.linear_more.setOnClickListener {
                GlobalApp.showPopUp(
                    holder.linear_more, context, activity,
                    dataSet!![listPosition]
                )
            }
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): Header_DataObjectHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.history_header_date, parent, false)
        return Header_DataObjectHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: Header_DataObjectHolder, position: Int) {
        if (holder is Header_DataObjectHolder) {
            try {
                val date: String = dataSet!![position].history_date
                var string: String? = null
                try {
                    string = if (date is String) {
                        DateUtils.formatToYesterdayOrToday_detail(date)
                    } else {
                        ""
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                holder.txt_header.text = string
            } catch (e: Exception) {
                holder.txt_header.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }

    override fun getHeaderId(position: Int): Long {
        var value = "0"
        try {
            val date: String = dataSet!![position].history_date
            var str_date = "0"
            try {
                val sdf = SimpleDateFormat("yyyyMMdd")
                val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
                str_date = sdf.format(dateTime)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            try {
                value = str_date as? String ?: "0"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    class Header_DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txt_header: TextView

        init {
            txt_header = itemView.findViewById<View>(R.id.txt_header) as TextView
        }
    }

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
            linear_more = itemView.findViewById<View>(R.id.linear_more) as LinearLayout
            cardView = itemView.findViewById<View>(R.id.card_view) as CardView
        }
    }

    companion object {
        private const val TYPE_ITEM = 1
    }

    init {
        dataSet = data
        this.context = context
        this.activity = activity
    }
}

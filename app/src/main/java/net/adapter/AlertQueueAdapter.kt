package net.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import net.basicmodel.R
import net.model.AlertPlayList

class AlertQueueAdapter(val _context: Context, data: List<AlertPlayList>) :
    ArrayAdapter<AlertPlayList?>(_context, 0, data) {
    private val data: List<AlertPlayList>
    override fun getCount(): Int {
        return data.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView: View = inflater.inflate(R.layout.queue_alertlist_item, parent, false)
        val txt_song_name_alertqueue_item =
            rowView.findViewById<View>(R.id.txt_song_name_alertqueue_item) as TextView
        val img_song_alertqueue_item =
            rowView.findViewById<View>(R.id.img_song_alertqueue_item) as ImageView
        txt_song_name_alertqueue_item.text = data[position].title
        if (data[position].img_url != "") {
            Picasso.get().load(data[position].img_url).error(R.drawable.musicalicon)
                .placeholder(R.drawable.musicalicon).into(img_song_alertqueue_item)
        }
        return rowView
    }

    init {
        this.data = data
    }
}

package net.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.Listner.OnItemClickListener
import net.basicmodel.R
import net.model.FolderSongs

class FolderAudioAdapter : RecyclerView.Adapter<FolderAudioAdapter.MyViewHolder> {
    private var context: Context? = null
    var activity: Activity? = null

    class MyViewHolder(videoRow: View?) : RecyclerView.ViewHolder(
        videoRow!!
    ),
        View.OnClickListener {
        var rel_folder: RelativeLayout
        var txt_folder_name: TextView
        var txt_video_count: TextView
        override fun onClick(v: View) {
            if (onItemClickListener != null) {
                onItemClickListener!!.onClick(v, adapterPosition)
            }
        }

        init {
            txt_folder_name = videoRow!!.findViewById<View>(R.id.text_folder_name) as TextView
            txt_video_count = videoRow.findViewById<View>(R.id.text_video_count) as TextView
            rel_folder = itemView.findViewById<View>(R.id.relative_folder) as RelativeLayout
            rel_folder.setOnClickListener(this)
        }
    }

    constructor(
        context: Context?,
        act: Activity?,
        data: List<FolderSongs>?,
        listener: OnItemClickListener?
    ) {
        this.context = context
        _data = data
        activity = act
        onItemClickListener = listener
    }

    constructor()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View? = null
        val myViewHolder: MyViewHolder
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_folder_video, parent, false)
        myViewHolder = MyViewHolder(view)
        return myViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_folder_name.text = _data!![position].bucketName
        holder.txt_video_count.text = _data!![position].totalCount.toString() + " Songs"
    }

    override fun getItemCount(): Int {
        return if (_data == null) {
            0
        } else {
            _data!!.size
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return FOLDER_ITEM
    }

    companion object {
        var _data: List<FolderSongs>? = null
        const val FOLDER_ITEM = 1
        var onItemClickListener: OnItemClickListener? = null
    }
}
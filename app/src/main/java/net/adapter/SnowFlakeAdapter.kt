package net.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import net.DataBase.OpenHelper
import net.basicmodel.R
import net.event.MessageEvent
import net.utils.PreferencesUtility
import org.greenrobot.eventbus.EventBus
import java.util.*

class SnowFlakeAdapter(
    private val dataSet: ArrayList<Int>,
    var context: Context,
    activity: Activity
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var openHelper: OpenHelper
    var activity: Activity
    private val mPreferences: PreferencesUtility
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.themeitem, parent, false)
            return VHItem(view)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VHItem) {
            val dataItem = getItem(position)
            holder.img_background.setImageResource(dataItem)
            holder.img_background.setOnClickListener {
                mPreferences.showSnawFallImagePos = position
                EventBus.getDefault().post(MessageEvent("toggal_snow_fall_view",position))
//                MainActivity.toggal_snow_fall_view(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    private fun getItem(position: Int): Int {
        return dataSet[position]
    }

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_background: ImageView

        init {
            img_background = itemView.findViewById<View>(R.id.img_background) as ImageView
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        private const val TYPE_ITEM = 1
    }

    init {
        openHelper = OpenHelper(context)
        this.activity = activity
        mPreferences = PreferencesUtility.getInstance(activity)
    }
}


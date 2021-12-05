package net.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import net.DataBase.OpenHelper
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GlobalApp
import org.greenrobot.eventbus.EventBus
import java.util.*

class ThemeAdapter(private val dataSet: ArrayList<Int>, var context: Context, activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var openHelper: OpenHelper
    var activity: Activity
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
                try {
//                    MainActivity.img_main_background.setImageResource(
//                        GlobalApp.integerArrayList[position]
//                    )
                    EventBus.getDefault().post(
                        MessageEvent(
                            "img_main_background",
                            GlobalApp.integerArrayList[position]
                        )
                    )
//                    SongsMainFragment.img_player_background.setImageResource(
//                        GlobalApp.integerArrayList.get(
//                            position
//                        )
//                    )
                    EventBus.getDefault().post(
                        MessageEvent(
                            "img_player_background",
                            GlobalApp.integerArrayList[position]
                        )
                    )
                } catch (e: OutOfMemoryError) {
                    e.printStackTrace()
                }
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor.putString(
                    GlobalApp.PREFREANCE_MAIN_DEFAULT_BACKGROUND,
                    GlobalApp.integerArrayList.get(position).toString() + ""
                )
                editor.putString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, "")
                editor.putInt(GlobalApp.BLUR_SEEKBAR_POS, 0)
                editor.putInt(
                    GlobalApp.TRANCPARENT_COLOR,
                    GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE
                )
                editor.putInt(GlobalApp.TRANCPARENT_COLOR_SEEKBAR_POS, 0)
                EventBus.getDefault().post(MessageEvent("ThemeFragment"))
//                ThemeFragment.rel_seekbar.setVisibility(View.GONE)
//                ThemeFragment.seek_blur.setProgress(0)
//                ThemeFragment.seek_transparent.setProgress(0)
//                MainActivity.img_main_background_color.setBackgroundColor(GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE)
                EventBus.getDefault().post(
                    MessageEvent(
                        "img_main_background_color",
                        GlobalApp.integerArrayList[position]
                    )
                )
                editor.commit()
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
    }
}


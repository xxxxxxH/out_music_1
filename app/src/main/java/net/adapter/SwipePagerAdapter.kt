package net.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import net.basicmodel.R
import net.general.GlobalApp

class SwipePagerAdapter(var mContext: Context) : PagerAdapter() {
    var mInflater: LayoutInflater
    override fun getCount(): Int {
        return if (GlobalApp.mediaItemsArrayList == null || GlobalApp.mediaItemsArrayList.size <= 0) {
            0
        } else {
            GlobalApp.mediaItemsArrayList.size
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View = mInflater.inflate(R.layout.change_songe_fragment_layout, container, false)
        view.visibility = View.GONE
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {}

    init {
        mInflater = LayoutInflater.from(mContext)
    }
}
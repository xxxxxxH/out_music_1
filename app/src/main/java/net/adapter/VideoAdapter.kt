package net.adapter

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.InterstitialAd
//import com.google.android.gms.ads.LoadAdError
import com.squareup.picasso.Picasso
import net.Listner.OnItemClickListener
import net.basicmodel.R
import net.basicmodel.VideoPlayerActivity
import net.general.Ad_Id_File
import net.general.GlobalApp
import net.model.Videos
import java.util.*

class VideoAdapter(context: Context, data: ArrayList<Videos?>, listener: OnItemClickListener?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var context: Context
    var pos = 0
    var listener: OnItemClickListener?
//    var mInterstitialAd: InterstitialAd? = null

    inner class MyViewHolder(videoRow: View) : RecyclerView.ViewHolder(videoRow),
        View.OnClickListener {
        var videoThumb: ImageView
        var lin_more: LinearLayout
        var txt_title: TextView
        var txt_duration: TextView
        var txt_size: TextView
        var rel_video: RelativeLayout
        override fun onClick(v: View) {
            if (listener != null) {
                listener!!.onClick(v, adapterPosition)
            }
        }

        init {
            videoThumb = videoRow.findViewById<View>(R.id.img_video) as ImageView
            lin_more = videoRow.findViewById<View>(R.id.lin_menu) as LinearLayout
            txt_title = videoRow.findViewById<View>(R.id.txt_title) as TextView
            txt_duration = videoRow.findViewById<View>(R.id.txt_time) as TextView
            txt_size = videoRow.findViewById<View>(R.id.txt_size) as TextView
            rel_video = videoRow.findViewById<View>(R.id.rel_main) as RelativeLayout
            lin_more.setOnClickListener(this)
        }
    }

    internal inner class VHAdsItem(finalRow: View?) : RecyclerView.ViewHolder(
        finalRow!!
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null
        when (viewType) {
            TYPE_ADS -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
                return VHAdsItem(view)
            }
            TYPE_ITEM -> {
                view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
                return MyViewHolder(view)
            }
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ADS -> {
            }
            TYPE_ITEM -> if (dataSet[position] != null) {
                try {
                    Picasso.get().load(
                        ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            dataSet[position]!!._id.toLong()
                        )
                    ).resize(500, 500).centerCrop().into((holder as MyViewHolder).videoThumb)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                (holder as MyViewHolder).txt_title.text = dataSet[position]!!.title
                try {
                    holder.txt_duration.text = GlobalApp.convert(
                        dataSet[position]!!.duration.toLong()
                    )
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                try {
                    holder.txt_size.text = GlobalApp.filesize(
                        dataSet[position]!!.filePath
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                holder.rel_video.setOnClickListener { v ->
                    if (v != null) {
                        val imm =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                    pos = position
                    GlobalApp.ads_count =
                        GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                    val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                    editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                    editor1.commit()
                    if (GlobalApp.sharedpreferences!!.getInt(
                            GlobalApp.ADSCOUNT,
                            0
                        ) % GlobalApp.video_ads_count == 0
                    ) {
//                        if (mInterstitialAd!!.isLoaded) {
//                            mInterstitialAd!!.show()
//                        } else {
                            val intent = Intent(context, VideoPlayerActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.putExtra(
                                "videoFilePath",
                                dataSet[position]!!.filePath
                            )
                            intent.putExtra("pos", position)
                            intent.putExtra("title", dataSet[position]!!.title)
                            context.startActivity(intent)
//                        }
                    } else {
                        val intent = Intent(context, VideoPlayerActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.putExtra(
                            "videoFilePath",
                            dataSet[position]!!.filePath
                        )
                        intent.putExtra("pos", position)
                        intent.putExtra("title", dataSet[position]!!.title)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

//    fun showIntrestialAds(act: Context?) {
//        val adRequest: AdRequest
//        adRequest = AdRequest.Builder().build()
//        mInterstitialAd = InterstitialAd(act)
//        if (Ad_Id_File.isActive_adMob) {
//            // set the ad unit ID
//            mInterstitialAd!!.adUnitId = Ad_Id_File.ADMOB_INTERSTITIAL_PUB_ID
//            // Load ads into Interstitial Ads
//            mInterstitialAd!!.loadAd(adRequest)
//            mInterstitialAd!!.adListener = object : AdListener() {
//                override fun onAdLoaded() {}
//                override fun onAdClosed() {
//                    super.onAdClosed()
//                    mInterstitialAd!!.loadAd(adRequest)
//                    val intent = Intent(context, VideoPlayerActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    intent.putExtra("videoFilePath", dataSet[pos]!!.filePath)
//                    intent.putExtra("pos", pos)
//                    intent.putExtra("title", dataSet[pos]!!.title)
//                    context.startActivity(intent)
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                }
//            }
//        }
//    }

    companion object {
        lateinit var dataSet: ArrayList<Videos?>
        private const val TYPE_ITEM = 1
        const val TYPE_ADS = 0
    }

    init {
        dataSet = data
        this.context = context
        this.listener = listener
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(context)
        }
//        showIntrestialAds(context)
    }

    fun getData():ArrayList<Videos?>{
        return dataSet
    }
}

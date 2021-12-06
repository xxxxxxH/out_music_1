package net.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.InterstitialAd
import com.squareup.picasso.Picasso
import net.basicmodel.R
import net.fragment.AlbumDetailFragment
import net.general.Ad_Id_File
import net.general.GlobalApp
import net.model.AlbumModel
import net.utils.PreferencesUtility

class AlbumAdapter(data: List<AlbumModel>, context: Context, activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val TYPE_ITEM = 1
    private val dataSet: List<AlbumModel>
    var context: Context
    var activity: Activity
    var temp_pos = 0
    var isGrid: Boolean
//    var mInterstitialAd: InterstitialAd? = null
    var temp_holder: VHItem? = null
    private fun getItem(position: Int): AlbumModel {
        return dataSet[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VHItem) {
            val dataItem: AlbumModel = getItem(position)
            holder.artist_name.setText(dataItem.title)
            holder.album_song_count.setText(dataItem.artistName)
            Picasso.get().load(dataItem.img_uri).placeholder(R.drawable.musicalbumicon)
                .error(R.drawable.musicalbumicon).into(
                    holder.artistImage
                )
            holder.content.setOnClickListener {
                GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
                if (GlobalApp.sharedpreferences == null) {
                    GlobalApp.savePrefrence(context)
                }
                val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
                editor1.commit()
                temp_pos = position
                if (GlobalApp.sharedpreferences!!.getInt(
                        GlobalApp.ADSCOUNT,
                        0
                    ) >= GlobalApp.ads_total_count
                ) {
                    temp_holder = holder
//                    if (mInterstitialAd!!.isLoaded) {
//                        mInterstitialAd!!.show()
//                    } else {
                        GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                            AlbumDetailFragment( dataItem.id,
                                dataItem.title), "AlbumDetailFragment", activity
                        )
//                    }
                } else {
                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                        AlbumDetailFragment( dataItem.id,
                            dataItem.title), "AlbumDetailFragment", activity
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            return if (isGrid) {
                val view1: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_artist_item_grid, parent, false)
                VHItem(view1)
            } else {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_artist_item_list, parent, false)
                VHItem(view)
            }
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var artist_name: TextView
        var album_song_count: TextView
        var artistImage: ImageView
        var content: RelativeLayout

        init {
            artist_name = itemView.findViewById<View>(R.id.artist_name) as TextView
            album_song_count = itemView.findViewById<View>(R.id.album_song_count) as TextView
            artistImage = itemView.findViewById<View>(R.id.artistImage) as ImageView
            content = itemView.findViewById<View>(R.id.content) as RelativeLayout
        }
    }

//    fun showIntrestialAds(act: Activity?) {
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
//                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
//                        AlbumDetailFragment().newInstance(
//                            dataSet[temp_pos].id, dataSet[temp_pos].title
//                        ), "AlbumDetailFragment", activity
//                    )
//                }
//            }
//        }
//    }

    init {
        dataSet = data
        this.context = context
        this.activity = activity
        isGrid = PreferencesUtility.getInstance(context).isAlbumsInGrid
//        showIntrestialAds(activity)
    }
}
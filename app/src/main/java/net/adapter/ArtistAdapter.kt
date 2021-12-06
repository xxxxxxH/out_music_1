package net.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.provider.MediaStore
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
import net.fragment.ArtistDetailFragment
import net.general.Ad_Id_File
import net.general.GeneralFunction
import net.general.GlobalApp
import net.model.ArtistModel
import net.utils.PreferencesUtility

class ArtistAdapter(data: List<ArtistModel>, context: Context, act: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val dataSet: List<ArtistModel>
    var context: Context
    var activity: Activity
//    var mInterstitialAd: InterstitialAd? = null
    var temp_pos = 0
    var temp_holder: MyViewHolder? = null
    var _albtmID: Long = 0
    var isGrid: Boolean

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            return if (isGrid) {
                val view1: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_artist_item_grid, parent, false)
                MyViewHolder(view1)
            } else {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_artist_item_list, parent, false)
                MyViewHolder(view)
            }
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].name.equals("ads")) {
            TYPE_ADS
        } else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return dataSet.size
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
//                    //  mInterstitialAd.loadAd(adRequest);
//                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
//                        ArtistDetailFragment().newInstance(
//                            dataSet[temp_pos].id, dataSet[temp_pos].name
//                        ), "ArtistDetailFragment", activity
//                    )
//                }
//            }
//        }
//    }

    companion object {
        private const val TYPE_ITEM = 1
        private const val TYPE_ADS = 0
    }

    init {
        dataSet = data
        this.context = context
        activity = act
        isGrid = PreferencesUtility.getInstance(context).isArtistsInGrid
//        showIntrestialAds(act)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.artist_name.text = dataSet[position].name
            val albumNmber: String = GeneralFunction.makeLabel(
                context,
                R.plurals.Nalbums,
                dataSet[position].albumCount
            )
            val songCount: String = GeneralFunction.makeLabel(
                context,
                R.plurals.Nsongs,
                dataSet[position].songCount
            )
            holder.album_song_count.text = GeneralFunction.makeCombinedString(
                context,
                albumNmber,
                songCount
            )
            val where = MediaStore.Audio.Media.ARTIST_ID + "=?"
            val whereVal = arrayOf<String>(dataSet[position].id.toString() + "")
            val orderBy1 = MediaStore.Audio.Media.TITLE
            val c1 = context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf("album_id"),
                where,
                whereVal,
                orderBy1
            )
            if (c1 != null) {
                if (c1.moveToFirst()) {
                    _albtmID = c1.getLong(c1.getColumnIndex("album_id"))
                }
            }
            Picasso.get().load(GlobalApp.getImgUri(_albtmID))
                .placeholder(R.drawable.musicartisticon).error(R.drawable.musicartisticon).into(
                    holder.artistImage
                )
            holder.content.setOnClickListener {
                GlobalApp.ads_count =
                    GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
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
                            ArtistDetailFragment(  dataSet[position].id, dataSet[position].name), "ArtistDetailFragment", activity
                        )
//                    }
                } else {
                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                        ArtistDetailFragment(dataSet[position].id, dataSet[position].name), "ArtistDetailFragment", activity
                    )
                }
            }
        }
    }
}
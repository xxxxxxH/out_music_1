package net.adapter

import android.app.Activity
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
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

class ArtistAlbumAdapter(context: Activity, arraylist: List<AlbumModel>?) :
    RecyclerView.Adapter<ArtistAlbumAdapter.ItemHolder>() {
    private val arraylist: List<AlbumModel>?
    private val mContext: Activity
    var temp_adpter_pos = 0
    var temp_album_art: ImageView? = null
//    var mInterstitialAd: InterstitialAd? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        val v: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_item_artist_album, null)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(itemHolder: ItemHolder, i: Int) {
        val localItem: AlbumModel = arraylist!![i]
        itemHolder.title.text = localItem.title
        val songCount: String =
            localItem.songCount.toString() + " Songs" //Fantastic_MyMusicUtils.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount);
        itemHolder.details.text = songCount
        try {
            Picasso.get().load(localItem.img_uri).placeholder(R.drawable.musicalbumicon)
                .error(R.drawable.musicalbumicon).into(itemHolder.albumArt)
        } catch (e: Exception) {
            Picasso.get().load(R.drawable.musicalbumicon).placeholder(R.drawable.musicalbumicon)
                .error(R.drawable.musicalbumicon).into(itemHolder.albumArt)
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return arraylist?.size ?: 0
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        var title: TextView
        var details: TextView
        var albumArt: ImageView
        protected var rootView: CardView
        override fun onClick(v: View) {
            temp_adpter_pos = adapterPosition
            temp_album_art = albumArt
            GlobalApp.ads_count = GlobalApp.sharedpreferences!!.getInt(GlobalApp.ADSCOUNT, 0) + 1
            val editor1: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
            editor1.putInt(GlobalApp.ADSCOUNT, GlobalApp.ads_count)
            editor1.commit()
            if (GlobalApp.ads_count >= GlobalApp.ads_total_count) {
//                if (mInterstitialAd!!.isLoaded) {
//                    mInterstitialAd!!.show()
//                } else {
                    GlobalApp.sharedInstance(mContext)!!.fragmentReplaceTransition(
                        AlbumDetailFragment(arraylist!![adapterPosition].id, arraylist[adapterPosition].title), "AlbumDetailFragment", mContext
                    )
//                }
            } else {
                GlobalApp.sharedInstance(mContext)!!.fragmentReplaceTransition(
                    AlbumDetailFragment(arraylist!![adapterPosition].id, arraylist[adapterPosition].title), "AlbumDetailFragment", mContext
                )
            }
        }

        init {
            rootView = view.findViewById<View>(R.id.root_view) as CardView
            title = view.findViewById<View>(R.id.album_title) as TextView
            details = view.findViewById<View>(R.id.album_details) as TextView
            albumArt = view.findViewById<View>(R.id.album_art) as ImageView
            view.setOnClickListener(this)
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
//                    //   mInterstitialAd.loadAd(adRequest);
//                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                    GlobalApp.sharedInstance(mContext)!!.fragmentReplaceTransition(
//                        AlbumDetailFragment().newInstance(
//                            arraylist!![temp_adpter_pos].id, arraylist[temp_adpter_pos].title
//                        ), "AlbumDetailFragment", mContext
//                    )
//                }
//            }
//        }
//    }

    init {
        this.arraylist = arraylist
        mContext = context
//        showIntrestialAds(mContext)
    }
}
package net.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.ads.AdListener
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.InterstitialAd
//import com.google.android.gms.ads.LoadAdError
import com.squareup.picasso.Picasso
import net.DataBase.OpenHelper
import net.basicmodel.R
import net.fragment.PlaylistDetaillFragment
import net.general.Ad_Id_File
import net.general.GeneralFunction
import net.general.GlobalApp
import net.model.AlertPlayList
import java.util.*

class PlaylistlAdapter(data: ArrayList<AlertPlayList>, context: Context, activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val dataSet: ArrayList<AlertPlayList>
    var context: Context
    var activity: Activity
//    var mInterstitialAd: InterstitialAd? = null
    var temp_pos = 0
    var temp_holder: RecyclerView.ViewHolder? = null
    var openHelper: OpenHelper
    var builder: AlertDialog.Builder
    private fun getItem(position: Int): AlertPlayList {
        return dataSet[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VHItem) {
            val dataItem: AlertPlayList = getItem(position)
            holder.artist_name.text = dataItem.title
            val songCount: String = GeneralFunction.makeLabel(
                context,
                R.plurals.Nsongs,
                dataSet[position].songCount
            )
            holder.album_song_count.text = songCount
            Picasso.get().load(dataItem.img_url).placeholder(R.drawable.musicplaylisticon)
                .error(R.drawable.musicplaylisticon).into(
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
                            PlaylistDetaillFragment().newInstance(
                                dataSet[position].id,
                                dataSet[position].img_url,
                                dataSet[position].title
                            ), "MyPlaylistDetaillFragment", activity
                        )
//                    }
                } else {
                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
                        PlaylistDetaillFragment().newInstance(
                            dataSet[position].id,
                            dataSet[position].img_url,
                            dataSet[position].title
                        ), "MyPlaylistDetaillFragment", activity
                    )
                }
            }
            holder.content.setOnLongClickListener {
                delete_aletdialog(dataItem.id, dataItem.title)
                notifyDataSetChanged()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_artist_item_grid, parent, false)
            return VHItem(view)
        }
        throw RuntimeException("there is no type that matches the type $viewType + make sure your using types correctly")
    }

    override fun getItemViewType(position: Int): Int {
        return TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    internal inner class VHItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
//        adRequest = AdRequest.Builder().addTestDevice("EDE3C4457F38ACA3C9790EF5A3445F99").build()
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
//                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
//                    editor.putInt(GlobalApp.ADSCOUNT, 0)
//                    editor.commit()
//                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransition(
//                        PlaylistDetaillFragment().newInstance(
//                            dataSet[temp_pos].id,
//                            dataSet[temp_pos].img_url,
//                            dataSet[temp_pos].title
//                        ), "MyPlaylistDetaillFragment", activity
//                    )
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                }
//            }
//        }
//    }

    fun delete_aletdialog(playlistid: String?, playlist_name: String) {
        builder.setTitle("Delete")

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to delete $playlist_name playlist ?")
            .setCancelable(false)
            .setPositiveButton(
                Html.fromHtml("<font color='#000000'>Yes</font>")
            ) { dialog, id -> openHelper.deletePlayList(playlistid) }
            .setNegativeButton(
                Html.fromHtml("<font color='#000000'>No</font>")
            ) { dialog, id -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Delete")
        alert.show()
    }

    companion object {
        private const val TYPE_ITEM = 1
    }

    init {
        dataSet = data
        this.context = context
        this.activity = activity
        openHelper = OpenHelper(context)
        builder = AlertDialog.Builder(context)
//        showIntrestialAds(activity)
    }
}

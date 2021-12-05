package net.fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.theme_fragment.*
import net.adapter.SnowFlakeAdapter
import net.adapter.ThemeAdapter
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GeneralFunction
import net.general.GlobalApp
import net.utils.PreferencesUtility
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ThemeFragment : Fragment() {
    var backgroundAdapter: ThemeAdapter? = null
    var snowFlakeAdapter: SnowFlakeAdapter? = null
    private var editor: SharedPreferences.Editor? = null
    var backgroundOpacity = 0
    var temp_transpant_progress = 0
    var blur_seekbar_pos = 0
    var img_path: String? = null
    var mPreferences: PreferencesUtility? = null
    var is_show_snowfall = false

    // String main_image_background = "";
    var opacity_value = 0
    var is_show_snowfallview = false
    private var sp: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        opacity_value = sp!!.getInt(
            GlobalApp.TRANCPARENT_COLOR,
            GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE
        ) // default transparancy
        mPreferences = PreferencesUtility.getInstance(activity)
        is_show_snowfallview = mPreferences!!.isShowSnawFall
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.theme_fragment, container, false)
        EventBus.getDefault().register(this)
        setHasOptionsMenu(true)
        mPreferences = PreferencesUtility.getInstance(context)
        img_path =
            GlobalApp.sharedpreferences!!.getString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, "")
        temp_transpant_progress =
            GlobalApp.sharedpreferences!!.getInt(GlobalApp.TRANCPARENT_COLOR_SEEKBAR_POS, 0)
        blur_seekbar_pos = GlobalApp.sharedpreferences!!.getInt(GlobalApp.BLUR_SEEKBAR_POS, 0)
        if (GlobalApp.integerArrayList_small.size == 0) {
//            SplashActivity.smallBackgroundImage()
        }
        if (GlobalApp.integerArrayList.size == 0) {
//            SplashActivity.mainBackgroundImage()
        }
        if (GlobalApp.flakeArrayList.size == 0) {
//            SplashActivity.flakeImage()
        }
        Initialize()
        editor = GlobalApp.sharedpreferences!!.edit()
        seek_transparent!!.progress = temp_transpant_progress
        seek_blur!!.progress = blur_seekbar_pos

        //main_image_background = GlobalApp.sharedpreferences.getString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, "");
        if (img_path == "") {
            rel_seekbar!!.visibility = View.GONE
        }
        showIntrestialAds(activity)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        bmpPic = BitmapFactory.decodeFile(img_path)
        seek_transparent!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                temp_transpant_progress = progress
                backgroundOpacity = progress * 0x03000000 + 0x000000
//                MainActivity.img_main_background_color.setBackgroundColor(backgroundOpacity)
                EventBus.getDefault().post(
                    MessageEvent(
                        "img_main_background_color",
                        backgroundOpacity
                    )
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        seek_blur!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val radius = seek_blur!!.progress
                if (radius <= 0) {
//                    MainActivity.img_main_background.setImageBitmap(bmpPic)
                    EventBus.getDefault().post(
                        MessageEvent(
                            "setImageBitmap",
                            bmpPic
                        )
                    )
                } else {
//                    MainActivity.img_main_background.setImageBitmap(
//                        GeneralFunction.createBlurBitmap(
//                            bmpPic, radius.toFloat(), activity
//                        )
//                    )
                    EventBus.getDefault().post(
                        MessageEvent(
                            "setImageBitmap",
                            GeneralFunction.createBlurBitmap(
                                bmpPic, radius.toFloat(), activity
                            )
                        )
                    )
                }
                editor!!.putInt(GlobalApp.BLUR_SEEKBAR_POS, radius)
                editor!!.commit()
            }
        })
        img_gallary!!.setOnClickListener {
            GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransitionSetting(
                CropImageFragment().newInstance(""),
                "CropImageFragment",
                requireActivity()
            )
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val c =
                requireContext().contentResolver.query(selectedImage!!, filePath, null, null, null)
            if (c != null) {
                c.moveToFirst()
                val columnIndex = c.getColumnIndex(filePath[0])
                val picturePath = c.getString(columnIndex)
                c.close()
//                if (mInterstitialAd!!.isLoaded) {
//                    mInterstitialAd!!.show()
//                } else {
//
//                }
                GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransitionSetting(
                    CropImageFragment().newInstance(picturePath),
                    "CropImageFragment",
                    requireActivity()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        img_path =
            GlobalApp.sharedpreferences!!.getString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, "")
    }

    override fun onPause() {
        super.onPause()
        editor!!.putInt(GlobalApp.TRANCPARENT_COLOR, backgroundOpacity)
        editor!!.putInt(GlobalApp.TRANCPARENT_COLOR_SEEKBAR_POS, temp_transpant_progress)
        editor!!.commit()
    }

    private fun Initialize() {
        rc_background!!.setHasFixedSize(true)
        rc_background!!.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rc_flake!!.setHasFixedSize(true)
        rc_flake!!.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        seek_blur!!.max = 25
        seek_transparent!!.max = 80
        Handler().postDelayed({
            snowFlakeAdapter =
                SnowFlakeAdapter(GlobalApp.flakeArrayList, requireContext(), requireActivity())
            rc_flake!!.adapter = snowFlakeAdapter
            backgroundAdapter =
                ThemeAdapter(GlobalApp.integerArrayList_small, requireContext(), requireActivity())
            rc_background!!.adapter = backgroundAdapter
        }, 50)
        rel_check!!.setOnClickListener {
            if (is_show_snowfall) {
                checkbox_snow!!.isChecked = false
                is_show_snowfall = false
//                MainActivity.toggal_snow_fall_view(-1)
                EventBus.getDefault().post(MessageEvent("toggal_snow_fall_view", -1))
                mPreferences!!.isShowSnawFall = false
                txt_snow_fall_effect!!.visibility = View.VISIBLE
                rc_flake!!.visibility = View.GONE
            } else {
                checkbox_snow!!.isChecked = true
                is_show_snowfall = true
//                MainActivity.toggal_snow_fall_view(mPreferences!!.showSnawFallImagePos)
                EventBus.getDefault().post(
                    MessageEvent(
                        "toggal_snow_fall_view",
                        mPreferences!!.showSnawFallImagePos
                    )
                )
                mPreferences!!.isShowSnawFall = true
                txt_snow_fall_effect!!.visibility = View.GONE
                rc_flake!!.visibility = View.VISIBLE
            }
        }
        is_show_snowfall = mPreferences!!.isShowSnawFall
        if (is_show_snowfall) {
            checkbox_snow!!.isChecked = true
            txt_snow_fall_effect!!.visibility = View.GONE
            rc_flake!!.visibility = View.VISIBLE
//            MainActivity.toggal_snow_fall_view(mPreferences.getShowSnawFallImagePos())
            EventBus.getDefault()
                .post(MessageEvent("toggal_snow_fall_view", mPreferences!!.showSnawFallImagePos))
        } else {
            checkbox_snow!!.isChecked = false
            txt_snow_fall_effect!!.visibility = View.VISIBLE
            rc_flake!!.visibility = View.GONE
//            MainActivity.toggal_snow_fall_view(-1)
            EventBus.getDefault().post(MessageEvent("toggal_snow_fall_view", -1))
        }
    }

    fun showIntrestialAds(act: Activity?) {
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
//                    val i = Intent(
//                        Intent.ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    )
//                    startActivityForResult(i, RESULT_LOAD_IMAGE)
//                }
//
//                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    super.onAdFailedToLoad(loadAdError)
//                }
//            }
//        }
    }

    companion object {
        var rel_seekbar: RelativeLayout? = null
        private const val RESULT_LOAD_IMAGE = 1
        var seek_transparent: SeekBar? = null
        var seek_blur: SeekBar? = null
        var bmpPic: Bitmap? = null
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val msg = event.getMessage()
        when (msg[0]) {
            "ThemeFragment" -> {
                rel_seekbar.visibility = View.GONE
                seek_blur.progress = 0
                seek_transparent.progress = 0
            }
        }
    }
}

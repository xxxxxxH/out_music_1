package net.basicmodel

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener
//import com.google.android.gms.ads.AdView
//import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_common_main.*
import kotlinx.android.synthetic.main.include_flake_layout.*
import net.event.MessageEvent
import net.fragment.SettingMainFragment
import net.fragment.SongsMainFragment
import net.fragment.VideoMainFragment
import net.general.GeneralFunction
import net.general.GlobalApp
import net.utils.PreferencesUtility
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    var drawer: DrawerLayout? = null
    var activity: Activity? = null
    var is_show_snowfallview = false
    var mPreferences: PreferencesUtility? = null
    var main_default_background = 0
    var main_image_background = ""
    var layout: RelativeLayout? = null
//    var mInterstitialAd: InterstitialAd? = null
//    var exit_dialog_adview: AdView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_main)
        EventBus.getDefault().register(this)
        activity = this
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(this)
        }

        mPreferences = PreferencesUtility.getInstance(activity)
        is_show_snowfallview = mPreferences!!.isShowSnawFall

        init()
        GlobalApp.fragmentReplaceTransitionCommon(
            SongsMainFragment(), "Songs",
            activity as MainActivity
        )

    }

    private fun init() {
        if (is_show_snowfallview) {
            toggal_snow_fall_view(mPreferences!!.showSnawFallImagePos)
        } else {
            toggal_snow_fall_view(-1)
        }
        val opacity_value = GlobalApp.sharedpreferences!!.getInt(
            GlobalApp.TRANCPARENT_COLOR,
            GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE
        ) // default transparancy
        try {
            img_main_background_color.setBackgroundColor(opacity_value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        bottom_navigation_view_linear.setNavigationChangeListener(BubbleNavigationChangeListener { view, position ->
            if (position == 0) {
                supportFragmentManager.popBackStack(
                    "Songs",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                GlobalApp.fragmentReplaceTransitionCommon(
                    SongsMainFragment(), "Songs",
                    activity!!
                )
            } else if (position == 1) {
                supportFragmentManager.popBackStack(
                    "Video",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                GlobalApp.fragmentReplaceTransitionCommon(
                    VideoMainFragment(), "Video",
                    activity!!
                )
            } else if (position == 2) {
//                if (mInterstitialAd!!.isLoaded) {
//                    mInterstitialAd!!.show()
//                } else {
                    supportFragmentManager.popBackStack(
                        "Settings",
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    GlobalApp.fragmentReplaceTransitionCommon(
                        SettingMainFragment(), "Settings",
                        activity!!
                    )
//                }
            }
        })
    }

    /*@Override
    public void onBackPressed() {
        AskForExit();
    }*/
    fun toggal_snow_fall_view(snow_effect_pos: Int) {
        if (snow_effect_pos == 0) {
            snowfallview.visibility = View.VISIBLE
            snowfallview1.visibility = View.GONE
            snowfallview2.visibility = View.GONE
            snowfallview3.visibility = View.GONE
            snowfallview4.visibility = View.GONE
            snowfallview5.visibility = View.GONE
        } else if (snow_effect_pos == 1) {
            snowfallview.visibility = View.GONE
            snowfallview1.visibility = View.VISIBLE
            snowfallview2.visibility = View.GONE
            snowfallview3.visibility = View.GONE
            snowfallview4.visibility = View.GONE
            snowfallview5.visibility = View.GONE
        } else if (snow_effect_pos == 2) {
            snowfallview.visibility = View.GONE
            snowfallview1.visibility = View.GONE
            snowfallview2.visibility = View.VISIBLE
            snowfallview3.visibility = View.GONE
            snowfallview4.visibility = View.GONE
            snowfallview5.visibility = View.GONE
        } else if (snow_effect_pos == 3) {
            snowfallview.visibility = View.GONE
            snowfallview1.visibility = View.GONE
            snowfallview2.visibility = View.GONE
            snowfallview3.visibility = View.VISIBLE
            snowfallview4.visibility = View.GONE
            snowfallview5.visibility = View.GONE
        } else if (snow_effect_pos == 4) {
            snowfallview.visibility = View.GONE
            snowfallview1.visibility = View.GONE
            snowfallview2.visibility = View.GONE
            snowfallview3.visibility = View.GONE
            snowfallview4.visibility = View.VISIBLE
            snowfallview5.visibility = View.GONE
        } else if (snow_effect_pos == 5) {
            snowfallview.visibility = View.GONE
            snowfallview1.visibility = View.GONE
            snowfallview2.visibility = View.GONE
            snowfallview3.visibility = View.GONE
            snowfallview4.visibility = View.GONE
            snowfallview5.visibility = View.VISIBLE
        } else {
            snowfallview.visibility = View.GONE
            snowfallview1.visibility = View.GONE
            snowfallview2.visibility = View.GONE
            snowfallview3.visibility = View.GONE
            snowfallview4.visibility = View.GONE
            snowfallview5.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        main_image_background =
            GlobalApp.sharedpreferences!!.getString(
                GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND,
                ""
            )!!
        main_default_background = GlobalApp.sharedpreferences!!.getString(
            GlobalApp.PREFREANCE_MAIN_DEFAULT_BACKGROUND,
            GlobalApp.integerArrayList[0].toString() + ""
        )!!
            .toInt()

        if (main_image_background != "") {
            val bmpPic = BitmapFactory.decodeFile(main_image_background)
            val radius = GlobalApp.sharedpreferences!!.getInt(GlobalApp.BLUR_SEEKBAR_POS, 0)
            if (radius == 0) {
                img_main_background.setImageBitmap(bmpPic)
            } else {
                img_main_background.setImageBitmap(
                    GeneralFunction.fastblur(
                        bmpPic,
                        1.0f,
                        radius
                    )
                )
            }
        } else {
            Glide.with(activity!!).load(main_default_background)
                .placeholder(R.drawable.gradient1).into(img_main_background)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MessageEvent) {
        val msg = event.getMessage()
        when (msg[0]) {
            "toggal_snow_fall_view" -> {
                toggal_snow_fall_view(msg[1] as Int)
            }
            "img_main_background" -> {
                img_main_background.setImageResource(
                    msg[1] as Int
                )
            }
            "img_main_background_color" -> {
                img_main_background_color.setBackgroundColor(GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE)
            }
            "setImageBitmap" -> {
                img_main_background.setImageBitmap(msg[1] as Bitmap?)
            }
            "setImageURI" -> {
                img_main_background.setImageURI(msg[1] as Uri?)
            }

        }
    }
}
package net.fragment

//import com.google.android.gms.ads.AdRequest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import net.adapter.SnowFlakeAdapter
import net.adapter.ThemeAdapter
import net.basicmodel.R
import net.general.GlobalApp
import net.utils.PreferencesUtility

class ThemeFragment : Fragment() {
    var rc_background: RecyclerView? = null
    var rc_flake: RecyclerView? = null
    var backgroundAdapter: ThemeAdapter? = null
    var snowFlakeAdapter: SnowFlakeAdapter? = null
    var toolbar: Toolbar? = null
    private var editor: SharedPreferences.Editor? = null
//    var appBarLayout: AppBarLayout? = null
    var backgroundOpacity = 0
    var temp_transpant_progress = 0
    var blur_seekbar_pos = 0
    var img_path: String? = null
    var img_gallary: ImageView? = null
    var txt_snow_fall_effect: TextView? = null
    var checkbox_snow: AppCompatCheckBox? = null
    var rel_snow_check: RelativeLayout? = null
    var rel_seekbar: RelativeLayout? = null
    var mPreferences: PreferencesUtility? = null
    var is_show_snowfall = false
    var seek_transparent:SeekBar?=null
    var seek_blur:SeekBar?=null
    var opacity_value = 0
    private val RESULT_LOAD_IMAGE = 1

    // String main_image_background = "";

    // String main_image_background = "";
    private var sp: SharedPreferences? = null
    var bmpPic: Bitmap? = null
    var is_show_snowfallview = false
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
    ): View {
       val view = inflater.inflate(R.layout.theme_fragment, container, false)
        setHasOptionsMenu(true)
        mPreferences = PreferencesUtility.getInstance(context)
        img_path =
            GlobalApp.sharedpreferences!!.getString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, "")
        temp_transpant_progress =
            GlobalApp.sharedpreferences!!.getInt(GlobalApp.TRANCPARENT_COLOR_SEEKBAR_POS, 0)
        blur_seekbar_pos = GlobalApp.sharedpreferences!!.getInt(GlobalApp.BLUR_SEEKBAR_POS, 0)
//        if (GlobalApp.integerArrayList_small.size === 0) {
//            SplashActivity.smallBackgroundImage()
//        }
//        if (GlobalApp.integerArrayList.size() === 0) {
//            SplashActivity.mainBackgroundImage()
//        }
//        if (GlobalApp.flakeArrayList.size() === 0) {
//            SplashActivity.flakeImage()
//        }
        Initialize(view)
        editor = GlobalApp.sharedpreferences!!.edit()
        seek_transparent!!.progress = temp_transpant_progress
        seek_blur!!.progress = blur_seekbar_pos

        //main_image_background = GlobalApp.sharedpreferences.getString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, "");
        if (img_path == "") {
            rel_seekbar!!.visibility = View.GONE
        }
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        toolbar!!.title = "Select Theme"
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar!!.setNavigationOnClickListener {
            editor!!.putInt(GlobalApp.TRANCPARENT_COLOR, backgroundOpacity)
            editor!!.putInt(GlobalApp.TRANCPARENT_COLOR_SEEKBAR_POS, temp_transpant_progress)
            editor!!.commit()
            (activity as AppCompatActivity?)!!.supportFragmentManager.popBackStack()
        }
        bmpPic = BitmapFactory.decodeFile(img_path)
        seek_transparent!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                temp_transpant_progress = progress
                backgroundOpacity = progress * 0x03000000 + 0x000000
               // MainActivity.img_main_background_color.setBackgroundColor(backgroundOpacity)
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
                   // MainActivity.img_main_background.setImageBitmap(bmpPic)
                } else {
                   // MainActivity.img_main_background.setImageBitmap(
//                        GeneralFunction.createBlurBitmap(
//                            bmpPic,
//                            radius.toFloat(), activity
//                        )
//                    )
                }
                editor!!.putInt(GlobalApp.BLUR_SEEKBAR_POS, radius)
                editor!!.commit()
            }
        })
        img_gallary!!.setOnClickListener {
            GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransitionSetting(
                CropImageFragment().newInstance(""), "CropImageFragment",
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
            val c = requireContext().contentResolver.query(selectedImage!!, filePath, null, null, null)
            if (c != null) {
                c.moveToFirst()
                val columnIndex = c.getColumnIndex(filePath[0])
                val picturePath = c.getString(columnIndex)
                c.close()
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show()
//                } else {
                    GlobalApp.sharedInstance(activity)!!.fragmentReplaceTransitionSetting(
                        CropImageFragment().newInstance(picturePath), "CropImageFragment",
                        requireActivity()
                    )
//                }
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

    private fun Initialize(view :View) {
        txt_snow_fall_effect = view.findViewById<View>(R.id.txt_snow_fall_effect) as TextView
        checkbox_snow = view.findViewById<View>(R.id.checkbox_snow) as AppCompatCheckBox
        rel_snow_check = view.findViewById<View>(R.id.rel_check) as RelativeLayout
        rel_seekbar = view.findViewById<View>(R.id.rel_seekbar) as RelativeLayout
        rel_seekbar = view.findViewById<View>(R.id.rel_seekbar) as RelativeLayout
        rc_flake = view.findViewById<View>(R.id.rc_flake) as RecyclerView
        rc_background = view.findViewById<View>(R.id.rc_background) as RecyclerView
        rc_background!!.setHasFixedSize(true)
        rc_background!!.layoutManager =
            LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        rc_flake!!.setHasFixedSize(true)
        rc_flake!!.layoutManager =
            LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        img_gallary = view.findViewById<View>(R.id.img_gallary) as ImageView
        //appBarLayout = view.findViewById<View>(R.id.appbar) as AppBarLayout
        seek_blur = view.findViewById<View>(R.id.seek_blur) as SeekBar
        seek_blur!!.max = 25
        seek_transparent = view.findViewById<View>(R.id.seek_transparent) as SeekBar
        seek_transparent!!.max = 80
        Handler().postDelayed({
            snowFlakeAdapter = SnowFlakeAdapter(
                GlobalApp.flakeArrayList,requireContext(),
                requireActivity()
            )
            rc_flake!!.adapter = snowFlakeAdapter
            backgroundAdapter = ThemeAdapter(
                GlobalApp.integerArrayList_small,
                requireContext(),
                requireActivity()
            )
            rc_background!!.adapter = backgroundAdapter
        }, 50)
        rel_snow_check!!.setOnClickListener {
            if (is_show_snowfall) {
                checkbox_snow!!.isChecked = false
                is_show_snowfall = false
                //MainActivity.toggal_snow_fall_view(-1)
                mPreferences!!.isShowSnawFall = false
                txt_snow_fall_effect!!.visibility = View.VISIBLE
                rc_flake!!.visibility = View.GONE
            } else {
                checkbox_snow!!.isChecked = true
                is_show_snowfall = true
                //MainActivity.toggal_snow_fall_view(mPreferences!!.showSnawFallImagePos)
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
            //MainActivity.toggal_snow_fall_view(mPreferences!!.showSnawFallImagePos)
        } else {
            checkbox_snow!!.isChecked = false
            txt_snow_fall_effect!!.visibility = View.VISIBLE
            rc_flake!!.visibility = View.GONE
            //MainActivity.toggal_snow_fall_view(-1)
        }
    }
}

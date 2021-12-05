package net.fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.act_crop_image.*
import net.basicmodel.R
import net.event.MessageEvent
import net.general.GlobalApp
import org.greenrobot.eventbus.EventBus
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CropImageFragment : Fragment() {
    fun newInstance(type: String?): CropImageFragment {
        return CropImageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.act_crop_image, container, false)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        cropImageView!!.isAutoZoomEnabled = true
        cropImageView!!.setFixedAspectRatio(true)
        cropImageView!!.cropShape = CropImageView.CropShape.RECTANGLE
        cropImageView!!.setAspectRatio(800, 1080)
        cropImageView!!.setMinCropResultSize(800, 1080)


        // for fragment (DO NOT use `getActivity()`)
        CropImage.activity().start(requireContext(), this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (result != null) {
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri = result.uri
                    var img_path: String? = null
                    try {
                        img_path = compressImage(80, File(resultUri.path)).absolutePath
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // MainActivity.img_main_background.setImageURI(Uri.parse(img_path))
                    EventBus.getDefault().post(MessageEvent("setImageURI", Uri.parse(img_path)))
                    ThemeFragment.bmpPic = BitmapFactory.decodeFile(img_path)
                    Toast.makeText(context, "Background set successfully", Toast.LENGTH_SHORT)
                        .show()
                    if (GlobalApp.sharedpreferences == null) {
                        GlobalApp.savePrefrence(requireContext())
                    }
                    val editor: SharedPreferences.Editor = GlobalApp.sharedpreferences!!.edit()
                    editor.putString(GlobalApp.PREFREANCE_MAIN_IMAGE_BACKGROUND, img_path + "")
                    //  editor.putString(GlobalApp.PREFREANCE_MAIN_DEFAULT_BACKGROUND, GlobalApp.integerArrayList.get(0) + "");
                    //  editor.commit();
                    ThemeFragment.rel_seekbar!!.visibility = View.VISIBLE
                    editor.putInt(GlobalApp.BLUR_SEEKBAR_POS, 0)
                    editor.putInt(
                        GlobalApp.TRANCPARENT_COLOR,
                        GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE
                    )
                    editor.putInt(GlobalApp.TRANCPARENT_COLOR_SEEKBAR_POS, 0)
                    ThemeFragment.seek_blur!!.progress = 0
                    ThemeFragment.seek_transparent!!.progress = 0
//                    MainActivity.img_main_background_color.setBackgroundColor(GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE)
                    EventBus.getDefault().post(
                        MessageEvent(
                            "img_main_background_color",
                            GlobalApp.TRANCPARENT_COLOR_DEFAULT_VALUE
                        )
                    )
                    editor.commit()
                    (activity as AppCompatActivity?)!!.supportFragmentManager.popBackStack()
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                }
            } else {
                (activity as AppCompatActivity?)!!.supportFragmentManager.popBackStack()
            }
        }
    }

    @Throws(IOException::class)
    fun compressImage(maxSize: Int, file: File): File {
        val MAX_IMAGE_SIZE = maxSize * 1024 // max final file size
        if (!File(GlobalApp.MAIN_DIRECTORY).exists()) {
            File(GlobalApp.MAIN_DIRECTORY).mkdir()
        }
        val temp_file: File = File(
            GlobalApp.MAIN_DIRECTORY,
            ".temp.jpg"
        )
        if (temp_file.exists()) {
            temp_file.delete()
        }
        var bmpPic = BitmapFactory.decodeFile(file.absolutePath)
        if (bmpPic.width >= 1024 && bmpPic.height >= 1024) {
            val bmpOptions = BitmapFactory.Options()
            bmpOptions.inSampleSize = 1
            while (bmpPic.width >= 1024 && bmpPic.height >= 1024) {
                bmpOptions.inSampleSize++
                bmpPic = BitmapFactory.decodeFile(file.absolutePath, bmpOptions)
            }
        }
        var compressQuality = 104 // quality decreasing by 5 every loop. (start from 99)
        var streamLength = MAX_IMAGE_SIZE
        while (streamLength >= MAX_IMAGE_SIZE) {
            val bmpStream = ByteArrayOutputStream()
            compressQuality -= 5
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            Log.d("HELLO", "Size: $streamLength")
        }
        try {
            val bmpFile = FileOutputStream(temp_file)
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile)
            bmpFile.flush()
            bmpFile.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return temp_file
    }

    companion object {
        fun newInstance(type: String?): CropImageFragment {
            return CropImageFragment()
        }
    }
}

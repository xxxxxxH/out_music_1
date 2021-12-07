package net.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.MyApplication
import net.basicmodel.R
import net.general.GlobalApp
import net.utils.PreferencesUtility

class VideoMainFragment : Fragment() {
    var mPreferencesUtility: PreferencesUtility? = null
    var selectedrdbtn = "_display_name"
    var selectedchckbx = "ASC"
    var menu: Menu? = null
    var app: MyApplication? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view = inflater.inflate(R.layout.video_main_fragment, container, false)
        setHasOptionsMenu(true)
        app = requireActivity().application as MyApplication
        mPreferencesUtility = PreferencesUtility.getInstance(app!!.instance)
        if (mPreferencesUtility!!.viewType.equals("files")) {
            GlobalApp.fragmentReplaceTransitionVideo(VideoFragment(), null, requireActivity())
        } else {
            GlobalApp.fragmentReplaceTransitionVideo(FolderFragment(), null, requireActivity())
        }
        return view
    }
}

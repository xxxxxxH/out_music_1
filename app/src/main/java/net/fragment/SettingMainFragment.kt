package net.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.basicmodel.R
import net.general.GlobalApp

class SettingMainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.setting_main_fragment, container, false)
        setHasOptionsMenu(true)
        GlobalApp.fragmentReplaceTransitionSetting(SettingFragment(), null, requireActivity())
        return view
    }
}
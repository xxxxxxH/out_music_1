package net.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import net.basicmodel.R
import net.general.GlobalApp
import java.util.*

class EqualizerFragment : Fragment(),
    OnSeekBarChangeListener {
    private var band1Pos = 0
    private var band2Pos = 0
    private var band3Pos = 0
    private var band4Pos = 0
    private var band5Pos = 0
    private var band6Pos = 0
    private var band7Pos = 0
    private var band8Pos = 0
    private var bassBoost: BassBoost? = null
    private var bassBoostPos = 0
    private var spinnertPos = 0
    private var editor: SharedPreferences.Editor? = null
    private var equalizer: Equalizer? = null
    private var equalizerPresetSpinner: Spinner? = null
    private var isEqualizer = false
    private var isFirstLoad = false
    private var layout: RelativeLayout? = null
    private var maxLevel = 100
    private var minLevel = 0
    private var numBands = 0
    private var sbBassBoost: SeekBar? = null
    var sliderMin = arrayOfNulls<TextView>(MAX_SLIDERS)
    var sliderMax = arrayOfNulls<TextView>(MAX_SLIDERS)
    private val sliders = arrayOfNulls<SeekBar>(MAX_SLIDERS)
    private var sp: SharedPreferences? = null
    private var txtBass: TextView? = null
    var ll_equilizer: RelativeLayout? = null
    var checkbox_equilizer: CheckBox? = null
    var txt_equilizer: TextView? = null
    var text_slider__fq = arrayOfNulls<TextView>(MAX_SLIDERS)

    //   AppBarLayout appBarLayout;
    var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        isFirstLoad = true
        numBands = 0
        sp = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
        if (GlobalApp.sharedpreferences == null) {
            GlobalApp.savePrefrence(requireContext())
        }
    }

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var i: Int
        val view = inflater.inflate(R.layout.equalizer_fragment, container, false)
        initialization(view)
        editor = sp!!.edit()
        isEqualizer = sp!!.getBoolean(GlobalApp.IS_EQUALIZER, false)
        bassBoostPos = sp!!.getInt(GlobalApp.BASS_BOOST, 0)
        spinnertPos = sp!!.getInt("spinnerpos", 0)
        band1Pos = sp!!.getInt(GlobalApp.BAND1, 50)
        band2Pos = sp!!.getInt(GlobalApp.BAND2, 50)
        band3Pos = sp!!.getInt(GlobalApp.BAND3, 50)
        band4Pos = sp!!.getInt(GlobalApp.BAND4, 50)
        band5Pos = sp!!.getInt(GlobalApp.BAND5, 50)
        band6Pos = sp!!.getInt(GlobalApp.BAND6, 50)
        band7Pos = sp!!.getInt(GlobalApp.BAND7, 50)
        band8Pos = sp!!.getInt(GlobalApp.BAND8, 50)
        if (isEqualizer) {
            checkbox_equilizer!!.isChecked = true
            txt_equilizer!!.text = "On"
        } else {
            checkbox_equilizer!!.isChecked = false
            txt_equilizer!!.text = "Off"
        }
        equalizer = Equalizer(0, 0)
        if (equalizer != null) {
            equalizer!!.enabled = isEqualizer
            numBands = equalizer!!.numberOfBands.toInt()
            val r = equalizer!!.bandLevelRange
            minLevel = r[0].toInt()
            maxLevel = r[1].toInt()
            i = 0
            while (i < numBands && i < MAX_SLIDERS) {
                val freq_range = equalizer!!.getBandFreqRange(i.toShort())
                sliders[i]!!.setOnSeekBarChangeListener(this)
                if (i == 0) {
                    sliders[i]!!.progress = band1Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 1) {
                    sliders[i]!!.progress = band2Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 2) {
                    sliders[i]!!.progress = band3Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 3) {
                    sliders[i]!!.progress = band4Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 4) {
                    sliders[i]!!.progress = band5Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 5) {
                    sliders[i]!!.progress = band6Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 6) {
                    sliders[i]!!.progress = band7Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                } else if (i == 7) {
                    sliders[i]!!.progress = band8Pos
                    sliderMin[i]!!.text = (minLevel / 100).toString() + " dB"
                    sliderMax[i]!!.text = (maxLevel / 100).toString() + " dB"
                    text_slider__fq[i]!!.text =
                        (equalizer!!.getCenterFreq(i.toShort()) / 1000).toString() + " Hz"
                }
                i++
            }
        }
        i = numBands
        while (i < MAX_SLIDERS) {
            sliders[i]!!.visibility = MAX_SLIDERS
            sliderMin[i]!!.visibility = MAX_SLIDERS
            sliderMax[i]!!.visibility = MAX_SLIDERS
            text_slider__fq[i]!!.visibility = MAX_SLIDERS
            i++
        }
        bassBoost = BassBoost(0, 0)
        if (bassBoost != null) {
            sbBassBoost!!.progress = bassBoostPos
        } else {
            sbBassBoost!!.visibility = MAX_SLIDERS
            txtBass!!.visibility = MAX_SLIDERS
        }
        val equalizerPresetNames: ArrayList<String?> = ArrayList<String?>()
        val equalizerPresetSpinnerAdapter: ArrayAdapter<String?> = ArrayAdapter<String?>(
            requireActivity(), R.layout.spinner_item, equalizerPresetNames
        )
        equalizerPresetSpinner = view!!.findViewById<View>(R.id.spinner) as Spinner
        equalizerPresetSpinner!!.setSelection(spinnertPos)
        var i2 = 0.toShort()
        while (i2 < equalizer!!.numberOfPresets) {
            equalizerPresetNames.add(equalizer!!.getPresetName(i2))
            i2 = (i2 + 1).toShort()
        }
        equalizerPresetSpinner!!.adapter = equalizerPresetSpinnerAdapter
        equalizerPresetSpinner!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (isFirstLoad) {
                        isFirstLoad = false
                        return
                    }
                    equalizer!!.usePreset(position.toShort())
                    val numberFrequencyBands = equalizer!!.numberOfBands
                    val lowerEqualizerBandLevel = equalizer!!.bandLevelRange[0]
                    val r = equalizer!!.bandLevelRange
                    minLevel = r[0].toInt()
                    maxLevel = r[1].toInt()
                    editor!!.putInt("spinnerpos", position)
                    editor!!.commit()
                    run {
                        var i = 0.toShort()
                        while (i < numberFrequencyBands) {
                            val equalizerBandIndex = i
                            sliders[i.toInt()]!!.progress =
                                equalizer!!.getBandLevel(i) * 100 / (maxLevel - minLevel) + 50
                            if (i == 0.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND1, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 1.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND2, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 2.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND3, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 3.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND4, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 4.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND5, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 5.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND6, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 6.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND7, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            } else if (i == 7.toShort()) {
                                editor!!.putInt(
                                    GlobalApp.BAND8, sliders[i.toInt()]!!
                                        .progress
                                )
                                editor!!.commit()
                            }
                            i = (i + 1).toShort()
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
        checkIfEqualizerOn()
        updateSliders()
        updateBassBoost()
        ll_equilizer!!.setOnClickListener {
            if (checkbox_equilizer!!.isChecked) {
                checkbox_equilizer!!.isChecked = false
                txt_equilizer!!.text = "Off"
            } else {
                checkbox_equilizer!!.isChecked = true
                txt_equilizer!!.text = "On"
            }
            /* EqualizerFragment.this.editor.putBoolean(Global.IS_EQUALIZER, checkbox_equilizer.isChecked());
                    EqualizerFragment.this.editor.commit();*/
        }
        checkbox_equilizer!!.setOnCheckedChangeListener { buttonView, isChecked ->
            equalizer!!.enabled = isChecked
            isEqualizer = isChecked
            if (isEqualizer) {
                Toast.makeText(
                    activity,
                    "Equalizer On",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    activity,
                    "Equalizer Off",
                    Toast.LENGTH_SHORT
                ).show()
            }
            checkIfEqualizerOn()
            //  EqualizerFragment.this.changeEqualizerColor();
            editor!!.putBoolean(GlobalApp.IS_EQUALIZER, isChecked)
            editor!!.commit()

            //setTheme();
        }
        return view
    }

    override fun onProgressChanged(seekBar: SeekBar, level: Int, fromTouch: Boolean) {
        var z = true
        try {
            if (seekBar === sbBassBoost) {
                val bassBoost = bassBoost
                if (level <= 0) {
                    z = false
                }
                bassBoost!!.enabled = z
                this.bassBoost!!.setStrength(level.toShort())
                editor!!.putInt(GlobalApp.BASS_BOOST, sbBassBoost!!.progress)
                editor!!.commit()
            } else if (equalizer != null) {
                val newLevel = minLevel + (maxLevel - minLevel) * level / 100
                for (i in 0 until numBands) {
                    if (sliders[i] === seekBar) {
                        equalizer!!.setBandLevel(i.toShort(), newLevel.toShort())
                        if (i == 0) {
                            editor!!.putInt(GlobalApp.BAND1, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 1) {
                            editor!!.putInt(GlobalApp.BAND2, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 2) {
                            editor!!.putInt(GlobalApp.BAND3, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 3) {
                            editor!!.putInt(GlobalApp.BAND4, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 4) {
                            editor!!.putInt(GlobalApp.BAND5, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 5) {
                            editor!!.putInt(GlobalApp.BAND6, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 6) {
                            editor!!.putInt(GlobalApp.BAND7, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else if (i == 7) {
                            editor!!.putInt(GlobalApp.BAND8, sliders[i]!!.progress)
                            editor!!.commit()
                            return
                        } else {
                            return
                        }
                    }
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        }
    }

    fun updateSliders() {
        for (i in 0 until numBands) {
            var level: Int
            level = if (equalizer != null) {
                equalizer!!.getBandLevel(i.toShort()).toInt()
            } else {
                0
            }
            sliders[i]!!.progress = level * 100 / (maxLevel - minLevel) + 50
        }
    }

    fun updateBassBoost() {
        if (bassBoost != null) {
            sbBassBoost!!.progress = bassBoost!!.roundedStrength.toInt()
        } else {
            sbBassBoost!!.progress = 0
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

    private fun checkIfEqualizerOn() {
        var i = 0
        while (i < numBands && i < MAX_SLIDERS) {
            sliders[i]!!.isEnabled = isEqualizer
            i++
        }
        sbBassBoost!!.isEnabled = isEqualizer
        bassBoost!!.enabled = isEqualizer
        equalizerPresetSpinner!!.isEnabled = isEqualizer
    }

    fun initialization(view: View) {
        toolbar = view.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayShowHomeEnabled(true)
        layout = view.findViewById<View>(R.id.main_layout) as RelativeLayout
        ll_equilizer = view.findViewById<View>(R.id.rel_equilizer) as RelativeLayout
        checkbox_equilizer = view.findViewById<View>(R.id.checkbox_equilizer) as CheckBox
        txt_equilizer = view.findViewById<View>(R.id.txt_equilizer) as TextView
        sbBassBoost = view.findViewById<View>(R.id.bass_boost) as SeekBar
        sbBassBoost!!.setOnSeekBarChangeListener(this)
        txtBass = view.findViewById<View>(R.id.txtBass) as TextView
        sliders[0] = view.findViewById<View>(R.id.slider_1) as SeekBar
        sliders[1] = view.findViewById<View>(R.id.slider_2) as SeekBar
        sliders[2] = view.findViewById<View>(R.id.slider_3) as SeekBar
        sliders[3] = view.findViewById<View>(R.id.slider_4) as SeekBar
        sliders[4] = view.findViewById<View>(R.id.slider_5) as SeekBar
        sliders[5] = view.findViewById<View>(R.id.slider_6) as SeekBar
        sliders[6] = view.findViewById<View>(R.id.slider_7) as SeekBar
        sliders[7] = view.findViewById<View>(R.id.slider_8) as SeekBar
        sliderMin[0] = view.findViewById<View>(R.id.text_slider_1) as TextView
        sliderMin[1] = view.findViewById<View>(R.id.text_slider_2) as TextView
        sliderMin[2] = view.findViewById<View>(R.id.text_slider_3) as TextView
        sliderMin[3] = view.findViewById<View>(R.id.text_slider_4) as TextView
        sliderMin[4] = view.findViewById<View>(R.id.text_slider_5) as TextView
        sliderMin[5] = view.findViewById<View>(R.id.text_slider_6) as TextView
        sliderMin[6] = view.findViewById<View>(R.id.text_slider_7) as TextView
        sliderMin[7] = view.findViewById<View>(R.id.text_slider_8) as TextView
        sliderMax[0] = view.findViewById<View>(R.id.text_slider_11) as TextView
        sliderMax[1] = view.findViewById<View>(R.id.text_slider_22) as TextView
        sliderMax[2] = view.findViewById<View>(R.id.text_slider_33) as TextView
        sliderMax[3] = view.findViewById<View>(R.id.text_slider_44) as TextView
        sliderMax[4] = view.findViewById<View>(R.id.text_slider_55) as TextView
        sliderMax[5] = view.findViewById<View>(R.id.text_slider_66) as TextView
        sliderMax[6] = view.findViewById<View>(R.id.text_slider_77) as TextView
        sliderMax[7] = view.findViewById<View>(R.id.text_slider_88) as TextView
        text_slider__fq[0] = view.findViewById<View>(R.id.text_slider__fq_1) as TextView
        text_slider__fq[1] = view.findViewById<View>(R.id.text_slider__fq_2) as TextView
        text_slider__fq[2] = view.findViewById<View>(R.id.text_slider__fq_3) as TextView
        text_slider__fq[3] = view.findViewById<View>(R.id.text_slider__fq_4) as TextView
        text_slider__fq[4] = view.findViewById<View>(R.id.text_slider__fq_5) as TextView
        text_slider__fq[5] = view.findViewById<View>(R.id.text_slider__fq_6) as TextView
        text_slider__fq[6] = view.findViewById<View>(R.id.text_slider__fq_7) as TextView
        text_slider__fq[7] = view.findViewById<View>(R.id.text_slider__fq_8) as TextView
    }

    companion object {
        const val MAX_SLIDERS = 8
    }
}

package com.local.amjbc.chandacal

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.*
import android.preference.Preference.OnPreferenceChangeListener
import com.local.amjbc.R

class DetailActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    var sp: SharedPreferences? = null
    var CheckboxPreference: Boolean = false
    var remoteSyncCheck: Boolean = false
    var moosiBox: CheckBoxPreference? = null
    var remoteSyncronization: CheckBoxPreference? = null
    var monthlyCheck: CheckBoxPreference? = null
    var organisation: ListPreference? = null
    var wassiyatNo: EditTextPreference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.activity_preference2)
        sp = PreferenceManager.getDefaultSharedPreferences(baseContext)

        moosiBox = findPreference("moosiPre") as CheckBoxPreference
        monthlyCheck = findPreference("monthlyPre") as CheckBoxPreference
        organisation = findPreference("organisationPre") as ListPreference
        wassiyatNo = findPreference("wassiyatNoPre") as EditTextPreference
        //  remoteSyncronization =  (CheckBoxPreference) findPreference("remoteSync");

        //remoteSyncronization.setDefaultValue(false);

        wassiyatNo!!.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val editor = sp!!.edit()
            editor.putString("wassiyatno", newValue as String)
            editor.apply()
            false
        }

        moosiBox!!.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            if (moosiBox!!.isChecked) {
                wassiyatNo!!.isEnabled = false
            } else {
                wassiyatNo!!.isEnabled = false
            }
            true
        }

        organisation!!.onPreferenceChangeListener = OnPreferenceChangeListener { preference, newValue ->
            val editor = sp!!.edit()
            editor.putString("org", newValue as String)
            editor.apply()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        // Set up a listener whenever a key changes
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        wassiyatNo!!.text = sp!!.getString("wassiyatno", "")
        organisation!!.value = sp!!.getString("org", "Ansaar")

        if (sp!!.getBoolean("moosiPre", true)) wassiyatNo!!.isEnabled = true else wassiyatNo!!.isEnabled = false
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener whenever a key changes
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {}
}

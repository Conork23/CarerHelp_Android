package com.intimealarm.carerhelp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 06/12/2016.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    // On Create
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Prefrences from prefrence.xml
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference company_pref = findPreference(SettingsActivity.COMPANY_NUMBER);
        company_pref.setSummary(sharedPref.getString(SettingsActivity.COMPANY_NUMBER,""));
        Preference login_pref = findPreference(SettingsActivity.LOGIN_TEMPLATE);
        login_pref.setSummary(sharedPref.getString(SettingsActivity.LOGIN_TEMPLATE,""));
        Preference logout_pref = findPreference(SettingsActivity.LOGOUT_TEMPLATE);
        logout_pref.setSummary(sharedPref.getString(SettingsActivity.LOGOUT_TEMPLATE,""));


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference connectionPref = findPreference(s);
        connectionPref.setSummary(sharedPreferences.getString(s, ""));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
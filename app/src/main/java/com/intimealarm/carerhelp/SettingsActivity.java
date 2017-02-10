package com.intimealarm.carerhelp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 06/12/2016.
 */

public class SettingsActivity extends AppCompatActivity{
    final static String COMPANY_NUMBER = "company_number";
    final static String LOGIN_TEMPLATE = "sms_login_template";
    final static String LOGOUT_TEMPLATE = "sms_logout_template";

    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Assign Fragments
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


    }

}

package com.quest.geotwit;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by stefan on 11/15/16.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}

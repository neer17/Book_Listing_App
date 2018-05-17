package com.example.neerajsewani.book_listing_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 *  At the time of defining the widget in PreferenceScreen we are passing a key
 *  through which we can address the correct Preference and get it's value
 */
public class SettingsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class CustomPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //  this is our PreferenceScreen
            addPreferencesFromResource(R.xml.settings_main);

            //  at the time of creating the fragment we are getting the preference
            //  and sending it to the helping method
            //  a Preference is identified by the default value that we have passed during it's defining in the

            //  for EditTextPreference
            Preference maxResult = findPreference(getString(R.string.settings_default_result_key));
            bindPreferenceSummaryToValue(maxResult);

            //  for ListPreference
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }

        //  onPreferenceChangeListener has this abstract method
        //  modified it to display the value of the preference
        //  calling it in the helping method bindPreferenceSummaryToValue()
        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String valueOfPreference = o.toString();    //  value is stored in o

            //  for ListPreference
            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference)preference;
                int prefIndex = listPreference.findIndexOfValue(valueOfPreference);

                //  settings label of the selected value of the item of the list
                if(prefIndex >= 0){
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }else {
                preference.setSummary(valueOfPreference);
            }
            return true;
        }

        //  here we are attaching the listener to preference so that any changes are identified
        //  the we are getting the SharedPreference related to that particular preference which is storing the value
        //  then getting the value out of that shared preference
        //  then calling onPreferenceChange() method
        private void bindPreferenceSummaryToValue(Preference preference){
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            String preferenceValue = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceValue);
        }
    }


}

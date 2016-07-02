package com.example.souvik.remindertemplate.Activities;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.example.souvik.remindertemplate.R;
import com.example.souvik.remindertemplate.RemindMe;

/**
 * Created by Souvik on 3/31/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

//	private static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    /*@Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }*/


    @Override
    public void onResume(){
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updatePreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(key);
    }

    private void updatePreferences() {
        updatePreference(RemindMe.TIME_OPTION);
        updatePreference(RemindMe.DATE_RANGE);
        updatePreference(RemindMe.DATE_FORMAT);
        updatePreference(RemindMe.RINGTONE_PREF);
    }

    private void updatePreference(String key){

        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
            return;
        }

/*		if (pref instanceof EditTextPreference){
			EditTextPreference editTextPreference =  (EditTextPreference) pref;
			if (editTextPreference.getText().trim().length() > 0){
				editTextPreference.setSummary("Entered Name is  " + editTextPreference.getText());
			}else{
				editTextPreference.setSummary("Enter Your Name");
			}
		}*/

        if (RemindMe.RINGTONE_PREF.equals(key)) {
            Uri ringtoneUri = Uri.parse(RemindMe.getRingtone());
            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            if (ringtone != null) pref.setSummary(ringtone.getTitle(getActivity()));
        }
    }

/*	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

		if ("about_pref".equals(preference.getKey())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Your Message");
			builder.setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// action on dialog close
						}
					});
			builder.show();
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}*/

}

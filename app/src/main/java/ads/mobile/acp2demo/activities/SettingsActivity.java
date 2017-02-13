package ads.mobile.acp2demo.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import ads.mobile.acp2demo.R;


/**
 * Created by Salla on 11.2.2017.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}

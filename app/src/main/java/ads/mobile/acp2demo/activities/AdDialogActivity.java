package ads.mobile.acp2demo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import ads.mobile.acp2demo.Provider;
import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.classes.AdFloatingViewManager;
import ads.mobile.acp2demo.db.DbManager;
import ads.mobile.acp2demo.services.AppCheckerService;

import static ads.mobile.acp2demo.activities.MainActivity.AD_NAME_PREF;
import static ads.mobile.acp2demo.activities.MainActivity.CURRENT_FOREGROUD_APP_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.CURRENT_TESTCASE_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.USER_NAME_PREF;

public class AdDialogActivity extends AppCompatActivity {
    private static final String TAG = AdDialogActivity.class.getSimpleName();
    private static SharedPreferences pref;
    private long adDialogCreated = 0;
    private int adCounter = 0;
    int[] adArray = {R.drawable.img_test1, R.drawable.img_test2, R.drawable.img_test3, R.drawable.img_test4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        //Save time when big ad is shown
        adDialogCreated = System.currentTimeMillis();
        long duration = adDialogCreated - AdFloatingViewManager.getAdTouchedTime();
        //insert event and time between ad is touched and big ad is shown.
        DbManager.insertEventRow(getApplicationContext(), duration, Provider.EventEntry.BIG_AD_SHOWN,
                pref.getString(USER_NAME_PREF, ""),
                pref.getString(AD_NAME_PREF, ""),
                pref.getString(CURRENT_FOREGROUD_APP_NAME, ""),
                pref.getString(CURRENT_TESTCASE_NAME, "") );
        //Hide Titlebar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //Modal dialog.
        this.setFinishOnTouchOutside(false);

        setContentView(R.layout.ad_dialog);
        ImageView ad = (ImageView) findViewById(R.id.ad_imageView);
        final Bundle bundle = getIntent().getExtras();
        adCounter = bundle.getInt("adCounter");
        ad.setImageResource(adArray[adCounter]);
        ImageView image = (ImageView) findViewById(R.id.closeImageView);

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAdTerminatedSignal();
                //ad is closed - time when ad is shown
                long duration = System.currentTimeMillis() - adDialogCreated;
                DbManager.insertEventRow(getApplicationContext(), duration, Provider.EventEntry.BIG_AD_CLOSED,
                        pref.getString(USER_NAME_PREF, ""),
                        pref.getString(AD_NAME_PREF, ""),
                        pref.getString(CURRENT_FOREGROUD_APP_NAME, ""),
                        pref.getString(CURRENT_TESTCASE_NAME, "") );
                finish();
            }
        });

    }

    public void sendAdTerminatedSignal(){
        Intent i = new Intent(AppCheckerService.AD_TERMINATED);
        i.putExtra("reason", "dno");
        sendBroadcast(i);
        Log.d(TAG, "Ad STOP broadcast is sended.");
    }
}

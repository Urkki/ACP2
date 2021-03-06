package ads.mobile.acp2demo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import static ads.mobile.acp2demo.activities.MainActivity.PREF_AD_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_CURRENT_FOREGROUD_APP_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_CURRENT_TESTCASE_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_USER_NAME;

public class AdDialogActivity extends AppCompatActivity {
    private static final String TAG = AdDialogActivity.class.getSimpleName();
    private static SharedPreferences pref;

    public static boolean isBigAdShown() {
        return bigAdShown;
    }

    private static boolean bigAdShown = false;

    private long adDialogCreated = 0;
//    private int adCounter = 0;
//    int[] adArray = {R.drawable.bk_big_ad, R.drawable.hese_big_ad, R.drawable.hese_big_ad, R.drawable.hese_big_ad, R.drawable.hese_big_ad};
//    String[] uriArray = {"http://bk.com", "http://hesburger.fi", "http://google.com", "http://google.com", "http://google.com"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bigAdShown = true;
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        //Save time when big ad is shown
        adDialogCreated = System.currentTimeMillis();
        long duration = adDialogCreated - AdFloatingViewManager.getAdTouchedTime();

        //Hide Titlebar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //Modal dialog.
        this.setFinishOnTouchOutside(false);

        setContentView(R.layout.ad_dialog);
        ImageView ad = (ImageView) findViewById(R.id.ad_imageView);
        final Bundle bundle = getIntent().getExtras();
        int imgResId = bundle.getInt("img_res_id");
        ad.setImageResource(imgResId);

//        ad.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                long duration = System.currentTimeMillis() - adDialogCreated;
//                DbManager.insertEventRow(getApplicationContext(), duration, Provider.EventEntry.BIG_AD_LINK_CLICKED,
//                        pref.getString(PREF_USER_NAME, ""),
//                        pref.getString(PREF_AD_NAME, ""),
//                        pref.getString(PREF_CURRENT_FOREGROUD_APP_NAME, ""),
//                        pref.getString(PREF_CURRENT_TESTCASE_NAME, "") );
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                intent.setData(Uri.parse(uriArray[adCounter]));
//                startActivity(intent);
//                bigAdShown = false;
//            }
//        });


        ImageView image = (ImageView) findViewById(R.id.closeImageView);

        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAdTerminatedSignal();
                //ad is closed - time when ad is shown
                long duration = System.currentTimeMillis() - adDialogCreated;
                DbManager.insertEventRow(getApplicationContext(), duration, Provider.EventEntry.BIG_AD_CLOSED,
                        pref.getString(PREF_USER_NAME, ""),
                        pref.getString(PREF_AD_NAME, ""),
                        pref.getString(PREF_CURRENT_FOREGROUD_APP_NAME, ""),
                        pref.getString(PREF_CURRENT_TESTCASE_NAME, "") );
                bigAdShown = false;
                finish();
            }
        });
        //update image name
        String img_name = getResources().getResourceEntryName(imgResId);
        pref.edit().putString(MainActivity.PREF_AD_NAME, img_name).commit();

        //insert event and time between ad is touched and big ad is shown.
        DbManager.insertEventRow(getApplicationContext(), duration, Provider.EventEntry.BIG_AD_SHOWN,
                pref.getString(PREF_USER_NAME, ""),
                pref.getString(PREF_AD_NAME, ""),
                pref.getString(PREF_CURRENT_FOREGROUD_APP_NAME, ""),
                pref.getString(PREF_CURRENT_TESTCASE_NAME, "") );
    }

    public void sendAdTerminatedSignal(){
        Intent i = new Intent(AppCheckerService.AD_TERMINATED);
        i.putExtra("reason", "dno");
        sendBroadcast(i);
        Log.d(TAG, "Ad STOP broadcast is sended.");
    }
}

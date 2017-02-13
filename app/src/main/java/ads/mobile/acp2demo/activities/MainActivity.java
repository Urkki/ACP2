package ads.mobile.acp2demo.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.aware.Aware;
import java.util.ArrayList;
import java.util.List;

import ads.mobile.acp2demo.classes.AppInfo;
import ads.mobile.acp2demo.classes.AppsList;
import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.databinding.ActivityMainBinding;
import ads.mobile.acp2demo.services.AppCheckerService;

import static ads.mobile.acp2demo.Plugin.NAME;
import static ads.mobile.acp2demo.Settings.STATUS_PLUGIN_AD_DEMO;
import static android.os.Build.VERSION_CODES.M;


public class MainActivity extends AppCompatActivity {

    public AppsList apps;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    public static final String USER_NAME_PREF = "CURRENT_USER_NAME";
    public static final String AD_NAME_PREF = "CURRENT_AD_NAME";
    public static final String CURRENT_FOREGROUD_APP_NAME = "CURRENT_FOREGROUND_APP_NAME";
    public static final String CURRENT_TESTCASE_NAME = "CURRENT_TESTCASE_NAME";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get installed apps
        apps = AppsList.load(getApplicationContext());
        if (apps == null) {
            apps = new AppsList(loadInstalledApps());
        }

        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WAKE_LOCK, Manifest.permission.READ_PHONE_STATE
              };
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        
        Aware.DEBUG = false;
        //Initialise AWARE
        Intent aware = new Intent(this, Aware.class);
        startService(aware);
        Aware.joinStudy(getApplicationContext(), getString(R.string.study_url));
        Aware.setSetting(getApplicationContext(), STATUS_PLUGIN_AD_DEMO, true);
        Aware.startPlugin(this, NAME);

        //Bind list to UI and attach listener
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //ask permissions for tracking foreground app.
        if(!hasUsageStatsPermission(this.getApplicationContext())) {
            requestUsageStatsPermission();
        }
        //overLayPermission
        requestOverLayPermission();
        //Write to external storage.
        requestWriteExternalStoragePermission();


        binding.setApps(apps);
        ListView listView = (ListView) findViewById(R.id.appListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cbx = (CheckBox) view.findViewById(R.id.cbx);
                cbx.setChecked(!cbx.isChecked()); // invert checkbox
                boolean success = apps.save(getApplicationContext());
                //notify service that list has changed...
                Intent i = new Intent(AppCheckerService.SEND_LIST_CHANGED);
                Log.d(TAG, "Sending broadcast.");
                sendBroadcast(i);
                if (!success) {
                    Toast.makeText(getApplicationContext(),
                            "SAVING FAILED....", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
        //Start service.
        AppCheckerService.start(getApplicationContext());

    }
    @TargetApi(M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                requestOverLayPermission();
            }
            else
            {
                //do as per your logic
            }
        }
    }
    private ArrayList<AppInfo> loadInstalledApps()
    {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        ArrayList<AppInfo> infos = new ArrayList<>();
        for (int i=0; i < packList.size(); i++)
        {
            PackageInfo packInfo = packList.get(i);
            //Take only installed apps.
//            if ( (packInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                AppInfo inf = new AppInfo(appName, packInfo);
                infos.add(inf);
            }
        }
        return infos;
    }

    void requestUsageStatsPermission() {
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private void requestWriteExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    public void requestOverLayPermission() {
        if (Build.VERSION.SDK_INT >= M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is presented
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Handles actionbar item clicks here. The action bar will automatically
        // handle clicks on the Home/Up button, so long as you specify a parent
        //activity in AndroidManifest.xml
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent b = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(b);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

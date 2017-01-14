package ads.mobile.acp2demo.activities;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ads.mobile.acp2demo.classes.AppInfo;
import ads.mobile.acp2demo.classes.AppsList;
import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.databinding.ActivityMainBinding;

import ads.mobile.acp2demo.services.AppCheckerService;

public class MainActivity extends AppCompatActivity {

    public AppsList apps;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get installed apps
        apps = AppsList.load(getApplicationContext());
        if (apps == null) {
            apps = new AppsList(loadInstalledApps());
        }



        //Bind list to UI and attach listener
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //ask permissions for tracking foreground app.
        if(!hasUsageStatsPermission(this.getApplicationContext())) {
            requestUsageStatsPermission();
        }
        //overLayPermission
        requestOverLayPermission();
        binding.setApps(apps);
        ListView listView = (ListView) findViewById(R.id.appListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cbx = (CheckBox) view.findViewById(R.id.cbx);
                cbx.setChecked(!cbx.isChecked()); // invert checkbox
                boolean success = apps.save(getApplicationContext());

//                AppChecker appChecker = new AppChecker();
//                String packageName = appChecker.getForegroundApp(getApplicationContext());
//                if (packageName != null){
//                    Log.i("packageName", packageName);
//                }
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
        Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_LONG).show();
//        if (savedInstanceState == null) {
//            FragmentTransaction ft = getFragmentManager().beginTransaction();
//            ft.add(R.id.activity_main, FloatingViewControlFragment.newInstance());
//            ft.commit();
//        }
    }
    @TargetApi(Build.VERSION_CODES.M)
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
            if ( (packInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
//                Log.e("App № " + Integer.toString(i), appName);
                AppInfo inf = new AppInfo(appName, packInfo);
                infos.add(inf);
//                AppListElementLayoutBinding binding = DataBindingUtil.setContentView(this, R.layout.app_list_element);
//                binding.setAppInfo(inf);

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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    public void requestOverLayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}

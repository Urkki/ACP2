package ads.mobile.acp2demo;

import android.content.pm.PackageInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ads.mobile.acp2demo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public AppsList apps;

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
        binding.setApps(apps);
        ListView listView = (ListView) findViewById(R.id.appListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cbx = (CheckBox) view.findViewById(R.id.cbx);
                cbx.setChecked(!cbx.isChecked()); // invert checkbox
                boolean success = apps.save(getApplicationContext());
                Log.i("SUCCESSS", "YEYEYE");
                if (!success) {
                    Toast.makeText(getApplicationContext(),
                            "SAVING FAILED....", Toast.LENGTH_LONG)
                            .show();
                }

            }
        });
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
//                AppListElementLayoutBinding binding = DataBindingUtil.setContentView(this, R.layout.app_list_element_layout);
//                binding.setAppInfo(inf);

            }

        }
        return infos;
    }
}

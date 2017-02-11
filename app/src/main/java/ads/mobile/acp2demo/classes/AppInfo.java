package ads.mobile.acp2demo.classes;

import android.content.pm.PackageInfo;
import android.databinding.BaseObservable;

import java.io.Serializable;


/**
 * Created by Urkki on 8.1.2017.
 */

public class AppInfo extends BaseObservable implements Serializable {
    public String AppName;
    public String appPackageName;
    public Boolean IsSelected = false;

    public AppInfo(String appName, PackageInfo i)
    {
        this.AppName = appName;
        this.appPackageName = i.packageName;
    }
}

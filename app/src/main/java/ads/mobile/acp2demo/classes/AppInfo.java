package ads.mobile.acp2demo.classes;

import android.content.pm.PackageInfo;
import android.databinding.BaseObservable;
import android.databinding.Bindable;


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

//    @Bindable
//    public String getAppName()
//    {
//        return this.AppName;
//    }
//
//    public void setAppName(String mAppName) {
//        this.AppName = mAppName;
//        notifyPropertyChanged(com.android.databinding.library.baseAdapters.BR.appName);
//    }
//
//    @Bindable
//    public Boolean getIsUsed() {
//        return IsSelected;
//    }
//
//    public void setIsUsed(boolean newVal) {
//        IsSelected = newVal;
//        notifyPropertyChanged(BR.isUsed);
//    }

}

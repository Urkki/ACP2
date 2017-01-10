package ads.mobile.acp2demo;

import android.content.pm.PackageInfo;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.*;
import com.android.databinding.library.baseAdapters.BR;

import java.io.Serializable;


/**
 * Created by Urkki on 8.1.2017.
 */

public class AppInfo extends BaseObservable implements Serializable {
    public String AppName;
//    private PackageInfo mInfo;
    public Boolean IsUsed = false;

    public AppInfo(String appName, PackageInfo i)
    {
        this.AppName = appName;
//        this.mInfo = i;
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
//        return IsUsed;
//    }
//
//    public void setIsUsed(boolean newVal) {
//        IsUsed = newVal;
//        notifyPropertyChanged(BR.isUsed);
//    }

}

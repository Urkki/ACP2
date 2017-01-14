package ads.mobile.acp2demo.classes;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import ads.mobile.acp2demo.services.AppCheckerService;

/**
 * Created by Urkki on 8.1.2017.
 */

public class AppsList implements Serializable {

    private static final String FILENAME = "saved_app_list.txt";
    private static final String TAG = AppCheckerService.class.getSimpleName();

    public ObservableArrayList<AppInfo> appsList = new ObservableArrayList<>();


    public AppsList()
    {

    }

    public AppsList(ArrayList<AppInfo> infos )
    {
        for (AppInfo i : infos) {
            appsList.add(i);
        }
    }
//    public void setApps(ArrayList<AppInfo> apps)
//    {
//        this.apps = apps;
//    }

    public ObservableArrayList<AppInfo> getApps()
    {
        return appsList;
    }

    public boolean save(Context c) {
        try {
            String path = c.getFilesDir().getAbsolutePath();
            FileOutputStream outStream = new FileOutputStream(path + "/" + FILENAME);
            ObjectOutputStream objOutStream;
            objOutStream = new ObjectOutputStream(outStream);
            objOutStream.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public ArrayList<String> getSelectedAppPackageNames(){
        ArrayList<String> selectedApps = new ArrayList<>();
        try {
            for (AppInfo i : this.appsList) {
                if(i.IsSelected){
                    selectedApps.add(i.appPackageName);
                }
            }
        } catch ( Exception e){
            e.printStackTrace();
        }

        return selectedApps;
    }

    public static AppsList load(Context c) {
        String path = c.getFilesDir().getAbsolutePath();
        String[] asdf = c.fileList();
        if (!new File(path + "/" + FILENAME).exists()) {
            Log.i(TAG, "Applist file at " + path + "/" + FILENAME + " not found.");
            return null;
        }

        FileInputStream inputStream;
        boolean failed = true;
        try {
            inputStream = new FileInputStream (path + "/" + FILENAME);
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            Object obj = objInputStream.readObject();
            failed = false;
            return (AppsList) obj;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally { //Remove corrupted file if necessary.
            if (failed) {
                c.deleteFile(FILENAME);
                Log.i(TAG, "Applist file at " + path + "/" + FILENAME + " was corrupted and deleted.");
            }
        }
        return null;
    }
}

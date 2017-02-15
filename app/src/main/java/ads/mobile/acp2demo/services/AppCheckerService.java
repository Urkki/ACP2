package ads.mobile.acp2demo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.rvalerio.fgchecker.AppChecker;

import java.util.ArrayList;
import java.util.Arrays;

import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.activities.MainActivity;
import ads.mobile.acp2demo.classes.AppsList;
import ads.mobile.acp2demo.db.DbManager;

import static ads.mobile.acp2demo.Provider.EventEntry.SMALL_AD_IS_CREATED;
import static ads.mobile.acp2demo.Provider.EventEntry.SMALL_AD_IS_REMOVED_BY_SYSTEM;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_AD_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_CURRENT_FOREGROUD_APP_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_CURRENT_TESTCASE_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_USER_NAME;


public class AppCheckerService extends Service {

    private final static int NOTIFICATION_ID = 1234;
    private static final String TAG = AppCheckerService.class.getSimpleName();
    private final static String STOP_SERVICE = AppCheckerService.class.getPackage()+".stop";
    public final static String SEND_LIST_CHANGED = "listChanged";
    public final static String AD_TERMINATED = "ad_is_terminated";
    private ArrayList<String> selectedApps;

    private BroadcastReceiver stopServiceReceiver;
    private BroadcastReceiver listChangedReceiver;
    private BroadcastReceiver adIsTerminatedReceiver;

    private AppChecker appChecker;
    private boolean adIsTriggered = false;
    private boolean appListIsChanged = true;
    private static SharedPreferences pref;
    private static long adTriggerTime = 0;
    private int adCounter = 0;

    public static void start(Context context) {
        context.startService(new Intent(context, AppCheckerService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, AppCheckerService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceivers();
        startChecker();
        Notification n = createStickyNotification();
        startForeground(NOTIFICATION_ID, n);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopChecker();
        removeNotification();
        unregisterReceivers();
        removeAdView();
        stopForeground(true);
        stopSelf();
    }

    private void startChecker() {
        appChecker = new AppChecker();
        appChecker
                .other(new AppChecker.Listener() {
                    @Override
                    public void onForeground(String packageName) {
                        //Update foreground app
                        pref.edit().putString(MainActivity.PREF_CURRENT_FOREGROUD_APP_NAME, packageName).apply();
                        //UI list has changed.
                        if(appListIsChanged) {
                            getUpdatedAppList(packageName);
                        }
                        if(selectedApps != null) {
                            //Foreground app is selected and ad is not triggered before.
                            if(selectedApps.contains(packageName) && !adIsTriggered) {
                                Log.d(TAG, "ad is triggered.");
                                adIsTriggered = true;
                                DbManager.insertDeviceInfoRow(getApplicationContext(), pref.getString(MainActivity.PREF_USER_NAME, ""));
                                //Shows ad only if last ad has been shown 20 seconds ago
                                while (true) {
                                    if (System.currentTimeMillis() - adTriggerTime >= 20000){
                                        showAdView();
                                        break;
                                    }
                                }
                            }
                            //Foreground app is not selected and ad is triggered before.
                            if(!selectedApps.contains(packageName) && adIsTriggered) {
                                Log.d(TAG, "ad is removed.");
                                adIsTriggered = false;
                                removeAdView();
                            }
                        }

                    }
                })
                .timeout(5000)
                .start(this);
    }

    private void getUpdatedAppList(String packageName){
        Log.d(TAG, "Fetching new list...");
        AppsList tmp = AppsList.load(getApplicationContext());
        if(tmp != null){
            selectedApps = tmp.getSelectedAppPackageNames();
            Log.d(TAG, "selectedApps: " + Arrays.toString(selectedApps.toArray()));
            Log.d(TAG, "Current Foreground app: " + packageName +  " isSelected: "
                    + Boolean.toString(selectedApps.contains(packageName)) );
            appListIsChanged = false;
        }
    }

    public void showAdView(){
        //Pass adCounter value to adService
        Intent intent = new Intent(getBaseContext(), AdViewService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("adCounter", adCounter);
        intent.putExtras(bundle);
        getBaseContext().startService(intent);
        //Increments adCounter to change ad, remember to edit limit if ads are added or removed
        adCounter += 1;
        if(adCounter > 4) {
            adCounter = 0;
        }
        //Get current time
        adTriggerTime = System.currentTimeMillis();
    }

    public void removeAdView() {
        getBaseContext().stopService(new Intent(getBaseContext(), AdViewService.class));
        long dur = System.currentTimeMillis() - adTriggerTime;
        DbManager.insertEventRow(getApplicationContext(), dur, SMALL_AD_IS_REMOVED_BY_SYSTEM,
                pref.getString(PREF_USER_NAME, ""),
                pref.getString(PREF_AD_NAME, ""),
                pref.getString(PREF_CURRENT_FOREGROUD_APP_NAME, ""),
                pref.getString(PREF_CURRENT_TESTCASE_NAME, "") );
    }

    public static long getAdTriggerTime() {
        return adTriggerTime;
    }

    private void stopChecker() {
        appChecker.stop();
    }

    private void registerReceivers() {
        stopServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "is stopping.");
//                onDestroy();
                stopForeground(true);
                stopSelf();
            }
        };
        listChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "LIST HAS CHANGED.");
                appListIsChanged = true;
            }
        };
        adIsTerminatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Ad is terminated.");
                adIsTriggered = false;
            }
        };
        registerReceiver(stopServiceReceiver, new IntentFilter(STOP_SERVICE));
        registerReceiver(listChangedReceiver, new IntentFilter(SEND_LIST_CHANGED));
        registerReceiver(adIsTerminatedReceiver, new IntentFilter(AD_TERMINATED));
    }

    private void unregisterReceivers() {
        unregisterReceiver(stopServiceReceiver);
        unregisterReceiver(listChangedReceiver);
        unregisterReceiver(adIsTerminatedReceiver);
    }

    private Notification createStickyNotification() {
//        NotificationManager manager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.stop_service))
                .setContentIntent(PendingIntent.getBroadcast(this, 0, new Intent(STOP_SERVICE), PendingIntent.FLAG_UPDATE_CURRENT))
                .setWhen(0)
                .build();
//        manager.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    private void removeNotification() {
        NotificationManager manager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        manager.cancel(NOTIFICATION_ID);
    }
}

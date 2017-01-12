package ads.mobile.acp2demo.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;

import com.rvalerio.fgchecker.AppChecker;

import java.util.ArrayList;
import java.util.Arrays;

import ads.mobile.acp2demo.R;
import ads.mobile.acp2demo.classes.AppsList;


public class AppCheckerService extends Service {

    private final static int NOTIFICATION_ID = 1234;
    private static final String TAG = AppCheckerService.class.getSimpleName();
    private final static String STOP_SERVICE = AppCheckerService.class.getPackage()+".stop";
    public final static String SEND_LIST_CHANGED = "listChanged";
    private ArrayList<String> selctedApps;

    private BroadcastReceiver stopServiceReceiver;
    private BroadcastReceiver listChangedReceiver;

    private AppChecker appChecker;
    private boolean adIsTriggered = false;
    private boolean appListIsChanged = true;

    public static void start(Context context) {
        context.startService(new Intent(context, AppCheckerService.class));
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, AppCheckerService.class));
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
        createStickyNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopChecker();
        removeNotification();
        unregisterReceivers();
        stopSelf();
    }

    private void startChecker() {
        appChecker = new AppChecker();
        appChecker
                .other(new AppChecker.Listener() {
                    @Override
                    public void onForeground(String packageName) {
                        if(appListIsChanged)
                        {
                            Log.d(TAG, "Fetching new list...");
                            AppsList tmp = AppsList.load(getApplicationContext());
                            selctedApps = tmp.getSelectedAppPackageNames();
                            Log.d(TAG, "selctedApps: " + Arrays.toString(selctedApps.toArray()));
                            Log.d(TAG, "Current Foreground app: " + packageName +  " isSelected: "
                            + Boolean.toString(selctedApps.contains(packageName)) );
                            appListIsChanged = false;
                        }

                        if(selctedApps.contains(packageName) && !adIsTriggered)
                        {
                            Log.d(TAG, "ad is triggered.");
                            adIsTriggered = true;
                            showAd();
                        }
                    }
                })
                .timeout(5000)
                .start(this);
    }

    private void showAd()
    {
    }

    private void stopChecker() {
        appChecker.stop();
    }

    private void registerReceivers() {
        stopServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("APP_CHECKER_SERVICE", "is stopping.");
                stopSelf();
            }
        };
        listChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("LIST_CHANGED_RECEIVER", "LIST HAS CHANGED.");
                appListIsChanged = true;
            }
        };
        registerReceiver(stopServiceReceiver, new IntentFilter(STOP_SERVICE));
        registerReceiver(listChangedReceiver, new IntentFilter(SEND_LIST_CHANGED));
    }

    private void unregisterReceivers() {
        unregisterReceiver(stopServiceReceiver);
        unregisterReceiver(listChangedReceiver);
    }

    private Notification createStickyNotification() {
        NotificationManager manager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
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
        manager.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    private void removeNotification() {
        NotificationManager manager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        manager.cancel(NOTIFICATION_ID);
    }
}

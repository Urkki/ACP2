package ads.mobile.acp2demo.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Urkki on 22.1.2017.
 */

public final class UniqueIdManager {
    private static final String TAG = UniqueIdManager.class.getSimpleName();
    private static String uuid;

    public UniqueIdManager(Activity a) {

        //Save uuid into persistent memory.
        SharedPreferences sharedPref = a.getPreferences(Context.MODE_PRIVATE);
        if( !sharedPref.contains("uuid") ) {
            SharedPreferences.Editor editor = sharedPref.edit();
            uuid = createUuid(a);
            editor.putString("uuid", uuid);
            editor.apply();
            Log.d(TAG, "Uuid created: " + uuid);
        }
        else { // read uuid
            uuid = sharedPref.getString("uuid", null);
            Log.d(TAG, "Uuid loaded: " + uuid);
        }
    }


    private String createUuid(Activity a)
    {
        final TelephonyManager tm = (TelephonyManager) a.getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(a.getBaseContext()
                .getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    public static String getUuid() {
        return uuid;
    }
}

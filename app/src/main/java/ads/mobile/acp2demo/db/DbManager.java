package ads.mobile.acp2demo.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.aware.Aware;
import com.aware.Aware_Preferences;

import ads.mobile.acp2demo.Provider;

import static android.R.attr.action;
import static android.R.attr.value;
import static android.R.attr.width;
import static android.R.attr.x;
import static android.R.attr.y;
import static com.aware.Aware.getSetting;

public final class DbManager {

    private static String TAG =  DbManager.class.getSimpleName();

//    private static String device_id = null;
    private DbManager() {}

    public static void insertDeviceInfoRow (Context c, String user_name)
    {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                Provider.DeviceInfoEntity.DEVICE_ID
        };
        String selection = Provider.DeviceInfoEntity.COLUMN_NAME_USER_NAME + " = ?";
        String[] selectionArgs = { user_name };

        Cursor cur = c.getContentResolver().query(Provider.DeviceInfoEntity.CONTENT_URI,
                projection,selection,selectionArgs, null);
        //if this row is not in db.
        if (cur != null && cur.getCount() == 0)
        {
            //screen axis
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            Long now = System.currentTimeMillis();
            String device = Aware.getSetting(c, Aware_Preferences.DEVICE_ID);
            //Then insert data to db.
            ContentValues values = new ContentValues();
            values.put(Provider.DeviceInfoEntity.TIMESTAMP, now);
            values.put(Provider.DeviceInfoEntity.DEVICE_ID, device);
            values.put(Provider.DeviceInfoEntity.COLUMN_NAME_USER_NAME, user_name);
            values.put(Provider.DeviceInfoEntity.COLUMN_NAME_SCREEN_WIDTH, width);
            values.put(Provider.DeviceInfoEntity.COLUMN_NAME_SCREEN_HEIGHT, height);
            c.getContentResolver().insert(Provider.DeviceInfoEntity.CONTENT_URI, values);
            Log.d(TAG, "device_info inserted, width: " + String.valueOf(width) +
                    " height: " + String.valueOf(height) + " user_name: " + user_name);
        }
        else {
            cur.close();
        }
//        device_id = getSetting(c, Aware_Preferences.DEVICE_ID);
    }

    public static void insertLocationBulkRow(Context c, ContentValues[] tmp) {
        c.getContentResolver().bulkInsert(Provider.LocationEntry.CONTENT_URI, tmp);
    }

    public static void insertEventRow(Context c, long duration, String event_name, String user_name,
                                      String ad_name, String app_name, String testcase_name
                                      ) {
        ContentValues values = new ContentValues();
        Long now = System.currentTimeMillis();
        values.put(Provider.EventEntry.TIMESTAMP, now);
        values.put(Provider.EventEntry.DEVICE_ID, getSetting(c, Aware_Preferences.DEVICE_ID) );
        values.put(Provider.EventEntry.COLUMN_NAME_DURATION, duration);
        values.put(Provider.EventEntry.COLUMN_NAME_EVENT_NAME, event_name);
        values.put(Provider.EventEntry.COLUMN_NAME_USER_NAME, user_name );
        values.put(Provider.EventEntry.COLUMN_NAME_CURRENT_AD_NAME, ad_name);
        values.put(Provider.EventEntry.COLUMN_NAME_CURRENT_APP_NAME, app_name);
        values.put(Provider.EventEntry.COLUMN_NAME_TEST_CASE_NAME, testcase_name);
        c.getContentResolver().insert(Provider.EventEntry.CONTENT_URI, values);
        Log.d(TAG, "eventRow inserted. " +  values.toString());
    }

    public static ContentValues createLocationContentValue( String action, int x, int y, String device_id  ) {
        ContentValues cont = new ContentValues();
        Long now = System.currentTimeMillis();
        cont.put(Provider.LocationEntry.COLUMN_NAME_USER_NAME, "asdf");
        cont.put(Provider.LocationEntry.DEVICE_ID, device_id);
        cont.put(Provider.LocationEntry.TIMESTAMP, now);
        cont.put(Provider.LocationEntry.COLUMN_NAME_ELEMENT_NAME, "element_name");
        cont.put(Provider.LocationEntry.COLUMN_NAME_ACTION, action);
        cont.put(Provider.LocationEntry.COLUMN_NAME_CURRENT_APP_NAME, "asdf");
        cont.put(Provider.LocationEntry.COLUMN_NAME_TEST_CASE_NAME, "asdf");
        cont.put(Provider.LocationEntry.COLUMN_NAME_X, x);
        cont.put(Provider.LocationEntry.COLUMN_NAME_Y, y);
        return cont;
    }
}
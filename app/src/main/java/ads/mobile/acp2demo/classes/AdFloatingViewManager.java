package ads.mobile.acp2demo.classes;

import android.content.ContentValues;
import android.content.Context;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.providers.Aware_Provider;

import java.util.ArrayList;
import java.util.List;

import ads.mobile.acp2demo.db.tables.LocationEntry;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static android.R.attr.x;
import static android.R.attr.y;
import static android.content.ContentValues.TAG;

/**
 * Created by Urkki on 18.1.2017.
 */

public class AdFloatingViewManager extends FloatingViewManager {
    private long prevTime = 0;
    private static long HALF_SEC_IN_MILLIS = 500;
    private Context c;
    ArrayList<ContentValues> cache = new ArrayList<>(10);
    private String device_id;
    public AdFloatingViewManager(Context context, FloatingViewListener listener) {
        super(context, listener);
        c = context;
        device_id = Aware.getSetting(c, Aware_Preferences.DEVICE_ID);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        long time = System.currentTimeMillis();
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // touched down
                //Delete cache
                cache.clear();
                Log.d(TAG, "DOWN");
                //insert data to cache.
                cache.add(createContentValue("DOWN", x, y));
                break;
            case MotionEvent.ACTION_MOVE: // moving
                // Record event every 500 ms (0.5 sec)
                if( (time - prevTime) >= HALF_SEC_IN_MILLIS){
                    prevTime = time;
                    Log.d(TAG, String.valueOf(time) + " x: " + String.valueOf(x) + "y: " + String.valueOf(y) );
                    cache.add(createContentValue("MOVING", x, y));
                }
                break;
            case MotionEvent.ACTION_UP: // touch is released
                Log.d(TAG,"UP");
                cache.add(createContentValue("UP", x, y));
                //insert data to db.
                ContentValues[] tmp = cache.toArray(new ContentValues[cache.size()]);
                c.getContentResolver().bulkInsert(LocationEntry.CONTENT_URI, tmp);
                break;
        }


        return true;
    }

    private ContentValues createContentValue( String action, int x, int y  ) {
        ContentValues cont = new ContentValues();
        Long now = System.currentTimeMillis();
        cont.put(LocationEntry.COLUMN_NAME_USER_NAME, "asdf");
        cont.put(LocationEntry.DEVICE_ID, device_id);
        cont.put(LocationEntry.TIMESTAMP, now.toString());
        cont.put(LocationEntry.COLUMN_NAME_ELEMENT_NAME, "element_name");
        cont.put(LocationEntry.COLUMN_NAME_ACTION, action);
        cont.put(LocationEntry.COLUMN_NAME_CURRENT_APP_NAME, "asdf");
        cont.put(LocationEntry.COLUMN_NAME_TEST_CASE_NAME, "asdf");
        cont.put(LocationEntry.COLUMN_NAME_X, x);
        cont.put(LocationEntry.COLUMN_NAME_Y, y);
        return cont;
    }
}

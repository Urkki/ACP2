package ads.mobile.acp2demo.classes;

import android.content.ContentValues;
import android.content.Context;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.aware.Aware;
import com.aware.Aware_Preferences;


import java.util.ArrayList;


import ads.mobile.acp2demo.Provider;
import ads.mobile.acp2demo.db.DbManager;
import ads.mobile.acp2demo.services.AdViewService;
import ads.mobile.acp2demo.services.AppCheckerService;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static ads.mobile.acp2demo.db.DbManager.createLocationContentValue;
import static android.content.ContentValues.TAG;

/**
 * Created by Urkki on 18.1.2017.
 */

public class AdFloatingViewManager extends FloatingViewManager {
    private long prevTime = 0;
    private static long HALF_SEC_IN_MILLIS = 500;
    private Context c;
    private AdViewService _adService;
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
                //Get touched time
                long now = System.currentTimeMillis();
                long adShowTime = AppCheckerService.getAdTriggerTime();
                long duration = now - adShowTime;
                DbManager.insertEventRow(c, duration, "AdIsTouched", "MyUserName", "MyAdName", "ForeGroundAppName", "TestCaseName");
                //Delete cache
                cache.clear();
                Log.d(TAG, "DOWN and eventrow inserted.");
                //insert data to cache.
                cache.add(createLocationContentValue("DOWN", x, y, device_id));
                break;
            case MotionEvent.ACTION_MOVE: // moving
                // Record event every 500 ms (0.5 sec)
                if( (time - prevTime) >= HALF_SEC_IN_MILLIS){
                    prevTime = time;
                    Log.d(TAG, String.valueOf(time) + " x: " + String.valueOf(x) + "y: " + String.valueOf(y) );
                    cache.add(createLocationContentValue("MOVING", x, y, device_id));
                }
                break;
            case MotionEvent.ACTION_UP: // touch is released
                Log.d(TAG,"UP");
                cache.add(createLocationContentValue("UP", x, y, device_id));
                //insert data to db.
                ContentValues[] tmp = cache.toArray(new ContentValues[cache.size()]);
                DbManager.insertLocationBulkRow(c, tmp);
                break;
        }
        return true;
    }
}

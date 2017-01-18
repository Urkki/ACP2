package ads.mobile.acp2demo.classes;

import android.content.Context;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;



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

    public AdFloatingViewManager(Context context, FloatingViewListener listener) {
        super(context, listener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        long time = System.currentTimeMillis();
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // touched down
                Log.d(TAG, "DOWN");
                break;
            case MotionEvent.ACTION_MOVE: // moving
                // Record event every 500 ms (0.5 sec)
                if( (time - prevTime) >= HALF_SEC_IN_MILLIS){
                    prevTime = time;
                    Log.d(TAG, String.valueOf(time) + " x: " + String.valueOf(x) + "y: " + String.valueOf(y) );
                }
                break;
            case MotionEvent.ACTION_UP: // touch is released
                Log.d(TAG,"UP");
                break;
        }


        return true;
    }
}

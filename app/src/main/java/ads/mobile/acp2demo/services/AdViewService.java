package ads.mobile.acp2demo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;


import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ads.mobile.acp2demo.R;


import ads.mobile.acp2demo.activities.AdDialogActivity;
import ads.mobile.acp2demo.activities.MainActivity;
import ads.mobile.acp2demo.classes.AdFloatingViewManager;
import ads.mobile.acp2demo.db.DbManager;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;


import static ads.mobile.acp2demo.Provider.EventEntry.SMALL_AD_DELETED_BY_USER;
import static ads.mobile.acp2demo.Provider.EventEntry.SMALL_AD_IS_CREATED;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_AD_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_CURRENT_FOREGROUD_APP_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_CURRENT_TESTCASE_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.PREF_USER_NAME;


/**
 * FloatingViewのカスタマイズを行います。
 * サンプルとしてクリック時にはメールアプリを起動します。
 */
public class AdViewService extends Service implements FloatingViewListener {
    private int adCounter = 0;
    int[] bannerAdArray = {R.drawable.asos_banneri, R.drawable.citymarket_banneri, R.drawable.finnmari_banneri, R.drawable.junkyard_banneri, R.drawable.kaleva_banneri};
    int[] bannerAdArray2 = {R.drawable.mcdonalds_banneri, R.drawable.norwegian_banneri, R.drawable.rooster_banneri, R.drawable.sokos_banneri, R.drawable.talouselama_banneri};
    int[] iconAdArray = {R.drawable.bk_icon, R.drawable.ellos_icon, R.drawable.hese_icon, R.drawable.intersport_icon, R.drawable.matkapojat_icon};
    int[] iconAdArray2 = {R.drawable.prisma_icon, R.drawable.qstock_icon, R.drawable.robertscoffee_icon, R.drawable.spiceice_icon, R.drawable.subway_icon};

    //Dict for choosing selected array.
    Map<String, int[]> adArrayChooser = new HashMap<String, int[]>() {{
        put("banner", bannerAdArray);
        put("icon", iconAdArray);
        put("banner2", bannerAdArray2);
        put("icon2", iconAdArray2);
    }};

    int[] currentAdArray = iconAdArray;
    /**
     * デバッグログ用のタグ
     */
    private static final String TAG = "AdViewService";

    /**
     * 通知ID
     */
    private static final int NOTIFICATION_ID = 908114;

    /**
     * CustomFloatingViewServiceBinder
     */
    private IBinder mCustomFloatingViewServiceBinder;

    /**
     * FloatingViewManager
     */
    private FloatingViewManager mFloatingViewManager;

    /**
     * {@inheritDoc}
     */
    private static SharedPreferences pref;

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        // 既にManagerが存在していたら何もしない
        if (mFloatingViewManager != null) {
            return START_STICKY;
        }
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Change correct array.
        String s = pref.getString(MainActivity.PREF_CURRENT_IMG_ARRAY_NAME, "icon"); //TODO: FIXME:
        currentAdArray = adArrayChooser.get(s);

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mCustomFloatingViewServiceBinder = new CustomFloatingViewServiceBinder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        ImageView adimageButton = (ImageView) inflater.inflate(R.layout.widget_ad, null, false);
        //Reads adCounter value from bundle and changes ad icon
        final Bundle bundle = intent.getExtras();
        adCounter = bundle.getInt("adCounter");

        String img_name = getResources().getResourceEntryName(currentAdArray[adCounter]);
        if (img_name.contains("icon"))
        {
            //Get bid ad resource id.
            String p = img_name.replace("_icon","") +  "_big_ad";
            int img_res_id = getId(p, R.drawable.class);
            bundle.putInt("img_res_id", img_res_id);
        }

        boolean allowAdClickListener = bundle.getBoolean("enableClickListener", true);
        adimageButton.setImageResource(currentAdArray[adCounter]);
        //Set listener
        if(allowAdClickListener) {
            adimageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // メールアプリの起動
                    final Intent intent = new Intent(getApplicationContext(), AdDialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    stopSelf();
                }
            });
        }


        //update image name
        pref.edit().putString(MainActivity.PREF_AD_NAME, img_name).commit();
        //Add event to the db.
        DbManager.insertEventRow(getApplicationContext(), 0, SMALL_AD_IS_CREATED,
                pref.getString(PREF_USER_NAME, ""),
                pref.getString(PREF_AD_NAME, ""),
                pref.getString(PREF_CURRENT_FOREGROUD_APP_NAME, ""),
                pref.getString(PREF_CURRENT_TESTCASE_NAME, "") );

        mFloatingViewManager = new AdFloatingViewManager(this, this);

        //set trash if we allow dragging
        if(allowAdClickListener){
            mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
            mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
            mFloatingViewManager.setTrashViewEnabled(true);
        }


        // Setting Options(you can change options at any time)
        loadDynamicOptions();
        // Initial Setting Options (you can't change options after created.)
        final FloatingViewManager.Options options = loadOptions(metrics);

        mFloatingViewManager.addViewToWindow(adimageButton, options);
        // TODO: FIXME Disable dragging NOT WORKING!!
        if(!allowAdClickListener){
            mFloatingViewManager.setTrashViewEnabled(false);
        }
        return START_REDELIVER_INTENT;
    }


    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        destroy();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mCustomFloatingViewServiceBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onFinishFloatingView() {
        //send message to appchecker that the ad is ended.
        Intent i = new Intent(AppCheckerService.AD_TERMINATED);
        i.putExtra("reason", "dno");
        sendBroadcast(i);
        Log.d(TAG, "Ad STOP broadcast is sended.");
        // small ad deletion time - small ad triggertime.
        long dur = System.currentTimeMillis() - AppCheckerService.getAdTriggerTime();
        DbManager.insertEventRow(getApplicationContext(), dur, SMALL_AD_DELETED_BY_USER,
                pref.getString(PREF_USER_NAME, ""),
                pref.getString(PREF_AD_NAME, ""),
                pref.getString(PREF_CURRENT_FOREGROUD_APP_NAME, ""),
                pref.getString(PREF_CURRENT_TESTCASE_NAME, ""));
        onDestroy();
        stopSelf();
    }

    /**
     * Viewを破棄します。
     */
    private void destroy() {
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }
    }


    /**
     * 動的に変更可能なオプションを読み込みます。
     */
    private void loadDynamicOptions() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        final String displayModeSettings = sharedPref.getString("settings_display_mode", "");
        if ("Always".equals(displayModeSettings)) {
            mFloatingViewManager.setDisplayMode(FloatingViewManager.DISPLAY_MODE_SHOW_ALWAYS);
        } else if ("FullScreen".equals(displayModeSettings)) {
            mFloatingViewManager.setDisplayMode(FloatingViewManager.DISPLAY_MODE_HIDE_FULLSCREEN);
        } else if ("Hide".equals(displayModeSettings)) {
            mFloatingViewManager.setDisplayMode(FloatingViewManager.DISPLAY_MODE_HIDE_ALWAYS);
        }

    }

    /**
     * FloatingViewのオプションを読み込みます。
     *
     * @param metrics X/Y座標の設定に利用するDisplayMetrics
     * @return Options
     */
    private FloatingViewManager.Options loadOptions(DisplayMetrics metrics) {
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Shape
        final String shapeSettings = sharedPref.getString("settings_shape", "");
        if ("Circle".equals(shapeSettings)) {
            options.shape = FloatingViewManager.SHAPE_CIRCLE;
        } else if ("Rectangle".equals(shapeSettings)) {
            options.shape = FloatingViewManager.SHAPE_RECTANGLE;
        }

        // Margin
        final String marginSettings = sharedPref.getString("settings_margin", String.valueOf(options.overMargin));
        options.overMargin = Integer.parseInt(marginSettings);

        // MoveDirection
        final String moveDirectionSettings = sharedPref.getString("settings_move_direction", "");
        if ("Default".equals(moveDirectionSettings)) {
            options.moveDirection = FloatingViewManager.MOVE_DIRECTION_DEFAULT;
        } else if ("Left".equals(moveDirectionSettings)) {
            options.moveDirection = FloatingViewManager.MOVE_DIRECTION_LEFT;
        } else if ("Right".equals(moveDirectionSettings)) {
            options.moveDirection = FloatingViewManager.MOVE_DIRECTION_RIGHT;
        } else if ("Fix".equals(moveDirectionSettings)) {
            options.moveDirection = FloatingViewManager.MOVE_DIRECTION_NONE;
        }

        // Init X/Y
        final String initXSettings = sharedPref.getString("settings_init_x", "");
        final String initYSettings = sharedPref.getString("settings_init_y", "");
        if (!TextUtils.isEmpty(initXSettings) && !TextUtils.isEmpty(initYSettings)) {
            final int offset = (int) (48 + 8 * metrics.density);
            options.floatingViewX = (int) (metrics.widthPixels * Float.parseFloat(initXSettings) - offset);
            options.floatingViewY = (int) (metrics.heightPixels * Float.parseFloat(initYSettings) - offset);
        }

        // Initial Animation
        final boolean animationSettings = sharedPref.getBoolean("settings_animation", options.animateInitialMove);
        options.animateInitialMove = animationSettings;

        return options;
    }

    /**
     * CustomFloatingServiceのBinderです。
     */
    public static class CustomFloatingViewServiceBinder extends Binder {

        /**
         * AdViewService
         */
        private final WeakReference<AdViewService> mService;

        /**
         * コンストラクタ
         *
         * @param service AdViewService
         */
        CustomFloatingViewServiceBinder(AdViewService service) {
            mService = new WeakReference<>(service);
        }

        /**
         * CustomFloatingViewServiceを取得します。
         *
         * @return AdViewService
         */
        public AdViewService getService() {
            return mService.get();
        }
    }

}

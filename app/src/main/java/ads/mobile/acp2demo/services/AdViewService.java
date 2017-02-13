package ads.mobile.acp2demo.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


import java.lang.ref.WeakReference;

import ads.mobile.acp2demo.R;


import ads.mobile.acp2demo.activities.AdDialogActivity;
import ads.mobile.acp2demo.classes.AdFloatingViewManager;
import ads.mobile.acp2demo.db.DbManager;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

import static ads.mobile.acp2demo.Provider.EventEntry.SMALL_AD_DELETED_BY_USER;
import static ads.mobile.acp2demo.activities.MainActivity.AD_NAME_PREF;
import static ads.mobile.acp2demo.activities.MainActivity.CURRENT_FOREGROUD_APP_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.CURRENT_TESTCASE_NAME;
import static ads.mobile.acp2demo.activities.MainActivity.USER_NAME_PREF;


/**
 * FloatingViewのカスタマイズを行います。
 * サンプルとしてクリック時にはメールアプリを起動します。
 */
public class AdViewService extends Service implements FloatingViewListener {
    private int adCounter = 0;
    int[] adArray = {R.drawable.ic_ad1, R.drawable.ic_ad2, R.drawable.ic_ad3, R.drawable.ic_ad4};
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

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mCustomFloatingViewServiceBinder = new CustomFloatingViewServiceBinder(this);
        final LayoutInflater inflater = LayoutInflater.from(this);
        ImageView adimageButton = (ImageView) inflater.inflate(R.layout.widget_ad, null, false);
        //Reads adCounter value from bundle and changes ad icon
        final Bundle bundle = intent.getExtras();
        adCounter = bundle.getInt("adCounter");
        adimageButton.setImageResource(adArray[adCounter]);

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

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mFloatingViewManager = new AdFloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_trash_fixed);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        mFloatingViewManager.setTrashViewEnabled(true);
        // Setting Options(you can change options at any time)
        loadDynamicOptions();
        // Initial Setting Options (you can't change options after created.)
        final FloatingViewManager.Options options = loadOptions(metrics);
        mFloatingViewManager.addViewToWindow(adimageButton, options);
        return START_REDELIVER_INTENT;
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
                pref.getString(USER_NAME_PREF, ""),
                pref.getString(AD_NAME_PREF, ""),
                pref.getString(CURRENT_FOREGROUD_APP_NAME, ""),
                pref.getString(CURRENT_TESTCASE_NAME, ""));
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

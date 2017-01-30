package ads.mobile.acp2demo.db.tables;

import android.net.Uri;
import android.provider.BaseColumns;

import ads.mobile.acp2demo.Plugin;

import static ads.mobile.acp2demo.Provider.AUTHORITY;


/**
 * Created by Urkki on 22.1.2017.
 */

public final class EventEntry implements BaseColumns {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private EventEntry(){}

    public static final String TABLE_NAME = "events";
    //Aware spesific columns
    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String DEVICE_ID = "device_id";
    //Our columns
    public static final String COLUMN_NAME_DURATION = "duration";
    public static final String COLUMN_NAME_EVENT_NAME = "event_name";
    public static final String COLUMN_NAME_USER_NAME = "user_name";
    public static final String COLUMN_NAME_CURRENT_AD_NAME = "current_ad_name";
    public static final String COLUMN_NAME_CURRENT_APP_NAME = "current_app_name";
    public static final String COLUMN_NAME_TEST_CASE_NAME= "test_case_name";

    /**
     * Your ContentProvider table content URI.<br/>
     * The last segment needs to match your database table name
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + EventEntry.TABLE_NAME);
    /**
     * How your data collection is identified internally in Android (vnd.android.cursor.dir). <br/>
     * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + Plugin.NAME + "." + TABLE_NAME; //TODO: RENAME
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + Plugin.NAME + "." + TABLE_NAME; //TODO: RENAME

    //EventTable
    public static final String TABLE_FIELDS = _ID + " integer primary key autoincrement," +
            DEVICE_ID + " text default 'unknown'," +
            TIMESTAMP + " real default 0," +
            COLUMN_NAME_DURATION + " integer default -1," +
            COLUMN_NAME_EVENT_NAME + " text default 'unknown'," +
            COLUMN_NAME_USER_NAME + " text default 'unknown'," +
            COLUMN_NAME_CURRENT_AD_NAME + " text default 'unknown'," +
            COLUMN_NAME_CURRENT_APP_NAME + " text default 'unknown'," +
            COLUMN_NAME_TEST_CASE_NAME + " text default 'unknown'";

}

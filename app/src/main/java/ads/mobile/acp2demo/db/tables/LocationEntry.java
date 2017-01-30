package ads.mobile.acp2demo.db.tables;

import android.content.ContentProvider;
import android.net.Uri;
import android.provider.BaseColumns;

import ads.mobile.acp2demo.aware_plugin.Plugin;

import static ads.mobile.acp2demo.aware_plugin.Provider.AUTHORITY;


/**
 * Created by Urkki on 22.1.2017.
 */

public final class LocationEntry implements BaseColumns {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LocationEntry(){}

    public static final String TABLE_NAME = "locations";
    //Aware spesific columns
    public static final String _ID = "_id";
    public static final String TIMESTAMP = "timestamp";
    public static final String DEVICE_ID = "device_id";
    //Our columns
    public static final String COLUMN_NAME_ELEMENT_NAME = "element";
    public static final String COLUMN_NAME_USER_NAME = "user_name";
    public static final String COLUMN_NAME_CURRENT_APP_NAME = "current_app_name";
    public static final String COLUMN_NAME_TEST_CASE_NAME= "test_case_name";
    public static final String COLUMN_NAME_ACTION = "action";
    public static final String COLUMN_NAME_X = "x";
    public static final String COLUMN_NAME_Y = "y";

    /**
     * Your ContentProvider table content URI.<br/>
     * The last segment needs to match your database table name
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LocationEntry.TABLE_NAME);
    /**
     * How your data collection is identified internally in Android (vnd.android.cursor.dir). <br/>
     * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + Plugin.NAME + "." + TABLE_NAME; //TODO: RENAME
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + Plugin.NAME + "." + TABLE_NAME; //TODO: RENAME

    //LocationTable
    public static final String TABLE_FIELDS = LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    LocationEntry.DEVICE_ID + " TEXT, " +
    LocationEntry.TIMESTAMP + " TEXT, " +
    LocationEntry.COLUMN_NAME_ELEMENT_NAME + " TEXT, " +
    LocationEntry.COLUMN_NAME_ACTION + " TEXT, " +
    LocationEntry.COLUMN_NAME_USER_NAME + " TEXT, " +
    LocationEntry.COLUMN_NAME_CURRENT_APP_NAME + " TEXT, " +
    LocationEntry.COLUMN_NAME_TEST_CASE_NAME + " TEXT, " +
    LocationEntry.COLUMN_NAME_X + " INTEGER, " +
    LocationEntry.COLUMN_NAME_Y + " INTEGER ";

}

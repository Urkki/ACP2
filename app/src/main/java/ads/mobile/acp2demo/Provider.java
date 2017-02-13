package ads.mobile.acp2demo;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.util.HashMap;


/**
 * Created by Urkki on 23.1.2017.
 */

public class Provider extends ContentProvider {
    public static String AUTHORITY = "ads.mobile.acp2demo.provider.ad_data";
    /**
     * ContentProvider database version. Increment every time you modify the database structure
     */
    public static final int DATABASE_VERSION = 6;
    /**
     * Database stored in storage as: plugin_example.db
     */
    public static final String DATABASE_NAME = "plugin_ad_data.db";

    public static final String DB_LOCATION_TABLE_NAME = "locations";
    public static final String DB_EVENT_TABLE_NAME = "events";
    public static final String DB_DEVICE_INFO_TABLE_NAME = "device_info";

    public static final String[] DATABASE_TABLES = {
            DB_LOCATION_TABLE_NAME,
            DB_EVENT_TABLE_NAME,
            DB_DEVICE_INFO_TABLE_NAME};

	private static String TAG = Provider.class.getSimpleName();
    //ContentProvider query indexes
    private static final int LOCATION_ENTRY = 1;
    private static final int LOCATION_ENTRY_ID = 2;
    private static final int EVENT_ENTRY = 3;
    private static final int EVENT_ENTRY_ID = 4;
    private static final int DEVICE_ENTRY = 5;
    private static final int DEVICE_ENTRY_ID = 6;



    //These are columns that we need to sync data, don't change this!
    public interface AWAREColumns extends BaseColumns {
        String _ID = "_id";
        String TIMESTAMP = "timestamp";
        String DEVICE_ID = "device_id";
    }

    public static final class LocationEntry implements AWAREColumns {

        /**
         * Your ContentProvider table content URI.<br/>
         * The last segment needs to match your database table name
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_LOCATION_TABLE_NAME);
        /**
         * How your data collection is identified internally in Android (vnd.android.cursor.dir). <br/>
         * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + Plugin.NAME + "." + DB_LOCATION_TABLE_NAME; //TODO: RENAME
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + Plugin.NAME + "." + DB_LOCATION_TABLE_NAME; //TODO: RENAME

        //Our columns
        public static final String COLUMN_NAME_ELEMENT_NAME = "element";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_CURRENT_APP_NAME = "current_app_name";
        public static final String COLUMN_NAME_TEST_CASE_NAME= "test_case_name";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_X = "x";
        public static final String COLUMN_NAME_Y = "y";

    }

    //LocationTable
    public static final String DB_LOCATION_TABLE_NAME_FIELDS =
            LocationEntry._ID + " integer primary key autoincrement," +
                    LocationEntry.TIMESTAMP + " real default 0," +
                    LocationEntry.DEVICE_ID + " text default ''," +
                    LocationEntry.COLUMN_NAME_ELEMENT_NAME + " text default ''," +
                    LocationEntry.COLUMN_NAME_ACTION + " text default ''," +
                    LocationEntry.COLUMN_NAME_USER_NAME + " text default ''," +
                    LocationEntry.COLUMN_NAME_CURRENT_APP_NAME + " text default ''," +
                    LocationEntry.COLUMN_NAME_TEST_CASE_NAME + " text default ''," +
                    LocationEntry.COLUMN_NAME_X + " integer default -1," +
                    LocationEntry.COLUMN_NAME_Y + " integer default -1";

    public static final class DeviceInfoEntity implements AWAREColumns {

        /**
         * Your ContentProvider table content URI.<br/>
         * The last segment needs to match your database table name
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_DEVICE_INFO_TABLE_NAME);
        /**
         * How your data collection is identified internally in Android (vnd.android.cursor.dir). <br/>
         * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + Plugin.NAME + "." + DB_DEVICE_INFO_TABLE_NAME; //TODO: RENAME
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + Plugin.NAME + "." + DB_DEVICE_INFO_TABLE_NAME; //TODO: RENAME

        //Our columns
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_SCREEN_WIDTH = "screen_width";
        public static final String COLUMN_NAME_SCREEN_HEIGHT = "screen_height";

    }
    //DeviceInfoTABLE
    public static final String DB_DEVICE_INFO_TABLE_NAME_FIELDS =
                    DeviceInfoEntity._ID + " integer primary key autoincrement," +
                    DeviceInfoEntity.TIMESTAMP + " real default 0," +
                    DeviceInfoEntity.DEVICE_ID + " text default ''," +
                    DeviceInfoEntity.COLUMN_NAME_USER_NAME + " text default ''," +
                    DeviceInfoEntity.COLUMN_NAME_SCREEN_WIDTH + " integer default -1," +
                    DeviceInfoEntity.COLUMN_NAME_SCREEN_HEIGHT + " integer default -1";


    public static final class EventEntry implements AWAREColumns {
        /**
         * Your ContentProvider table content URI.<br/>
         * The last segment needs to match your database table name
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_EVENT_TABLE_NAME);
        /**
         * How your data collection is identified internally in Android (vnd.android.cursor.dir). <br/>
         * It needs to be /vnd.aware.plugin.XXX where XXX is your plugin name (no spaces!).
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd." + Plugin.NAME + "." + DB_EVENT_TABLE_NAME; //TODO: RENAME
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + Plugin.NAME + "." + DB_EVENT_TABLE_NAME; //TODO: RENAME

        //Our columns
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_EVENT_NAME = "event_name";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_CURRENT_AD_NAME = "current_ad_name";
        public static final String COLUMN_NAME_CURRENT_APP_NAME = "current_app_name";
        public static final String COLUMN_NAME_TEST_CASE_NAME= "test_case_name";

        //small ad
        public static final String SMALL_AD_DELETED_BY_USER = "small_ad_is_deleted_by_user";
        public static final String SMALL_AD_TOUCHED = "small_ad_is_touched";
        public static final String SMALL_AD_IS_CREATED = "small_ad_is_created";
        public static final String SMALL_AD_IS_REMOVED_BY_SYSTEM = "small_ad_is_removed_by_system";

        //big ad
        public static final String BIG_AD_SHOWN = "big_ad_is_shown";
        public static final String BIG_AD_CLOSED = "big_ad_is_closed_by_user";
    }

    //EventTable
    public static final String DB_EVENT_TABLE_NAME_FIELDS =
            EventEntry._ID + " integer primary key autoincrement," +
            EventEntry.TIMESTAMP + " real default 0," +
            EventEntry.DEVICE_ID + " text default ''," +
            EventEntry.COLUMN_NAME_DURATION + " real default -1," +
            EventEntry.COLUMN_NAME_EVENT_NAME + " text default ''," +
            EventEntry.COLUMN_NAME_USER_NAME + " text default ''," +
            EventEntry.COLUMN_NAME_CURRENT_AD_NAME + " text default ''," +
            EventEntry.COLUMN_NAME_CURRENT_APP_NAME + " text default ''," +
            EventEntry.COLUMN_NAME_TEST_CASE_NAME + " text default ''";


    public static final String[] TABLES_FIELDS = {
            DB_LOCATION_TABLE_NAME_FIELDS,
            DB_EVENT_TABLE_NAME_FIELDS,
            DB_DEVICE_INFO_TABLE_NAME_FIELDS
    };


    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> tableMapLocation = null;
    private static HashMap<String, String> tableMapEvent = null;
    private static HashMap<String, String> tableMapDevice = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;

    /**
     * Initialise the ContentProvider
     */
    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS );
        }
        if( databaseHelper != null && ( database == null || ! database.isOpen()) ) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }
    /**
     * Allow resetting the ContentProvider when updating/reinstalling AWARE
     */
//    public static void resetDB( Context c ) {
//        Log.d("AWARE", "Resetting " + DATABASE_NAME + "...");
//
//        File db = new File(DATABASE_NAME);
//        db.delete();
//        databaseHelper = new DatabaseHelper( c, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
//        if( databaseHelper != null ) {
//            database = databaseHelper.getWritableDatabase();
//        }
//    }

    @Override
    public boolean onCreate() {
        //This is a hack to allow providers to be reusable in any application/plugin by making the authority dynamic using the package name of the parent app
        AUTHORITY = getContext().getPackageName() + ".provider.ad_data"; //make AUTHORITY dynamic
        Log.i("AUTHORITY", "auth: " + AUTHORITY);
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //location
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], LOCATION_ENTRY); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", LOCATION_ENTRY_ID); //URI for a single record
        //event
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], EVENT_ENTRY); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1]+"/#", EVENT_ENTRY_ID); //URI for a single record
        //device
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[2], DEVICE_ENTRY); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[2]+"/#", DEVICE_ENTRY_ID); //URI for a single record

        tableMapLocation = new HashMap<String, String>();
        tableMapLocation.put(LocationEntry._ID, LocationEntry._ID);
        tableMapLocation.put(LocationEntry.TIMESTAMP, LocationEntry.TIMESTAMP);
        tableMapLocation.put(LocationEntry.DEVICE_ID, LocationEntry.DEVICE_ID);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_ACTION, LocationEntry.COLUMN_NAME_ACTION);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_ELEMENT_NAME, LocationEntry.COLUMN_NAME_ELEMENT_NAME);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_CURRENT_APP_NAME, LocationEntry.COLUMN_NAME_CURRENT_APP_NAME);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_TEST_CASE_NAME, LocationEntry.COLUMN_NAME_TEST_CASE_NAME);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_USER_NAME, LocationEntry.COLUMN_NAME_USER_NAME);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_X, LocationEntry.COLUMN_NAME_X);
        tableMapLocation.put(LocationEntry.COLUMN_NAME_Y, LocationEntry.COLUMN_NAME_Y);

        tableMapEvent = new HashMap<String, String>();
        tableMapEvent.put(EventEntry._ID, EventEntry._ID);
        tableMapEvent.put(EventEntry.TIMESTAMP, EventEntry.TIMESTAMP);
        tableMapEvent.put(EventEntry.DEVICE_ID, EventEntry.DEVICE_ID);
        tableMapEvent.put(EventEntry.COLUMN_NAME_DURATION, EventEntry.COLUMN_NAME_DURATION);
        tableMapEvent.put(EventEntry.COLUMN_NAME_EVENT_NAME, EventEntry.COLUMN_NAME_EVENT_NAME);
        tableMapEvent.put(EventEntry.COLUMN_NAME_USER_NAME, EventEntry.COLUMN_NAME_USER_NAME);
        tableMapEvent.put(EventEntry.COLUMN_NAME_CURRENT_AD_NAME, EventEntry.COLUMN_NAME_CURRENT_AD_NAME);
        tableMapEvent.put(EventEntry.COLUMN_NAME_CURRENT_APP_NAME, EventEntry.COLUMN_NAME_CURRENT_APP_NAME);
        tableMapEvent.put(EventEntry.COLUMN_NAME_TEST_CASE_NAME, EventEntry.COLUMN_NAME_TEST_CASE_NAME);

        tableMapDevice = new HashMap<String, String>();
        tableMapDevice.put(DeviceInfoEntity._ID, DeviceInfoEntity._ID);
        tableMapDevice.put(DeviceInfoEntity.TIMESTAMP, DeviceInfoEntity.TIMESTAMP);
        tableMapDevice.put(DeviceInfoEntity.DEVICE_ID, DeviceInfoEntity.DEVICE_ID);
        tableMapDevice.put(DeviceInfoEntity.COLUMN_NAME_USER_NAME, DeviceInfoEntity.COLUMN_NAME_USER_NAME);
        tableMapDevice.put(DeviceInfoEntity.COLUMN_NAME_SCREEN_WIDTH, DeviceInfoEntity.COLUMN_NAME_SCREEN_WIDTH);
        tableMapDevice.put(DeviceInfoEntity.COLUMN_NAME_SCREEN_HEIGHT, DeviceInfoEntity.COLUMN_NAME_SCREEN_HEIGHT);

        return true; //let Android know that the database is ready to be used.
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            if( ! initializeDB() ) {
                Log.w(TAG,"Database unavailable...");
                return null;
            }

            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            switch (sUriMatcher.match(uri)) {
                case LOCATION_ENTRY:
                    qb.setTables(DATABASE_TABLES[0]);
                    qb.setProjectionMap(tableMapLocation);
                    break;
                case EVENT_ENTRY:
                    qb.setTables(DATABASE_TABLES[1]);
                    qb.setProjectionMap(tableMapEvent);
                    break;
                case DEVICE_ENTRY:
                    qb.setTables(DATABASE_TABLES[2]);
                    qb.setProjectionMap(tableMapDevice);
                    break;
                default:
                    int match = sUriMatcher.match(uri);
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            try {
                Cursor c = qb.query(database, projection, selection, selectionArgs, null, null, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            } catch (IllegalStateException e) {
                if (Aware.DEBUG) Log.e(Aware.TAG, e.getMessage());
                return null;
            }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case LOCATION_ENTRY:
                return LocationEntry.CONTENT_TYPE;
            case LOCATION_ENTRY_ID:
                return LocationEntry.CONTENT_ITEM_TYPE;
            case EVENT_ENTRY:
                return EventEntry.CONTENT_TYPE;
            case EVENT_ENTRY_ID:
                return EventEntry.CONTENT_ITEM_TYPE;
            case DEVICE_ENTRY:
                return DeviceInfoEntity.CONTENT_TYPE;
            case DEVICE_ENTRY_ID:
                return DeviceInfoEntity.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues new_values) {
        if( ! initializeDB() ) {
            Log.w(TAG,"Database unavailable...");
            return null;
        }

        ContentValues values = (new_values != null) ? new ContentValues(new_values) : new ContentValues();
        long _id;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case LOCATION_ENTRY:
                _id = database.insert(DATABASE_TABLES[0],LocationEntry.DEVICE_ID, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(LocationEntry.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case EVENT_ENTRY:
                _id = database.insert(DATABASE_TABLES[1],EventEntry.DEVICE_ID, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(EventEntry.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case DEVICE_ENTRY:
                _id = database.insert(DATABASE_TABLES[2],DeviceInfoEntity.DEVICE_ID, values);
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(DeviceInfoEntity.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(TAG,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case LOCATION_ENTRY:
                count = database.delete(DATABASE_TABLES[0], selection,selectionArgs);
                break;
            case EVENT_ENTRY:
                count = database.delete(DATABASE_TABLES[1], selection,selectionArgs);
                break;
            case DEVICE_ENTRY:
                count = database.delete(DATABASE_TABLES[2], selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if( ! initializeDB() ) {
            Log.w(TAG,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case LOCATION_ENTRY:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;
            case EVENT_ENTRY:
                count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
                break;
            case DEVICE_ENTRY:
                count = database.update(DATABASE_TABLES[2], values, selection, selectionArgs);
                break;
            default:
                database.close();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}

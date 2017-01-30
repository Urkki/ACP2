package ads.mobile.acp2demo.aware_plugin;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.io.File;
import java.util.HashMap;

import ads.mobile.acp2demo.db.tables.EventEntry;
import ads.mobile.acp2demo.db.tables.LocationEntry;

/**
 * Created by Urkki on 23.1.2017.
 */

public class Provider extends ContentProvider {
    private static String TAG = Provider.class.getSimpleName();
    /**
     * Authority of this content provider
     */
    public static String AUTHORITY = "ads.mobile.acp2demo.provider.ad_data";
    /**
     * ContentProvider database version. Increment every time you modify the database structure
     */
    public static final int DATABASE_VERSION = 1;
    /**
     * Database stored in storage as: plugin_example.db
     */
    public static final String DATABASE_NAME = "plugin_ad_data.db";
    //ContentProvider query indexes
    private static final int LOCATION_ENTRY = 1;
    private static final int LOCATION_ENTRY_ID = 2;
    private static final int EVENT_ENTRY = 3;
    private static final int EVENT_ENTRY_ID = 4;
    /**
     * Database tables:<br/>
     * - plugin_example
     */
    public static final String[] DATABASE_TABLES = {LocationEntry.TABLE_NAME,
            EventEntry.TABLE_NAME};

    public static final String[] TABLES_FIELDS = {LocationEntry.TABLE_FIELDS,
            EventEntry.TABLE_FIELDS };

    public Provider() {
    }

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> tableMapLocation = null;
    private static HashMap<String, String> tableMapEvent = null;
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
    public static void resetDB( Context c ) {
        Log.d("AWARE", "Resetting " + DATABASE_NAME + "...");

        File db = new File(DATABASE_NAME);
        db.delete();
        databaseHelper = new DatabaseHelper( c, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if( databaseHelper != null ) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    @Override
    public boolean onCreate() {
        AUTHORITY = getContext().getPackageName() + ".provider.ad_data"; //make AUTHORITY dynamic
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //location
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], LOCATION_ENTRY); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0]+"/#", LOCATION_ENTRY_ID); //URI for a single record
        //event
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], EVENT_ENTRY); //URI for all records
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1]+"/#", EVENT_ENTRY_ID); //URI for a single record

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
                default:
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
        switch (sUriMatcher.match(uri)) {
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
            default:
                database.close();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

//    public void insertImageData(Context c, String element_name, String action, String app_name,
//                                String test_case_name, String user_name, int x, int y) {
//        ContentValues new_data = new ContentValues();
//        new_data.put(LocationEntry.DEVICE_ID, Aware.getSetting(c, Aware_Preferences.DEVICE_ID));
//        new_data.put(LocationEntry.TIMESTAMP, System.currentTimeMillis());
//        new_data.put(LocationEntry.COLUMN_NAME_ELEMENT_NAME, element_name);
//        new_data.put(LocationEntry.COLUMN_NAME_ACTION, action);
//        new_data.put(LocationEntry.COLUMN_NAME_CURRENT_APP_NAME, app_name);
//        new_data.put(LocationEntry.COLUMN_NAME_TEST_CASE_NAME, test_case_name);
//        new_data.put(LocationEntry.COLUMN_NAME_USER_NAME, user_name);
//        new_data.put(LocationEntry.COLUMN_NAME_X, x);
//        new_data.put(LocationEntry.COLUMN_NAME_Y, y);
//        //        Insert the data to the ContentProvider
//        c.getContentResolver().insert(LocationEntry.CONTENT_URI, new_data);
//    }

}

//package ads.mobile.acp2demo.db.helpers;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import ads.mobile.acp2demo.db.tables.LocationEntry;
//
///**
// * Created by Urkki on 22.1.2017.
// */
//public class LocationDbHelper extends SQLiteOpenHelper {
//    // If you change the database schema, you must increment the database version.
//    public static final int DATABASE_VERSION = 1;
//    public static final String DATABASE_NAME = "Location.db";
//    private static SQLiteDatabase mDb;
//    private static final String SQL_CREATE_ENTRIES =
//            "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
//                    LocationEntry._ID + " INTEGER PRIMARY KEY," +
//                    LocationEntry.TIMESTAMP + " TEXT," +
//                    LocationEntry.COLUMN_NAME_ELEMENT_NAME + " TEXT," +
//                    LocationEntry.COLUMN_NAME_ACTION + " TEXT," +
//                    LocationEntry.COLUMN_NAME_X + " INTEGER," +
//                    LocationEntry.COLUMN_NAME_Y + " INTEGER)";
//
//    private static final String SQL_DELETE_ENTRIES =
//            "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;
//
//    public LocationDbHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        mDb = this.getWritableDatabase();
//    }
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(SQL_CREATE_ENTRIES);
//    }
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // This database is only a cache for online data, so its upgrade policy is
//        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);
//    }
//
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        onUpgrade(db, oldVersion, newVersion);
//    }
//
//    public void insertEntity (String uuid, String timestamp,
//                                 String element, String action, int x, int y) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(LocationEntry.COLUMN_NAME_USER_NAME, uuid);
//        contentValues.put(LocationEntry.TIMESTAMP, timestamp);
//        contentValues.put(LocationEntry.COLUMN_NAME_ELEMENT_NAME, element);
//        contentValues.put(LocationEntry.COLUMN_NAME_ACTION, action);
//        contentValues.put(LocationEntry.COLUMN_NAME_X, x);
//        contentValues.put(LocationEntry.COLUMN_NAME_Y, y);
//        if (mDb == null) {
//            mDb = this.getWritableDatabase();
//        }
//        mDb.insert(LocationEntry.TABLE_NAME, null, contentValues);
//    }
//}
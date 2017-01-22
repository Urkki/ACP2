package ads.mobile.acp2demo.db.tables;

import android.provider.BaseColumns;

import ads.mobile.acp2demo.services.AppCheckerService;

/**
 * Created by Urkki on 22.1.2017.
 */

public final class LocationTable {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LocationTable(){}

    /* Inner class that defines the table contents */
    public static class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_NAME_ELEMENT = "element";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_ACTION = "action";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_X = "x";
        public static final String COLUMN_NAME_Y = "y";
    }

}

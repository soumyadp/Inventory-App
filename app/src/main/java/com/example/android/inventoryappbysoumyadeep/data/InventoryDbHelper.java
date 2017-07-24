package com.example.android.inventoryappbysoumyadeep.data;
import com.example.android.inventoryappbysoumyadeep.data.InventoryContract.InventoryEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Soumya on 15-07-2017.
 */

public class InventoryDbHelper  extends SQLiteOpenHelper {

    public static final String TAG = InventoryDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;


    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_INVENTORY = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.NAME + " TEXT NOT NULL, "
                + InventoryEntry.QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.PRICE + " REAL NOT NULL DEFAULT 0.0, "
                + InventoryEntry.IMAGE + " TEXT NOT NULL DEFAULT 'No images', "
                + InventoryEntry.ITEMS_SOLD_COUNT + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.VENDOR + " TEXT NOT NULL DEFAULT 'new' "
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_INVENTORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

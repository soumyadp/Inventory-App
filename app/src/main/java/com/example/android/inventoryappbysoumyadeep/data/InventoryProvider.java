package com.example.android.inventoryappbysoumyadeep.data;
import com.example.android.inventoryappbysoumyadeep.R;
import com.example.android.inventoryappbysoumyadeep.data.InventoryContract.InventoryEntry;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Soumya on 15-07-2017.
 */

public class InventoryProvider extends ContentProvider {

    public static final String TAG = InventoryProvider.class.getSimpleName();


    //URI matcher and Content Provider concepts used was got from the internet
    private static final int INVENTORY_URI_MATCH = 100;
    private static final int INVENTORY_ID_URI_MATCH = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY_URI_MATCH);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID_URI_MATCH);
    }


    private InventoryDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_URI_MATCH:
                cursor = sqLiteDatabase.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case INVENTORY_ID_URI_MATCH:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = sqLiteDatabase.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_URI_MATCH:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID_URI_MATCH:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY_URI_MATCH:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri.toString());
        }


    }

    public Uri insertProduct(Uri uri, ContentValues values) {


        String name = values.getAsString(InventoryEntry.NAME);
        if (name == null) {
            throw new IllegalArgumentException("Invalid name");
        }


        Integer quantity = values.getAsInteger(InventoryEntry.QUANTITY);


        Float price = values.getAsFloat(InventoryEntry.PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Invalid price");
        }



        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        // Insert the new product with the given values
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the inventory content URI
        //Content Resolver code sourced from the internet
        getContext().getContentResolver().notifyChange(uri, null);


        // Return the new URI with the ID (of the newly inserted row) appended at the end
        Log.i(TAG, "Record inserted");
        return ContentUris.withAppendedId(uri, id);
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case INVENTORY_URI_MATCH:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID_URI_MATCH:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delition is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int matchValue = sUriMatcher.match(uri);
        int rowsCount=0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot update NULL values");
        }


        switch (matchValue) {
            case INVENTORY_URI_MATCH:
                rowsCount = database.update(InventoryEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case INVENTORY_ID_URI_MATCH:
                rowsCount = database.update(InventoryEntry.TABLE_NAME,
                        contentValues,
                        InventoryEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }

        return rowsCount;
    }

}


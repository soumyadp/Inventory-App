package com.example.android.inventoryappbysoumyadeep;

import com.bumptech.glide.Glide;
import com.example.android.inventoryappbysoumyadeep.data.InventoryContract.InventoryEntry;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Soumya on 15-07-2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {
    private static final String TAG = InventoryCursorAdapter.class.getSimpleName();


    protected InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView product_name = (TextView) view.findViewById(R.id.inventory_item_name_text);
        TextView product_quantity = (TextView) view.findViewById(R.id.inventory_item_current_quantity_text);
        TextView product_price = (TextView) view.findViewById(R.id.inventory_item_price_text);
        TextView product_sold = (TextView) view.findViewById(R.id.inventory_item_current_sold_text);
        ImageView product_add_btn = (ImageView) view.findViewById(R.id.sale_product);
        ImageView product_thumbnail = (ImageView) view.findViewById(R.id.product_thumbnail);


        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.PRICE);
        int thumbnailColumnIndex = cursor.getColumnIndex(InventoryEntry.IMAGE);
        int salesColumnIndex = cursor.getColumnIndex(InventoryEntry.ITEMS_SOLD_COUNT);

        int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final String productName = cursor.getString(nameColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final int products_sold = cursor.getInt(salesColumnIndex);
        String productPrice = "Price ₹" + cursor.getString(priceColumnIndex);
        Uri thumbUri = Uri.parse(cursor.getString(thumbnailColumnIndex));

        String productQuantity = String.valueOf(quantity) + " Inventory";
        String productSold = String.valueOf(products_sold) + " Sold";

        final Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

        Log.d(TAG, "genero Uri: " + currentProductUri + " Product name: " + productName + " id: " + id);

        product_name.setText(productName);
        product_quantity.setText(productQuantity);
        product_price.setText(productPrice);
        product_sold.setText(productSold);
        //Glide to import photo images got from another source
        Glide.with(context).load(thumbUri)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_add_a_photo_black_24dp)
                .crossFade()
                .centerCrop()
                .into(product_thumbnail);

        product_add_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, productName + " quantity= " + quantity);
                ContentResolver resolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    int qq = quantity;
                    int yy = products_sold;
                    Log.d(TAG, "new quantity= " + qq);
                    values.put(InventoryEntry.QUANTITY, --qq);
                    values.put(InventoryEntry.ITEMS_SOLD_COUNT, ++yy);
                    resolver.update(
                            currentProductUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentProductUri, null);
                } else {
                    Toast.makeText(context, "Item out of stock", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CustomPrices extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "custom_prices";
    private static final int DB_VERSION = 1;
    public CustomPrices(@Nullable Context context) {
        super(context, TABLE_NAME, null, DB_VERSION);
    }

    public double getCustomPrice(int pupusaId, int partyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                new String[] { "price" },
                String.format("pupusa_id = %d AND party_id = %d", pupusaId, partyId),
                null,
                null,
                null,
                null
        );

        if (!cursor.moveToFirst()) return 0;

        return cursor.getDouble(0);
    }

    public boolean savePrice(int pupusaId, int partyId, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (this.getCustomPrice(pupusaId, partyId) > 0) {
            values.put("price", price);
            int rowsUpdated = db.update(
                    TABLE_NAME,
                    values,
                    String.format("pupusa_id = %d AND party_id = %d", pupusaId, partyId),
                    null
            );
            return rowsUpdated >= 1;
        }

        values.put("party_id", partyId);
        values.put("pupusa_id", pupusaId);
        values.put("price", price);
        long insertedId = db.insert(
                TABLE_NAME,
                null,
                values
        );
        return insertedId != -1;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryInstruction = "CREATE TABLE %s (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "party_id INTEGER NOT NULL," +
                "pupusa_id INTEGER NOT NULL," +
                "price DECIMAL(4,2) NOT NULL)";
        String query = String.format(queryInstruction, TABLE_NAME);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

package com.intimealarm.carerhelp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * @Author: Conor Keenan
 * Student No: x13343806
 * Created on 07/12/2016.
 */

public class DbHelper extends SQLiteOpenHelper {

    // Variables
    public static final String DB_NAME = "CarerHelper.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_CLIENTS = "Client";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";

    private static final String DATABASE_CREATE =
            "create table " + TABLE_CLIENTS + "( " +
                    COLUMN_ID + " INTEGER primary key autoincrement," +
                    COLUMN_CLIENT_ID + " TEXT not null," +
                    COLUMN_NAME + " TEXT not null," +
                    COLUMN_ADDRESS + " TEXT not null," +
                    COLUMN_PHONE + " TEXT not null," +
                    COLUMN_TIME + " TEXT not null," +
                    COLUMN_LAT + " REAL not null," +
                    COLUMN_LNG + " REAL not null);";


    // Constructor
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Create Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);

    }

    // On Database Upgrade
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP DATABASE IF EXISTS " + DB_NAME);
        onCreate(sqLiteDatabase);

    }

    // Method to add a Client
    public Client addClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CLIENT_ID, client.getClient_id());
        values.put(COLUMN_NAME, client.getName());
        values.put(COLUMN_PHONE, client.getPhone());
        values.put(COLUMN_ADDRESS, client.getAddress());
        values.put(COLUMN_TIME, client.getTime());

        LatLng latLng = client.getGeo();
        values.put(COLUMN_LAT, latLng.latitude);
        values.put(COLUMN_LNG, latLng.longitude);

        db.insert(TABLE_CLIENTS, null, values);
        db.close();
        return client;
    }

    // Method to Retrieve All Clients
    public ArrayList<Client> allClients() {
        ArrayList<Client> clients = new ArrayList<Client>();

        String selectQuery = "SELECT  * FROM " + TABLE_CLIENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String name, client_id, address, phone, time;
                double lat, lng;
                LatLng geo;
                name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                client_id = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ID));
                address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS));
                phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                time = cursor.getString(cursor.getColumnIndex(COLUMN_TIME));
                lat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LAT)));
                lng = Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_LNG)));
                geo = new LatLng(lat, lng);

                clients.add(new Client(
                        client_id,
                        name,
                        address,
                        phone,
                        time,
                        geo));

            } while (cursor.moveToNext());
        }

        return clients;
    }

    // Method to Delete a Client
    public void deleteClient(Client client) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CLIENTS +
                " WHERE " + COLUMN_CLIENT_ID + "='" + client.getClient_id() + "' " +
                "AND " + COLUMN_NAME + "='" + client.getName() + "'");

        db.close();
    }

    // Method to Update a Client
    public Client update(Client c, String client_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CLIENT_ID, c.getClient_id());
        values.put(COLUMN_NAME, c.getName());
        values.put(COLUMN_PHONE, c.getPhone());
        values.put(COLUMN_ADDRESS, c.getAddress());
        values.put(COLUMN_TIME, c.getTime());

        LatLng latLng = c.getGeo();
        values.put(COLUMN_LAT, latLng.latitude);
        values.put(COLUMN_LNG, latLng.longitude);

        db.update(TABLE_CLIENTS, values, COLUMN_CLIENT_ID + "='" + client_id + "'", null);
        db.close();

        return c;
    }
}

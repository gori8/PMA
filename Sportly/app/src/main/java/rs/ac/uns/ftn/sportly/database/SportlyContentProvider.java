package rs.ac.uns.ftn.sportly.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import rs.ac.uns.ftn.sportly.model.ValueList;

public class SportlyContentProvider extends ContentProvider {
    private SportlySQLiteHelper database;

    private static final int FRIENDS = 1;
    private static final int FRIENDS_SERVER_ID = 2;
    private static final int SPORTSFIELDS = 3;
    private static final int SPORTSFIELDS_SERVER_ID = 4;
    private static final int EVENTS = 5;
    private static final int EVENTS_SERVER_ID = 6;
    private static final int MY_EVENTS = 7;
    private static final int PARTICIPATING_EVENTS = 8;
    private static final int FAVORITES = 9;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final String AUTHORITY = "rs.ac.uns.ftn.sportly";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");

    static {
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_FRIENDS, FRIENDS);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_FRIENDS + "/#", FRIENDS_SERVER_ID);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_SPORTSFIELDS, SPORTSFIELDS);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_SPORTSFIELDS + "/#", SPORTSFIELDS_SERVER_ID);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_EVENTS, EVENTS);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_EVENTS + "/#", EVENTS_SERVER_ID);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_MY_EVENTS, MY_EVENTS);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_PARTICIPATING_EVENTS, PARTICIPATING_EVENTS);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_FAVORITES, FAVORITES);
    }

    @Override
    public boolean onCreate() {
        database = new SportlySQLiteHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FRIENDS_SERVER_ID:
                queryBuilder.appendWhere(DataBaseTables.SERVER_ID + "="
                        + uri.getLastPathSegment());
                queryBuilder.setTables(DataBaseTables.TABLE_FRIENDS);
                break;
            case FRIENDS:
                queryBuilder.setTables(DataBaseTables.TABLE_FRIENDS);
                break;
            case SPORTSFIELDS_SERVER_ID:
                queryBuilder.appendWhere(DataBaseTables.SERVER_ID + "="
                        + uri.getLastPathSegment());
                queryBuilder.setTables(DataBaseTables.TABLE_SPORTSFIELDS);
                break;
            case SPORTSFIELDS:
                queryBuilder.setTables(DataBaseTables.TABLE_SPORTSFIELDS);
                break;
            case EVENTS_SERVER_ID:
                queryBuilder.appendWhere(DataBaseTables.SERVER_ID + "="
                        + uri.getLastPathSegment());
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case EVENTS:
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case MY_EVENTS:
                queryBuilder.appendWhere(DataBaseTables.EVENTS_CREATOR + "="
                        + 1);
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case PARTICIPATING_EVENTS:
                queryBuilder.appendWhere(DataBaseTables.EVENTS_PARTICIPATING + "="
                        + 1);
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case FAVORITES:
                queryBuilder.appendWhere(DataBaseTables.SPORTSFIELDS_FAVORITE + "="
                        + 1);
                queryBuilder.setTables(DataBaseTables.TABLE_SPORTSFIELDS);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }



    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = insertOrUpdateById(sqlDB, uri, contentValues, "server_id");

        Uri returnUri = Uri.parse(uri+"/"+id);

        return returnUri;
    }

    private long insertOrUpdateById(SQLiteDatabase db, Uri uri,
                                    ContentValues values, String column) throws SQLException {
        try {
            long id = db.insertOrThrow(uri.getLastPathSegment(), null, values);
            getContext().getContentResolver().notifyChange(uri, null);
            return id;
        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?",
                    new String[]{values.getAsString(column)});
            if (nrRows == 0)
                throw e;

            Cursor cursor = query(Uri.parse(uri+"/"+values.getAsString(column)),
                    new String[]{DataBaseTables.ID}, null,null,null);
            cursor.moveToFirst();

            long id = cursor.getLong(0);
            return id;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

       return 1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;

        rowsUpdated = sqlDB.update(uri.getLastPathSegment(),
                values,
                selection,
                selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

}
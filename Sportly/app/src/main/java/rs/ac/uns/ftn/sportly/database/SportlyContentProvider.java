package rs.ac.uns.ftn.sportly.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
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
    private static final int SPORTSFIELDS_EVENTS = 10;
    private static final int APPLICATION_LIST = 11;
    private static final int APPLICATION_LIST_SERVER_ID = 12;
    private static final int FRIENDS_TO_INVITE_FOR_EVENT = 13;

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
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.SPORTSFIELDS_EVENTS + "/#", SPORTSFIELDS_EVENTS);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_APPLICATION_LIST, APPLICATION_LIST);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_APPLICATION_LIST + "/*", APPLICATION_LIST_SERVER_ID);
        sURIMatcher.addURI(AUTHORITY, DataBaseTables.TABLE_FRIENDS+"/invite", FRIENDS_TO_INVITE_FOR_EVENT);
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
            case APPLICATION_LIST_SERVER_ID:
                queryBuilder.appendWhere(DataBaseTables.SERVER_ID + "="
                        + uri.getLastPathSegment());
                queryBuilder.setTables(DataBaseTables.TABLE_APPLICATION_LIST);
                break;
            case APPLICATION_LIST:
                queryBuilder.setTables(DataBaseTables.TABLE_APPLICATION_LIST);
                break;
            case MY_EVENTS:
                queryBuilder.appendWhere(DataBaseTables.EVENTS_APPLICATION_STATUS + "="
                        + "'CREATOR'");
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case PARTICIPATING_EVENTS:
                queryBuilder.appendWhere(DataBaseTables.EVENTS_APPLICATION_STATUS + "="
                        + "'PARTICIPANT'");
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case FAVORITES:
                queryBuilder.appendWhere(DataBaseTables.SPORTSFIELDS_FAVORITE + "="
                        + 1);
                queryBuilder.setTables(DataBaseTables.TABLE_SPORTSFIELDS);
                break;
            case SPORTSFIELDS_EVENTS:
                queryBuilder.appendWhere(DataBaseTables.EVENTS_SPORTS_FILED_ID + "="
                        + uri.getLastPathSegment());
                queryBuilder.setTables(DataBaseTables.TABLE_EVENTS);
                break;
            case FRIENDS_TO_INVITE_FOR_EVENT:

                String inviteQuery="SELECT DISTINCT f._id, f.first_name, f.last_name, f.server_id, f.email"
                        + " FROM friends f LEFT OUTER JOIN application_list al ON (f.server_id = al.applier_id)"
                        + " WHERE f.friends_type='CONFIRMED' AND f.server_id NOT IN (SELECT alist.applier_id FROM application_list alist WHERE alist.event_id="+selection+")";

                SQLiteDatabase db = database.getWritableDatabase();
                Cursor cursor = db.rawQuery(inviteQuery,null,null);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);

                return cursor;
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
        long id;
        try {
            id = db.insertOrThrow(uri.getLastPathSegment(), null, values);
            getContext().getContentResolver().notifyChange(uri, null);

        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?",
                    new String[]{values.getAsString(column)});
            if (nrRows == 0)
                throw e;

            //IF SERVER ID IS NOT NUMBER PUT '' BECAUSE OF DATABASE SYNTAX
            String arg = values.getAsString(column);
            try{
                Integer.parseInt(arg);
            }catch (Exception ex){
                 arg = "'"+arg+"'";
            }

            Cursor cursor = query(Uri.parse(uri+"/"+arg),
                    new String[]{DataBaseTables.ID}, null,null,null);
            cursor.moveToFirst();

            id = cursor.getLong(0);
        }

        int uriType = sURIMatcher.match(uri);
        switch(uriType){
            case SPORTSFIELDS:{
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_FAVORITES), null);
            }break;
            case EVENTS:{
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_MY_EVENTS), null);
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_PARTICIPATING_EVENTS), null);
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.SPORTSFIELDS_EVENTS+"/"+values.getAsInteger(DataBaseTables.EVENTS_SPORTS_FILED_ID)), null);
            }break;
            case FRIENDS:{
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_FRIENDS+"/invite"), null);
            }break;
            case APPLICATION_LIST:{
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_FRIENDS+"/invite"), null);
            }break;
        }

        return id;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqlDB = database.getWritableDatabase();
        sqlDB.delete(uri.getLastPathSegment(), selection, selectionArgs);

        int uriType = sURIMatcher.match(uri);

        getContext().getContentResolver().notifyChange(uri, null);

        switch(uriType){
            case FRIENDS: {
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + DataBaseTables.TABLE_FRIENDS), null);
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI + DataBaseTables.TABLE_FRIENDS + "/invite"), null);
            }
                break;

            case EVENTS:{
                String id = selection.split("AND")[1].split("=")[1];
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_MY_EVENTS), null);
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_PARTICIPATING_EVENTS), null);
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.TABLE_PARTICIPATING_EVENTS), null);
                getContext().getContentResolver().notifyChange(Uri.parse(CONTENT_URI+DataBaseTables.SPORTSFIELDS_EVENTS+"/"+id),null);
            }
                break;

            case SPORTSFIELDS:{
                getContext().getContentResolver().notifyChange(Uri.parse(DataBaseTables.TABLE_FAVORITES),null);
            }
        }

        return 0;
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

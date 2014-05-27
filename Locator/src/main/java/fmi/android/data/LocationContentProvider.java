package fmi.android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by dpavlov on 14-2-20.
 */
public class LocationContentProvider extends ContentProvider {

    SQLHelper sqlHelper;
    String table = "";
    public static final Uri CONTENT_URI = Uri.parse("content://fmi.android.locator.cursorloader.data");

    @Override
    public boolean onCreate() {
        sqlHelper = new SQLHelper(getContext());
        table = sqlHelper.getTableName();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        Cursor cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        long value = database.insert(table, null, contentValues);
        return Uri.parse(String.valueOf(value));
    }

    @Override
    public int delete(Uri uri, String where, String[] args) {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        return database.delete(table, where, args);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        return database.update(table, contentValues, whereClause, whereArgs);
    }
}

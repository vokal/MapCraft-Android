package com.vokal.mapcraft.cp;

import android.content.*;
import android.content.Entity.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class SQLiteSimpleContentProvider extends ContentProvider {
    static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    protected SQLiteOpenHelper mDbHelper;

    public static boolean isValidCursor(final Cursor aCursor) {
        return aCursor != null && !aCursor.isClosed() 
            && !aCursor.isAfterLast() && !aCursor.isBeforeFirst();
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public String getType(Uri aUri) {
        return null;
    }

    @Override
    public int delete(Uri aUri, String aWhere, String[] aWhereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String table = getTableFromUri(aUri);

        return db.delete(table, aWhere, aWhereArgs);
    }

    @Override
    public int update(Uri aUri, ContentValues aValues, String aWhere, String[] aWhereArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table = getTableFromUri(aUri);

        int result = db.update(table, aValues, aWhere, aWhereArgs);

        getContext().getContentResolver().notifyChange(aUri, null);

        return result;
    }

    @Override
    public int bulkInsert(Uri aUri, ContentValues[] aValues) {
        String table = getTableFromUri(aUri);
        int result = 0;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (ContentValues value : aValues) {
                db.insertWithOnConflict(table, "", value, SQLiteDatabase.CONFLICT_REPLACE);
                result++;
            }

            db.setTransactionSuccessful();
            if (result > 0) {
                getContext().getContentResolver().notifyChange(aUri, null);
            }
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return result;
    }

    @Override
    public Uri insert(Uri aUri, ContentValues aValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table = getTableFromUri(aUri);

        long id = db.insertWithOnConflict(table, "", aValues, SQLiteDatabase.CONFLICT_REPLACE);

        if (id > -1) {
            Uri rowUri = ContentUris.withAppendedId(aUri, id);

            getContext().getContentResolver().notifyChange(aUri, null);
            return rowUri;
        }

        throw new SQLException("Failed to insert row into " + aUri);
    }

    protected abstract String getTableFromUri(final Uri aUri);

    @Override
    public Cursor query(Uri aUri, String[] aProjection, String aSelection,
            String[] aSelectionArgs, String aSortOrder) 
    {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        builder.setTables(getTableFromUri(aUri));

        Cursor result = builder.query(db, aProjection, aSelection, 
                aSelectionArgs, null, null, aSortOrder);
        result.setNotificationUri(getContext().getContentResolver(), aUri);

        return result;
    }

    /**
     * Inserts an argument at the beginning of the selection arg list.
     *
     * The {@link android.database.sqlite.SQLiteQueryBuilder}'s where clause is
     * prepended to the user's where clause (combined with 'AND') to generate
     * the final where close, so arguments associated with the QueryBuilder are
     * prepended before any user selection args to keep them in the right order.
     */
    protected String[] insertSelectionArg(String[] selectionArgs, String arg) {
        if (selectionArgs == null) {
            return new String[] {arg};
        } else {
            int newLength = selectionArgs.length + 1;
            String[] newSelectionArgs = new String[newLength];
            newSelectionArgs[0] = arg;
            System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, selectionArgs.length);
            return newSelectionArgs;
        }
    }

    protected String appendWhere(String aWhere, String aAppend) {
        if (aWhere == null) {
            return aAppend;
        }

        return aWhere + " AND " + aAppend;
    }

}

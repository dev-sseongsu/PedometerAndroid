package com.hoon.pedometer.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hoon.pedometer.provider.PedometerContract.DailySteps;
import com.hoon.pedometer.provider.PedometerDatabase.Tables;

import static com.hoon.pedometer.provider.PedometerContract.CONTENT_AUTHORITY;
import static com.hoon.pedometer.provider.PedometerContract.PATH_DAILY_STEPS;

public class PedometerProvider extends ContentProvider {

    private static final int DAILY_STEP = 100;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(CONTENT_AUTHORITY, PATH_DAILY_STEPS, DAILY_STEP);
    }

    private PedometerDatabase mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new PedometerDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {
            case DAILY_STEP:
                queryBuilder.setTables(Tables.DAILY_STEPS);
                return queryBuilder.query(db, projection, selection, selectionArgs,
                        null, null, sortOrder, null);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case DAILY_STEP:
                return DailySteps.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId;

        switch (URI_MATCHER.match(uri)) {
            case DAILY_STEP:
                rowId = db.insertOrThrow(Tables.DAILY_STEPS, null, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Uri insertedItemUri = ContentUris.withAppendedId(uri, rowId);
        notifyChange(insertedItemUri);
        return insertedItemUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int deletedItems;

        switch (URI_MATCHER.match(uri)) {
            case DAILY_STEP:
                deletedItems = db.delete(Tables.DAILY_STEPS, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedItems > 0) notifyChange(uri);
        return deletedItems;
    }

    @Override
    public int update(@NonNull Uri uri,
                      ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int updatedItems;

        switch (URI_MATCHER.match(uri)) {
            case DAILY_STEP:
                updatedItems = db.update(Tables.DAILY_STEPS, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updatedItems > 0) notifyChange(uri);
        return updatedItems;
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }
}

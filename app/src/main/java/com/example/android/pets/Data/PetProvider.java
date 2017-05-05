package com.example.android.pets.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.pets.Data.PetContract.PetEntry;

public class PetProvider extends ContentProvider
{
    public static final String LOG_TAG = PetProvider.class.getSimpleName();
    private PetDbHelper mDbHelper;

    private static final int PETS = 100;
    private static final int PET_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    @Override
    public boolean onCreate()
    {
        mDbHelper = new PetDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match)
        {
            case PETS:
                cursor = db.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[] {
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = db.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;

            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case PETS:
                return insertPet(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertPet (Uri uri, ContentValues values)
    {
        petValidation(values);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(PetEntry.TABLE_NAME, null, values);

        if (id == -1)
        {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private ContentValues petValidation(ContentValues values)
    {
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        String weight = values.getAsString(PetEntry.COLUMN_PET_WEIGHT);

        //NAME VALIDATION SHOULD NEVER BE SEEN AS IT IS COVERED IN THE EDITOR ACTIVITY
        if (name.equals(""))
        {
            values.put(PetEntry.COLUMN_PET_NAME, "No Name");
        }

        if (breed.equals(""))
        {
            values.put(PetEntry.COLUMN_PET_BREED, "Unknown");
        }

        if (weight.equals(""))
        {
            weight = "0";
            int petWeight = Integer.parseInt(weight);
            values.put(PetEntry.COLUMN_PET_WEIGHT, petWeight);
        }

        return values;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case PETS:
                return deletePet(uri, selection, selectionArgs);

            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return deletePet(uri, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    private int deletePet(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        final int match = sUriMatcher.match(uri);

        switch (match)
        {
            case PETS:
                return updatePet(uri, values, selection, selectionArgs);

            case PET_ID:
                selection = PetEntry.COLUMN_PET_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if (values.size() == 0)
        {
            return 0;
        }

        petValidation(values);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdated = db.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}

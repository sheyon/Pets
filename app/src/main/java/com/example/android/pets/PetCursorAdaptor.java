package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CursorAdapter;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.pets.Data.PetContract;


public class PetCursorAdaptor extends CursorAdapter
{
    public PetCursorAdaptor(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        TextView petNameTextView = (TextView) view.findViewById(R.id.name);
        TextView petBreedTextView = (TextView) view.findViewById(R.id.summary);

        String petName = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_NAME));
        String petBreed = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_BREED));

        petNameTextView.setText(petName);
        petBreedTextView.setText(petBreed);
    }
}
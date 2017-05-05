/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.Data.PetContract.PetEntry;

public class EditorActivity extends AppCompatActivity {

    //private PetDbHelper mDbHelper;

    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        Uri currentPetUri = intent.getData();

        if (currentPetUri == null)
        {
            setTitle(getString(R.string.editor_activity_title_add_pet));
        }
        else
        {
            setTitle(getString(R.string.editor_activity_title_edit_pet));
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        //mDbHelper = new PetDbHelper(this);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mGenderSpinner.setAdapter(genderSpinnerAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selection = (String) parent.getItemAtPosition(position);

                if (!TextUtils.isEmpty(selection))
                {
                    if (selection.equals(getString(R.string.gender_male)))
                    {
                        mGender = PetEntry.GENDER_MALE;
                    }
                    else if (selection.equals(getString(R.string.gender_female)))
                    {
                        mGender = PetEntry.GENDER_FEMALE;
                    }
                    else
                    {
                        mGender = PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                mGender = PetEntry.GENDER_UNKNOWN;
            }
        });
    }

    private void insertPet()
    {
//      SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String petNameEntered = mNameEditText.getText().toString().trim();
        String petBreedEntered = mBreedEditText.getText().toString().trim();
        String petWeightEntered = mWeightEditText.getText().toString().trim();

        if (petNameEntered.equals(""))
        {
            Toast noName = Toast.makeText(this, "Please enter a pet name!" , Toast.LENGTH_SHORT);
            noName.show();
            return;
        }

//        OLD VALIDATION, NOW COVERED BY THE PET PROVIDER
//
//        if (petBreedEntered.equals(""))
//        {
//            petBreedEntered = "Unknown";
//        }
//
//        if (petWeightEntered.equals(""))
//        {
//            petWeightEntered = "0";
//        }
//
//        int petWeight = Integer.parseInt(petWeightEntered);

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, petNameEntered);
        values.put(PetEntry.COLUMN_PET_BREED, petBreedEntered);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, petWeightEntered);

        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

        if (newUri == null)
        {
            Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
        }

//        OLD CODE FOR DIRECT DATABASE INTERACTION
//
//        long newPetId = db.insert(PetEntry.TABLE_NAME, null, values);
//
//        if (newPetId == -1)
//        {
//            Toast fail = Toast.makeText(this, petNameEntered + " could not be added!" , Toast.LENGTH_SHORT);
//            fail.show();
//        }
//        else
//        {
//            Toast success = Toast.makeText(this, petNameEntered + " added! ID: " + newPetId, Toast.LENGTH_SHORT);
//            success.show();
//        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId())
        {
            case R.id.action_save:
                insertPet();
                return true;

            case R.id.action_delete:
                //nothing yet
                return true;

            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
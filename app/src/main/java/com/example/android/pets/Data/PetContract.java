package com.example.android.pets.Data;

import android.provider.BaseColumns;

public final class PetContract {

    private PetContract()
    {
        //empty
    }

    public static final class PetEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "pets";

        public static final String COLUMN_PET_ID = "_id";
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }


}

package com.mayo.firebase.database;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class FirebaseDatabase {
    DatabaseReference mDatabaseReference;

    FirebaseDatabase() {
        //intialize database reference
        initDatabase();
    }

    private void initDatabase() {
        mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    }
}

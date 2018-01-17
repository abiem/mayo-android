package com.mayo.firebase.database;

import com.google.firebase.database.DatabaseReference;
import com.mayo.models.Task;
import com.mayo.models.Users;

import java.util.Date;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class FirebaseDatabase {
    DatabaseReference mDatabaseReference;

    public FirebaseDatabase() {
        //intialize database reference
        initDatabase();
    }

    private void initDatabase() {
        mDatabaseReference = com.google.firebase.database.FirebaseDatabase.getInstance().getReference().child("android");
    }

    // [START basic_write]
    public void writeNewTask(Task pTask) {
        mDatabaseReference.child("tasks").setValue(pTask);
    }

    public void writeNewUser(Users pUser) {
        mDatabaseReference.child("users").setValue(pUser);
    }
}

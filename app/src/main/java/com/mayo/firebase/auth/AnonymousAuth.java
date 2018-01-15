package com.mayo.firebase.auth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mayo.R;
import com.mayo.Utility.CommonUtility;

/**
 * Created by Lakshmikodali on 15/01/18.
 */

public class AnonymousAuth {
    private static FirebaseAuth mAuth;

    public static void signInAnonymously(final Activity pActivity) {
        // [START signin_anonymously]
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously()
                .addOnCompleteListener(pActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //If sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            setUserId(user, pActivity);
                        } else {
                            // If sign in fails
                            Toast.makeText(pActivity, pActivity.getResources().getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private static void setUserId(FirebaseUser pFireBaseUser, Activity pActivity) {
        CommonUtility.setUserId(pFireBaseUser.getUid(), pActivity);
    }

    public static void signOut() {
        mAuth.signOut();
    }
}

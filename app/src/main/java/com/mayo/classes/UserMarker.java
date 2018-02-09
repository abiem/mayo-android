package com.mayo.classes;

import android.content.Context;

import com.mayo.models.UsersLocations;
import java.util.HashSet;

/**
 * Created by Lakshmi on 09/02/18.
 */

public class UserMarker {
    Context mContext;
    HashSet<UsersLocations> mUserLocations;

    public UserMarker(Context pContext, HashSet<UsersLocations> pUserLocations) {
        mContext = pContext;
        mUserLocations=pUserLocations;
    }

}

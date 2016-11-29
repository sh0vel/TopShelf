package com.shovonh.topshelf;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Shovon on 11/21/2016.
 */

public class User {
    public static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private User(){}

    public static String getUid(){
        return user.getUid();
    }

    public static String getName(){
        return user.getDisplayName();
    }
}

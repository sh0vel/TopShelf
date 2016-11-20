package com.shovonh.topshelf;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shovon on 11/19/2016.
 */

public class Database {
    private static FirebaseDatabase db;

    private Database(){

    }

    public static FirebaseDatabase getDBRef(){
        if (db == null)
            db = FirebaseDatabase.getInstance();
        return  db;
    }
}

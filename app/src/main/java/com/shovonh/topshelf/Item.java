package com.shovonh.topshelf;

import org.parceler.Parcel;

/**
 * Created by Shovon on 11/19/2016.
 */

@Parcel
public class Item {
    String name;
    boolean checked;

    public Item(){

    }

    public Item(String name){
        this.name=name;
        checked = false;
    }

    public String getName() {
        return name;
    }
    public boolean toggleChecked(){
        checked = !checked;
        return  checked;
    }
}

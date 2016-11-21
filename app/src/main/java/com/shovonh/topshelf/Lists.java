package com.shovonh.topshelf;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shovon on 11/19/2016.
 */

@Parcel
public class Lists {
    public String name;
    public ArrayList<Item> itemsInList;


    public Lists() {

    }

    public Lists(String name) {
        this.name = name;
        itemsInList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Item> getItemsInList() {
        if (itemsInList != null)
            return itemsInList;
        else
            itemsInList = new ArrayList<>();
        return itemsInList;
    }

    public void setItemsInList(ArrayList<Item> itemsInList) {
        this.itemsInList = itemsInList;
    }

    public boolean anyItemsChecked(){
        for (Item i : itemsInList)
            if (i.checked)
                return true;
        return false;
    }


}

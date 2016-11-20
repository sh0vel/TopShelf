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


    public Lists(){

    }

    public Lists(String name){
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
        return itemsInList;
    }

    public void setItemsInList(ArrayList<Item> itemsInList) {
        this.itemsInList = itemsInList;
    }
    @Exclude
    public Map<Integer, Item> toMap(){
        HashMap<Integer, Item> result = new HashMap<>();
        for (Item i : itemsInList)
            result.put(itemsInList.indexOf(i), i);
        return result;
    }

    @Exclude
    public static Map<String, Lists> listsToMap(ArrayList<Lists> ls){
        HashMap<String, Lists> r = new HashMap<>();
        for (Lists l : ls){
            r.put(l.name, l);
        }

        return r;
    }

}

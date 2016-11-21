package com.shovonh.topshelf;

/**
 * Created by Shovon on 11/19/2016.
 */

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
    public void toggleChecked(){
        checked = !checked;
    }
}

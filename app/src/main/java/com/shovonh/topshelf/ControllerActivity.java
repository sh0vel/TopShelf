package com.shovonh.topshelf;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hanks.library.AnimateCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ControllerActivity extends AppCompatActivity implements AllListsFragment.OnFragmentInteractionListener, ItemsInListFragement.OnFragmentInteractionListener {
    FragmentManager fm;
    Fragment mAllListsFragment;
    Fragment mItemsinListFragment;
    FloatingActionButton fab;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Lists mMainList;

    public static ArrayList<Lists> mAllLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        mAllLists = Parcels.unwrap(getIntent().getBundleExtra("alllists"));
//        mMainList = Parcels.unwrap(getIntent().getBundleExtra("mainlist"));


        mAllListsFragment = AllListsFragment.newInstance(mAllLists, mMainList);

        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragmentContainer, mAllListsFragment).commit();


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                View v = getLayoutInflater().inflate(R.layout.dialogtext, null);
                final MaterialEditText et = (MaterialEditText) v.findViewById(R.id.dialog_list_title);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ControllerActivity.this);
                builder.setTitle("New Lists").setView(v).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText() != null) {
                            ((AllListsFragment) currentFragment).createList(et.getText().toString());
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });

//        Database.getDBRef().getReference("main_list").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                System.err.println("!!!!!!" + dataSnapshot.getValue());
//
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        });







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0) {
            fab.show();
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void openList(Lists li) {
        fab.hide();
        mItemsinListFragment = new ItemsInListFragement().newInstance(li);
        fm.beginTransaction().addToBackStack("").replace(R.id.fragmentContainer, mItemsinListFragment).commit();

    }

    public void setUpAllLists() {

    }


}

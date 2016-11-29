package com.shovonh.topshelf;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.hanks.library.AnimateCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.parceler.Parcels;

import java.util.ArrayList;


public class ItemsInListFragement extends Fragment {
    enum action{
        deleted,
        checked,
        added
    }
    private static final String TAG = AllListsFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    ContentAdapter mContentAdapter;
    MaterialEditText mEditText;
    ImageButton mImgButton;

    action updateAction;
    int checkedPosition;

    boolean mDeleteFabEnabled = false;

    Lists mCurrentList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String listName;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ItemsInListFragement() {
        // Required empty public constructor
    }


    public static ItemsInListFragement newInstance(Lists openedLists) {
        ItemsInListFragement fragment = new ItemsInListFragement();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, Parcels.wrap(openedLists));
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentList = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM1));

            //System.out.println("nombre = " + mCurrentList.getName());

//            Database.getDB().getReference(mCurrentList.name).child("itemsInList").addChildEventListener(
//                    new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//
//
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                            Item i = dataSnapshot.getValue(Item.class);
//                            //mCurrentList.getItemsInList().add(i);
//                            System.out.println("Items"+i.getName().toString());
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });

            Database.getDB().getReference("users").child(User.getUid()).addChildEventListener(
                    new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            System.out.println("added");
//                            Item i = dataSnapshot.getValue(Item.class);
//                            mContentAdapter.addItem(i);

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Lists l = dataSnapshot.getValue(Lists.class);
                            mCurrentList = l;

                            if (updateAction == action.checked)
                                mContentAdapter.updateList(checkedPosition);
                            else
                                mContentAdapter.updateList();

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            System.out.println("removed");
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.items_in_list, container, false);

        mEditText = (MaterialEditText) view.findViewById(R.id.textbox);
        mImgButton = (ImageButton) view.findViewById(R.id.textbox_button);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview_items);
        mContentAdapter = new ContentAdapter();

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateAction = action.added;
                    Item i = new Item(mEditText.getText().toString());
                    Database.getDB().getReference("users").child(User.getUid()).child(mCurrentList.getName())
                            .child("itemsInList").child(mCurrentList.getItemsInList().size() + "")
                            .setValue(i);
                    mEditText.setText("");
                    handled = true;
                }
                return handled;
            }
        });


        mRecyclerView.setAdapter(mContentAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public AnimateCheckBox checkBox;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_listitem, parent, false));
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            checkBox = (AnimateCheckBox) itemView.findViewById(R.id.item_checkbox);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                    updateAction = action.checked;
                    checkedPosition = getAdapterPosition();
                    Item i = mCurrentList.getItemsInList().get(getAdapterPosition());
//                    i.toggleChecked();

                    Database.getDB().getReference("users").child(User.getUid()).child(mCurrentList.getName())
                            .child("itemsInList").child(getAdapterPosition() + "")
                            .child("checked").setValue(i.toggleChecked());


                    Log.v(TAG, "OnClick Index: " + getAdapterPosition() + " Name: " + i.getName() + " Boolean: " + i.checked);

                }
            });
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        public ContentAdapter() {
            System.out.println("mCurrentList: " + mCurrentList.getName());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item i = mCurrentList.getItemsInList().get(position);

            holder.itemName.setText(i.getName());
            holder.checkBox.setChecked(i.checked);
            //Log.v(TAG, "OnBind Index: " + position + " Name: " + i.getName() + " Boolean: " + i.checked);


            if (mCurrentList.anyItemsChecked())
                enableDeleteFab();
            else
                disableDeleteFab();

        }

        @Override
        public int getItemCount() {
            return mCurrentList.getItemsInList().size();
        }

        public void addItem(Item i) {
            mCurrentList.getItemsInList().add(i);
            notifyItemInserted(mCurrentList.getItemsInList().size() - 1);
        }

        public void updateList() {
            notifyDataSetChanged();
        }

        public void updateList(int pos) {
            notifyItemChanged(pos);
        }


    }

    public void deleteAllCheckedItems(){
        ArrayList<Item> al = mCurrentList.getItemsInList();
        ArrayList toRemove = new ArrayList();
        for (Item i : al ) {
            if (i.checked) {
                Log.v(TAG, "Index:" + al.indexOf(i) + " Name: " + i.getName());
                toRemove.add(i);
            }
        }
        mCurrentList.getItemsInList().removeAll(toRemove);
        updateAction = action.deleted;
        Database.getDB().getReference("users").child(User.getUid()).child(mCurrentList.getName())
                .child("itemsInList").setValue(al);
        disableDeleteFab();
    }

    void enableDeleteFab() {
        if (mListener != null) {
            mListener.enableDeleteFab();
            mDeleteFabEnabled = true;
        }
    }

    void disableDeleteFab() {
        if (mListener != null) {
            mListener.disableDeleteFab();
            mDeleteFabEnabled = false;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface OnFragmentInteractionListener {
        void enableDeleteFab();

        void disableDeleteFab();
    }
}

package com.shovonh.topshelf;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hanks.library.AnimateCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.parceler.Parcels;

import java.util.ArrayList;


public class ItemsInListFragement extends Fragment {
    RecyclerView mRecyclerView;
    ContentAdapter mContentAdapter;
    MaterialEditText mEditText;
    ImageButton mImgButton;

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

            Database.getDBRef().getReference(mCurrentList.name).addChildEventListener(
                    new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Item i = dataSnapshot.child("itemsInList").getValue(Item.class);
                            System.out.println(i.getName());
                            //TODO: put items into datbase

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
                    Item i = new Item(mEditText.getText().toString());
                    Database.getDBRef().getReference("lists").child(mCurrentList.getName())
                            .child("itemsInList").child(mCurrentList.getItemsInList().size() + "")
                            .setValue(i);


                    mCurrentList.getItemsInList().add(i);
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
                }
            });
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        public ContentAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemName.setText(mCurrentList.getItemsInList().get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mCurrentList.getItemsInList().size();
        }

        public void addItem(Item i) {
            mCurrentList.getItemsInList().add(i);
            notifyItemInserted(mCurrentList.getItemsInList().size() - 1);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

    }
}

package com.shovonh.topshelf;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;


public class AllListsFragment extends Fragment {
    CardView shoppingList;
    RecyclerView mRecyclerView;
    ContentAdapter mContentAdapter;

    ArrayList<Lists> mAllLists = new ArrayList<>();
    Lists mMainList;

    private OnFragmentInteractionListener mListener;

    public AllListsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AllListsFragment newInstance(ArrayList list, Lists mainList) {
        AllListsFragment fragment = new AllListsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Database.getDBRef().getReference("lists").addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Lists l = dataSnapshot.getValue(Lists.class);
                        mContentAdapter.addItem(l);
//                        StringBuilder builder = new StringBuilder();
//                        Lists lit = dataSnapshot.getValue(Lists.class);
//                        builder.append("List name: ").append(lit.getName()).append(" items: ");
//                        for (Item i : lit.getItemsInList()){
//                            builder.append(i.getName() + ", ");
//                        }
                        //System.out.println(builder.toString());
                        //for (DataSnapshot data : dataSnapshot.getChildren()) {
                        // System.err.println("!!!!!!" + data.getValue());

                        //}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        //System.out.println("!!!!!!!!!!!!!!!!!!!!!!" + dataSnapshot.getValue() +":::" + s);
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
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.all_lists, container, false);
        shoppingList = (CardView) view.findViewById(R.id.shopping_list_card);
        final TextView tv = (TextView) view.findViewById(R.id.shopping_list_card).findViewById(R.id.list_name);
        tv.setText("Shopping List");
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listItemPressed(mMainList);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview_lists);
        mContentAdapter = new ContentAdapter();

        mRecyclerView.setAdapter(mContentAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Database.getDBRef().getReference("lists").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lists l = dataSnapshot.getValue(Lists.class);
                System.out.println(l);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemsPreview, itemCount;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_listitem, parent, false));
            itemName = (TextView) itemView.findViewById(R.id.list_name);
            itemsPreview = (TextView) itemView.findViewById(R.id.list_preview_items);
            itemCount = (TextView) itemView.findViewById(R.id.list_item_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listItemPressed(mAllLists.get(getAdapterPosition()));

                }
            });
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        //ArrayList<Lists> mItemsInList = new ArrayList<>();

        public ContentAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemName.setText(mAllLists.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mAllLists.size();
        }

        public void addItem(Lists l) {
             mAllLists.add(l);
            notifyItemInserted(mAllLists.size() - 1);
        }
    }


    public void listItemPressed(Lists li) {
        if (mListener != null)
            mListener.openList(li);
    }

    public void createList(String listName){
        Lists l = new Lists(listName);
        Database.getDBRef().getReference("lists").child(listName).setValue(l);
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void openList(Lists li);
    }
}

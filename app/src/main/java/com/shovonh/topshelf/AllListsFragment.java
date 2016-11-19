package com.shovonh.topshelf;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.parceler.Parcels;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllListsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllListsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllListsFragment extends Fragment {
    CardView shoppingList;
    RecyclerView mRecyclerView;
    ContentAdapter mContentAdapter;

    ArrayList<Lists> mAllLists;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AllListsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AllListsFragment newInstance(ArrayList list) {
        AllListsFragment fragment = new AllListsFragment();
       Bundle args = new Bundle();
        Parcelable wrapped = Parcels.wrap(list);
       args.putParcelable(ARG_PARAM1, wrapped);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mAllLists = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM1));
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                listItemPressed(tv.getText().toString());
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
                    listItemPressed(itemName.getText().toString());
                }
            });
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        ArrayList<Lists> mItemsInList = new ArrayList<>();

        public ContentAdapter() {
            for (Lists l : mAllLists)
                mItemsInList.add(l);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemName.setText(mItemsInList.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mItemsInList.size();
        }

        public void addItem(Lists l) {
             mItemsInList.add(l);
            notifyItemInserted(mItemsInList.size() - 1);
        }
    }


    public void listItemPressed(String listName) {
        if (mListener != null)
            mListener.openList(listName);
    }

    public void createList(String listName){
        Lists l = new Lists(listName);
        Database.getDBRef().getReference("lists").push().setValue(l);
        //System.out.println(Database.getDBRef().getReference().push().getKey());
        mContentAdapter.addItem(l);
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
        void openList(String listName);
    }
}

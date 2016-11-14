package com.shovonh.topshelf;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hanks.library.AnimateCheckBox;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemsInListFragement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemsInListFragement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemsInListFragement extends Fragment {
    RecyclerView mRecyclerView;
    ContentAdapter mContentAdapter;
    MaterialEditText mEditText;
    ImageButton mImgButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ItemsInListFragement() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemsInListFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemsInListFragement newInstance() {
        ItemsInListFragement fragment = new ItemsInListFragement();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
                    mContentAdapter.addItem(mEditText.getText().toString());
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {


        //
        ArrayList mItemsInList = new ArrayList();

        public ContentAdapter() {
            mItemsInList.add("Apples");
            mItemsInList.add("Bannannannnas");
            mItemsInList.add("Pinapples");
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemName.setText(mItemsInList.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return mItemsInList.size();
        }

        public void addItem(String item) {
            mItemsInList.add(item);
            notifyItemInserted(mItemsInList.size() - 1);
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

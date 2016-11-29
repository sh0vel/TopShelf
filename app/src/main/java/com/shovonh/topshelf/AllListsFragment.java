package com.shovonh.topshelf;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AllListsFragment extends Fragment {
    private static final String TAG = AllListsFragment.class.getSimpleName();
    CardView shoppingList;
    RecyclerView mRecyclerView;
    ContentAdapter mContentAdapter;

    ArrayList<Lists> mAllLists = new ArrayList<>();
    Lists mMainList;

    int mSelectionIndex;

    private OnFragmentInteractionListener mListener;

    public AllListsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AllListsFragment newInstance() {
        AllListsFragment fragment = new AllListsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Create");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Database.getDB().getReference("users").child(User.getUid()).addChildEventListener(
                new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Lists l = dataSnapshot.getValue(Lists.class);
                        if (l.getName().equals("Shopping List")) {
                            if (mMainList == null)
                                mMainList = l;
                            updateMainList();
                        } else
                            mContentAdapter.addItem(l);

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Lists l = dataSnapshot.getValue(Lists.class);
                        Log.e(TAG, "Changed");
                        if (l.getName().equals("Shopping List")) {
                            mMainList = l;
                            updateMainList();
                        } else
                            mAllLists.get(mSelectionIndex).setItemsInList(l.getItemsInList());
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        mContentAdapter.removedFromDB(mSelectionIndex);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                        mAllLists.remove(dataSnapshot.getValue(Lists.class));
//                        mContentAdapter.updateList();
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
        Log.v(TAG, "CreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.all_lists, container, false);
        shoppingList = (CardView) view.findViewById(R.id.shopping_list_card);

        updateMainList();
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMainList == null) {
                    mMainList = new Lists("Shopping List");
                    Database.getDB().getReference("users").child(User.getUid()).child(mMainList.getName()).setValue(mMainList);
                }
                listItemPressed(mMainList);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycleview_lists);
        mContentAdapter = new ContentAdapter();

        mRecyclerView.setAdapter(mContentAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();

        return view;
    }

    public void updateMainList() {
        ((TextView) shoppingList.findViewById(R.id.list_name)).setText("Shopping List");
        if (mMainList != null) {
            int count = mMainList.getItemsInList().size();
            ((TextView) shoppingList.findViewById(R.id.list_item_count)).setText(count + (count > 1 ? "\nItems" : "\nItem"));
            StringBuilder builder = new StringBuilder();
            if (count > 0)
                builder.append(mMainList.getItemsInList().get(0).getName());
            if (count > 1)
                builder.append(", " + mMainList.getItemsInList().get(1).getName());
            if (count > 2)
                builder.append(", " + mMainList.getItemsInList().get(2).getName() + "...");

            ((TextView) shoppingList.findViewById(R.id.list_preview_items)).setText(builder.toString());
        }
    }

    public void setUpListText() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemsPreview, itemCount;
        Button undoButton;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_listitem, parent, false));
            itemName = (TextView) itemView.findViewById(R.id.list_name);
            itemsPreview = (TextView) itemView.findViewById(R.id.list_preview_items);
            itemCount = (TextView) itemView.findViewById(R.id.list_item_count);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectionIndex = getAdapterPosition();
                    listItemPressed(mAllLists.get(mSelectionIndex));

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    Lists l = mAllLists.get(getAdapterPosition());
                    int size = mAllLists.size();
                    for (Item i : l.getItemsInList()){
                        Log.v(TAG, i.getName() + ", " + mMainList.getItemsInList().size());

                        Database.getDB().getReference("users").child(User.getUid()).child(mMainList.getName())
                                .child("itemsInList").child(mMainList.getItemsInList().size() + "")
                                .setValue(i);
                        mMainList.getItemsInList().add(i);
                    }

                    Snackbar snackbar = Snackbar.make
                            (v, "Added " + l.getItemsInList().size() + " items to Shopping List", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    return true;
                }
            });


        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private static final int PENDING_REMOVAL_TIMEOUT = 3000;
        //ArrayList<Lists> mItemsInList = new ArrayList<>();
        List<Lists> itemsPendingRemoval;
        boolean undoOn; // is undo on, you can turn it on from the toolbar menu

        private Handler handler = new Handler(); // hanlder for running delayed runnables
        HashMap<Lists, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

        public ContentAdapter() {
            itemsPendingRemoval = new ArrayList<>();
            setUndoOn(true);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Lists item = mAllLists.get(position);
            Log.e(TAG, item.getName() + " added at index " +position);
            int count = item.getItemsInList().size();
            holder.itemName.setText(item.getName());
            Log.e(TAG, "itemName = " +holder.itemName.getText());
            holder.itemCount.setText(count + (count > 1 ? "\nItems" : "\nItem"));
            StringBuilder builder = new StringBuilder();
            if (count > 0)
                builder.append(item.getItemsInList().get(0).getName());
            if (count > 1)
                builder.append(", " + item.getItemsInList().get(1).getName());
            if (count > 2)
                builder.append(", " + item.getItemsInList().get(2).getName() + "...");

            holder.itemsPreview.setText(builder.toString());



            if (itemsPendingRemoval.contains(item)) {
                Log.e(TAG, "itemsPendingRemoval");
                // we need to show the "undo" state of the row
                holder.itemView.setBackgroundColor(Color.RED);
                holder.itemName.setVisibility(View.GONE);
                holder.itemsPreview.setVisibility(View.GONE);
                holder.itemCount.setVisibility(View.GONE);
                holder.undoButton.setVisibility(View.VISIBLE);
                holder.undoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // user wants to undo the removal, let's cancel the pending task
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);
                        if (pendingRemovalRunnable != null)
                            handler.removeCallbacks(pendingRemovalRunnable);
                        itemsPendingRemoval.remove(item);
                        // this will rebind the row in "normal" state
                        notifyItemChanged(mAllLists.indexOf(item));
                    }
                });

            } else {
                Log.e(TAG, "else statement");
                // we need to show the "normal" state
                holder.itemView.setBackgroundColor(Color.parseColor("#FAFAFA"));
                holder.itemCount.setVisibility(View.VISIBLE);
                holder.itemsPreview.setVisibility(View.VISIBLE);
                holder.itemCount.setVisibility(View.VISIBLE);
                // holder.titleTextView.setText(item);
                holder.undoButton.setVisibility(View.GONE);
                holder.undoButton.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return mAllLists.size();
        }

        public void addItem(Lists l) {
            mAllLists.add(l);
            notifyItemInserted(mAllLists.size() - 1);
        }

        public void updateList() {
            notifyDataSetChanged();
        }

        public void setUndoOn(boolean undoOn) {
            this.undoOn = undoOn;
        }

        public boolean isUndoOn() {
            return true;
        }



        public void pendingRemoval(int position) {
            final Lists item = mAllLists.get(position);
            if (!itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.add(item);
                // this will redraw rotw in "undo" state
                notifyItemChanged(position);
                // let's create, store and post a runnable to remove the item
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(mAllLists.indexOf(item));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
                pendingRunnables.put(item, pendingRemovalRunnable);
            }
        }

        public void remove(int position) {
            Lists item = mAllLists.get(position);
            if (itemsPendingRemoval.contains(item)) {
                itemsPendingRemoval.remove(item);
            }
            if (mAllLists.contains(item)) {
                Database.getDB().getReference("users").child(User.getUid()).child(item.getName()).removeValue();
                mSelectionIndex = position;
            }


        }

        public void removedFromDB(int position) {
            mAllLists.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            Log.e(TAG, "RemovedFromDB at position " + position);
        }


        public boolean isPendingRemoval(int position) {
            Lists item = mAllLists.get(position);
            return itemsPendingRemoval.contains(item);
        }

    }

    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(getActivity(), R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getResources().getDimension(R.dimen.activity_horizontal_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ContentAdapter contentAdapter = (ContentAdapter) recyclerView.getAdapter();
                if (contentAdapter.isUndoOn() && contentAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ContentAdapter adapter = (ContentAdapter) mRecyclerView.getAdapter();
                boolean undoOn = adapter.isUndoOn();
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }


    public void listItemPressed(Lists li) {
        if (mListener != null)
            mListener.openList(li);
    }

    public void createList(String listName) {
        Lists l = new Lists(listName);
        Database.getDB().getReference("users").child(User.getUid()).child(listName).setValue(l);
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
    public void onPause() {
        super.onPause();
        Log.v(TAG, "Pause" + mSelectionIndex);
    }

    @Override
    public void onResume() {
        Log.v(TAG, "Resume" + mSelectionIndex);
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

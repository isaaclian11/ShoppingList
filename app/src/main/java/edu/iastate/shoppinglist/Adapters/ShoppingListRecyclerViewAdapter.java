package edu.iastate.shoppinglist.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.iastate.shoppinglist.Models.ShoppingListModel;
import edu.iastate.shoppinglist.R;

public class ShoppingListRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "MainListAdapter";

    private ArrayList<ShoppingListModel> mTitles; //A list of shopping lists
    private OnMainListListener mOnMainListListener; //An interface to handle clicks

    /**
     * Constructor for an adapter of list of ShoppingLists recyclerview
     * @param mTitles Name of the each shopping list
     * @param onMainListListener - Interface to handle clicks
     */
    public ShoppingListRecyclerViewAdapter(ArrayList<ShoppingListModel> mTitles, OnMainListListener onMainListListener) {
        this.mTitles = mTitles;
        this.mOnMainListListener = onMainListListener;
    }

    /**
     * Creates a view holder object containing a row
     * @param parent
     * @param viewType
     * @return returns a ViewHolder object
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mOnMainListListener);
        return viewHolder;
    }

    /**
     * Populates the list
     * @param holder a row
     * @param position position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.mainTitles.setText(mTitles.get(position).getTitle());
    }

    /**
     * Gets the size of the list
     * @return returns the size of the list
     */
    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    /**
     * Custom ViewHolder specifically for ShoppingList
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mainTitles; //Name of the shopping list
        OnMainListListener onMainListListener; //Interface to handle clicks
        ImageView delete, duplicate; //Delete and duplicate images used as buttons

        public ViewHolder(@NonNull View itemView, final OnMainListListener onMainListListener) {
            super(itemView);
            mainTitles = itemView.findViewById(R.id.main_items_titles);
            this.onMainListListener = onMainListListener;
            delete = itemView.findViewById(R.id.delete_list);
            duplicate = itemView.findViewById(R.id.duplicate_list);

            //Handles delete onclick. The onDeleteClick is handled in the actual fragment
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onMainListListener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            onMainListListener.onDeleteClick(position);
                        }
                    }
                }
            });

            //Handles duplicate on click. The duplicateClick method is handled in the fragment
            duplicate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onMainListListener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            onMainListListener.duplicateClick(position);
                        }
                    }
                }
            });
            itemView.setOnClickListener(this);
        }

        /**
         * Handles clicks on the list
         * @param view
         */
        @Override
        public void onClick(View view) {
            onMainListListener.onMainListClick(getAdapterPosition(), view);
        }

    }

    /**
     * Custom interface to handle different clicks
     */
    public interface OnMainListListener{
        /**
         * Opens a list of items corresponding to this list
         * @param position Position of the list clicked
         * @param view
         */
        void onMainListClick(int position, View view);

        /**
         * Deletes a shopping list and all its children
         * @param position Position of the list clicked
         */
        void onDeleteClick(int position);

        /**
         * Duplicates a list
         * @param position Position of the list clicked
         */
        void duplicateClick(int position);
    }

}

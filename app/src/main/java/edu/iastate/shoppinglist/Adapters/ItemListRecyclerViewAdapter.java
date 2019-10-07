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

import edu.iastate.shoppinglist.Models.ItemListModel;
import edu.iastate.shoppinglist.R;

public class ItemListRecyclerViewAdapter extends RecyclerView.Adapter<ItemListRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "ShoppingListAdapter";

    private OnShoppingListDeleteListener listener; //Interface to handle clicks
    private ArrayList<ItemListModel> mItems; //List of items in a shopping list

    /**
     * Constructor for a shopping list recyclerview
     * @param mItems ArrayList of single item objects
     * @param listener Interface to handle on click
     */
    public ItemListRecyclerViewAdapter(ArrayList<ItemListModel> mItems,
                                       OnShoppingListDeleteListener listener) {
        this.mItems = mItems;
        this.listener = listener;
    }

    /**
     * Creates a view holder object containing a row
     * @param parent
     * @param viewType
     * @return returns a ViewHolder object
     */
    @NonNull
    @Override
    public ItemListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    /**
     * Populates the list
     * @param holder A row
     * @param position Position of the row
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.item.setText(mItems.get(position).getItem());
    }

    /**
     * Returns the size of the list
     * @return the size of the list
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Custom ViewHolder specifically for itmes in a shopping list
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView item; //An item in the shopping list
        ImageView delete; //Image that is used as a button
        OnShoppingListDeleteListener listener; //Listener used to handle onClick

        public ViewHolder(@NonNull View itemView, final OnShoppingListDeleteListener listener) {
            super(itemView);
            item = itemView.findViewById(R.id.items_titles);
            delete = itemView.findViewById(R.id.delete_item);

            //This handles the delete button in a row. The method itself is implemented in the fragment
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            this.listener = listener;
        }
    }

    /**
     * Custom interface that handles clicks on a specific object
     * on a list
     */
    public interface OnShoppingListDeleteListener{
        /**
         * Deletes an item from a list
         * @param position Position of the item to be deleted
         */
        void onDeleteClick(int position);
    }


}

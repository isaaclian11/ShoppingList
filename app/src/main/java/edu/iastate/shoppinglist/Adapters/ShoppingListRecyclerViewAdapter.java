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

    private static final String TAG = "ShoppingListAdapter";
    private OnShoppingListDeleteListener listener;

    private ArrayList<ShoppingListModel> mItems;

    /**
     * Constructor for a shopping list recyclerview
     * @param mItems ArrayList of single item objects
     * @param listener Interface to handle on click
     */
    public ShoppingListRecyclerViewAdapter(ArrayList<ShoppingListModel> mItems,
                                           OnShoppingListDeleteListener listener) {
        this.mItems = mItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShoppingListRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.item.setText(mItems.get(position).getItem());
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView item;
        ImageView delete;
        OnShoppingListDeleteListener listener;

        public ViewHolder(@NonNull View itemView, final OnShoppingListDeleteListener listener) {
            super(itemView);
            item = itemView.findViewById(R.id.items_titles);
            delete = itemView.findViewById(R.id.delete_item);
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

    public interface OnShoppingListDeleteListener{
        void onDeleteClick(int position);
    }


}

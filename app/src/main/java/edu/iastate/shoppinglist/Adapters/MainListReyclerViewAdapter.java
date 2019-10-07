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
import java.util.UUID;

import edu.iastate.shoppinglist.Models.MainListModel;
import edu.iastate.shoppinglist.R;

public class MainListReyclerViewAdapter extends RecyclerView.Adapter<MainListReyclerViewAdapter.ViewHolder>{

    private static final String TAG = "MainListAdapter";

    private ArrayList<MainListModel> mTitles = new ArrayList<>();
    private OnMainListListener mOnMainListListener;

    /**
     * Constructor for an adapter of list of ShoppingLists recyclerview
     * @param mTitles Name of the each shopping list
     * @param onMainListListener - Interface to handle clicks
     */
    public MainListReyclerViewAdapter(ArrayList<MainListModel> mTitles, OnMainListListener onMainListListener) {
        this.mTitles = mTitles;
        this.mOnMainListListener = onMainListListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mOnMainListListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        holder.mainTitles.setText(mTitles.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mainTitles;
        OnMainListListener onMainListListener;
        ImageView delete, duplicate;

        public ViewHolder(@NonNull View itemView, final OnMainListListener onMainListListener) {
            super(itemView);
            mainTitles = itemView.findViewById(R.id.main_items_titles);
            this.onMainListListener = onMainListListener;
            delete = itemView.findViewById(R.id.delete_list);
            duplicate = itemView.findViewById(R.id.duplicate_list);
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

        @Override
        public void onClick(View view) {
            onMainListListener.onMainListClick(getAdapterPosition(), view);
        }

    }

    public interface OnMainListListener{
        void onMainListClick(int position, View view);
        void onDeleteClick(int position);
        void duplicateClick(int position);
    }

}

package edu.iastate.shoppinglist.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import edu.iastate.shoppinglist.Adapters.ShoppingListRecyclerViewAdapter;
import edu.iastate.shoppinglist.Models.ShoppingListModel;
import edu.iastate.shoppinglist.Models.ItemListModel;
import edu.iastate.shoppinglist.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingList extends Fragment implements ShoppingListRecyclerViewAdapter.OnMainListListener {

    private final String key = "parent_id";
    private final String positionKey = "position";
    private final String titleKey = "title";
    private final String filename = "main_list";
    private final String globalItemList = "item_list";


    ImageView floatingActionButton; //The add button
    private ArrayList<ShoppingListModel> titles = new ArrayList<>(); //A list of shoppinglists
    private ArrayList<ItemListModel> globalItems = new ArrayList<>(); //A list of all the items in every list

    RecyclerView recyclerView; //Recyclerview for the list
    ShoppingListRecyclerViewAdapter adapter; //Adapter for the recyclerview
    EditText editText; //Name of the shopping list

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main_list, container, false);
        floatingActionButton = view.findViewById(R.id.add_title);
        editText = view.findViewById(R.id.createText);

        if(savedInstanceState==null){
            //Loads the shopping lists from storage
            loadFromFile(view.getContext(), filename);
        }

        initializeRecyclerView(view);

        /**
         * This is the add button. When clicked, it gets the text from
         * the EditText and creates a ShoppingListModel object. The object
         * is then added to the recyclerview.
         */
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String title = editText.getText().toString();
                    UUID id = UUID.randomUUID();
                    titles.add(new ShoppingListModel(id, title));
                    saveState(view.getContext(), filename, titles);
                    editText.getText().clear();
                    adapter.notifyDataSetChanged();

                    //This closes the keyboard after button is pressed
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        });

        return view;
    }

    /**
     * Initializes Recyclerview and its adapter
     * @param view View object used to get ids
     */
    private void initializeRecyclerView(View view){
        Log.d(TAG, "initializeRecyclerView: initializeRecyclerView.");
        recyclerView = view.findViewById(R.id.main_list);
        adapter = new ShoppingListRecyclerViewAdapter(titles, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    /**
     * This method opens a fragment containing the corresponding items in a shopping list
     * @param position position of the clicked list
     * @param view
     */
    @Override
    public void onMainListClick(int position, View view) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Bundle bundle = new Bundle();
        String id = titles.get(position).getId().toString();
        String listTitle = titles.get(position).getTitle();
        bundle.putString(key, id);
        bundle.putString(titleKey, listTitle);
        bundle.putInt(positionKey, position);
        Fragment shoppingList = new ItemList();
        shoppingList.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, shoppingList).addToBackStack(null).commit();

    }

    /**
     * This method deletes a shopping list and all its child items
     * @param position Position of the clicked list
     */
    @Override
    public void onDeleteClick(int position) {
        ItemList itemList = new ItemList();
        globalItems = itemList.loadFromFile(getContext(), globalItemList);
        for(int i=0; i<globalItems.size(); i++){
            if(globalItems.get(i).getParentID().equals(titles.get(position).getId())){
                globalItems.remove(i);
            }
        }
        titles.remove(position);
        adapter.notifyDataSetChanged();
        itemList.saveState(getContext(), globalItemList, globalItems);
        saveState(getContext(), filename, titles);
    }

    /**
     * This method duplicates a list and its child items
     * The new copy of the list has its own id and its children are attached to the new id
     * @param position Position of the clicked list
     */
    @Override
    public void duplicateClick(int position) {
        ArrayList<ItemListModel> duplicateList = new ArrayList<>();
        UUID id = titles.get(position).getId();
        UUID newID = UUID.randomUUID();
        ItemList itemList = new ItemList();
        globalItems = itemList.loadFromFile(getContext(), globalItemList);
        for(int i=0; i<globalItems.size();i++){
            if(globalItems.get(i).getParentID().equals(id)){
                duplicateList.add(new ItemListModel(newID, globalItems.get(i).getItem()));
            }
        }
        globalItems.addAll(duplicateList);
        itemList.saveState(getContext(), globalItemList, globalItems);
        ShoppingListModel duplicateModel = new ShoppingListModel(newID, titles.get(position).getTitle());
        titles.add(duplicateModel);
        adapter.notifyDataSetChanged();
        saveState(getContext(), filename, titles);
    }

    /**
     * Saves the shopping list array to storage
     * @param context Context
     * @param filename Saves the array to this file name
     * @param titles An array of shopping lists to be saved
     */
    public void saveState(Context context, String filename, ArrayList<ShoppingListModel> titles) {
        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(titles);
            oos.close();
            fos.close();
            Log.d("Model", "Changes written");
        } catch(Exception e) {
            Log.d("Model", "Exception on saving", e);
        }
    }

    /**
     * Loads and returns a list of shopping lists
     * @param context Context
     * @param filename Name of the file to be loaded
     * @return Returns a list of shopping lists
     */
    public ArrayList<ShoppingListModel> loadFromFile(Context context, String filename) {
        FileInputStream fos;
        ObjectInputStream oos;

        try {
            fos = new FileInputStream(context.getFileStreamPath(filename));
            oos = new ObjectInputStream(fos);
            titles = (ArrayList<ShoppingListModel>) oos.readObject();
            oos.close();
            fos.close();
        } catch(Exception e) {
            Log.d("Model", "Exception on loading", e);
        }
        return titles;
    }
}

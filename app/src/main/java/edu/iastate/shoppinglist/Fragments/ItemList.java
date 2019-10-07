package edu.iastate.shoppinglist.Fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.TextView;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import edu.iastate.shoppinglist.Adapters.ItemListRecyclerViewAdapter;
import edu.iastate.shoppinglist.Models.ShoppingListModel;
import edu.iastate.shoppinglist.Models.ItemListModel;
import edu.iastate.shoppinglist.R;


public class ItemList extends Fragment implements ItemListRecyclerViewAdapter.OnShoppingListDeleteListener {

    private static final String TAG = "ItemList";
    private final String key = "parent_id";
    private final String parentPosition = "position";
    private final String titleKey = "title";
    private final String filename = "item_list";
    private final String titleFileName = "main_list";


    private ArrayList<ItemListModel> items = new ArrayList<>(); //A list of items in the current view
    private ArrayList<ItemListModel> globalItem = new ArrayList<>(); //A list of all items in all lists
    private ArrayList<ShoppingListModel> titles = new ArrayList<>(); //A list of all the shopping lists

    ImageView floatingActionButton, editTitle; //Add and edit buttons
    RecyclerView recyclerView;
    ItemListRecyclerViewAdapter adapter;
    EditText editText, titleEditor; //Editors of the name of the shopping list
    TextView titleText; //TextView to display the name of the shopping list
    AlertDialog alertDialog; //Dialog that allows editing of the title

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.shopping_list_fragment, container, false);
        setup(view);

        /**
         * ShoppingList fragment passes on data when a shopping list is clicked.
         * The data is then received by the current fragment
         */
        Bundle bundle = getArguments();
        final String parentID = bundle.getString(key);
        final String title = bundle.getString(titleKey);
        final int position = bundle.getInt(parentPosition);

        //Sets the title to be the name of the shopping list
        titleText.setText(title);

        //Opens a dialog to enable user to edit the title when edit icon is pressed
        alertDialog.setTitle("Edit title");
        alertDialog.setView(titleEditor);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                titleText.setText(titleEditor.getText().toString());
                ShoppingList shoppingList = new ShoppingList();
                titles = shoppingList.loadFromFile(getContext(), titleFileName);
                titles.set(position, new ShoppingListModel(UUID.fromString(parentID), titleEditor.getText().toString()));
                shoppingList.saveState(getContext(), titleFileName, titles);
            }
        });

        //Handles onClick of the edit icon. Opens the alert dialog
        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditor.setText(title);
                alertDialog.show();
            }
        });

        //Converts string to UUID
        UUID id = null;
        if(parentID!=null)
            id = UUID.fromString(parentID);

        //Loads the items corresponding to a specific shopping list
        if(savedInstanceState==null){
            loadFromFile(view.getContext(), filename);
            for(int i=0; i<globalItem.size(); i++){
                if(globalItem.get(i).getParentID().equals(id)){
                    items.add(globalItem.get(i));
                }
            }
        }

        initializeRecyclerView(view);

        final UUID finalId = id;

        //Adds a new item to the shopping list
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String item = editText.getText().toString();
                    if (item.isEmpty())
                        return;
                    ItemListModel itemListModel = new ItemListModel(finalId, item);
                    editText.getText().clear();
                    items.add(itemListModel);
                    globalItem.add(itemListModel);
                    adapter.notifyDataSetChanged();
                    saveState(view.getContext(), filename, globalItem);

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
     * Sets up the recyclerview and its adapter
     * @param view
     */
    private void initializeRecyclerView(View view){
        Log.d(TAG, "initializeRecyclerView: initializeRecyclerView.");
        recyclerView = view.findViewById(R.id.main_list);
        adapter = new ItemListRecyclerViewAdapter(items, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    /**
     * Initializes views
     * @param view
     */
    private void setup(View view){
        floatingActionButton = view.findViewById(R.id.add_item_button);
        editText = view.findViewById(R.id.add_item);
        titleText = view.findViewById(R.id.shopping_list_title);
        editTitle = view.findViewById(R.id.edit_title);
        titleEditor = new EditText(getContext());
        alertDialog = new AlertDialog.Builder(getContext()).create();
    }

    /**
     * Saves the items to a storage
     * @param context Context
     * @param filename Saves the array to this file name
     * @param items An array of items to be saved
     */
    public void saveState(Context context, String filename, ArrayList<ItemListModel> items) {
        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(items);
            oos.close();
            fos.close();
            Log.d("Model", "Changes written");
        } catch(Exception e) {
            Log.d("Model", "Exception on saving", e);
        }
    }

    /**
     * Loads and returns a list of items
     * @param context Context
     * @param filename Name of the file to be loaded
     * @return Returns a list of items
     */
    public ArrayList<ItemListModel> loadFromFile(Context context, String filename) {
        FileInputStream fos;
        ObjectInputStream oos;

        try {
            fos = new FileInputStream(context.getFileStreamPath(filename));
            oos = new ObjectInputStream(fos);
            globalItem = (ArrayList<ItemListModel>) oos.readObject();
            oos.close();
            fos.close();
        } catch(Exception e) {
            Log.d("Model", "Exception on loading", e);
        }
        return globalItem;
    }

    /**
     * Deletes a row from the list
     * @param position Position of the row to be deleted
     */
    @Override
    public void onDeleteClick(int position) {
        ItemListModel model = items.get(position);
        for(int i=0; i<globalItem.size(); i++){
            if(globalItem.get(i).equals(model))
                globalItem.remove(i);
        }
        items.remove(position);
        saveState(getContext(), filename, globalItem);
        adapter.notifyDataSetChanged();
    }
}

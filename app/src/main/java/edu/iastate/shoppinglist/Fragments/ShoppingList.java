package edu.iastate.shoppinglist.Fragments;


import android.content.Context;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import edu.iastate.shoppinglist.Adapters.ShoppingListRecyclerViewAdapter;
import edu.iastate.shoppinglist.Models.ShoppingListModel;
import edu.iastate.shoppinglist.R;


public class ShoppingList extends Fragment implements ShoppingListRecyclerViewAdapter.OnShoppingListDeleteListener {

    private static final String TAG = "ShoppingList";
    private final String key = "parent_id";
    private final String titleKey = "title";
    private final String filename = "item_list";

    private ArrayList<ShoppingListModel> items = new ArrayList<>();
    private ArrayList<ShoppingListModel> globalItem = new ArrayList<>();
    ImageView floatingActionButton;
    RecyclerView recyclerView;
    ShoppingListRecyclerViewAdapter adapter;
    EditText editText;
    TextView titleText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.shopping_list_fragment, container, false);
        floatingActionButton = view.findViewById(R.id.add_item_button);
        editText = view.findViewById(R.id.add_item);
        titleText = view.findViewById(R.id.shopping_list_title);

        Bundle bundle = getArguments();
        final String parentID = bundle.getString(key);
        final String title = bundle.getString(titleKey);

        titleText.setText(title);

        UUID id = null;
        if(parentID!=null)
            id = UUID.fromString(parentID);

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
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = editText.getText().toString();
                if(item.isEmpty())
                    return;
                ShoppingListModel shoppingListModel = new ShoppingListModel(finalId, item);
                editText.getText().clear();
                items.add(shoppingListModel);
                globalItem.add(shoppingListModel);
                adapter.notifyDataSetChanged();
                saveState(view.getContext(), filename, globalItem);

                //This closes the keyboard after button is pressed
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        return view;
    }

    private void initializeRecyclerView(View view){
        Log.d(TAG, "initializeRecyclerView: initializeRecyclerView.");
        recyclerView = view.findViewById(R.id.main_list);
        adapter = new ShoppingListRecyclerViewAdapter(items, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void saveState(Context context, String filename, ArrayList<ShoppingListModel> globalItem) {
        FileOutputStream fos;
        ObjectOutputStream oos;

        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(globalItem);
            oos.close();
            fos.close();
            Log.d("Model", "Changes written");
        } catch(Exception e) {
            Log.d("Model", "Exception on saving", e);
        }
    }

    public ArrayList<ShoppingListModel> loadFromFile(Context context, String filename) {
        FileInputStream fos;
        ObjectInputStream oos;

        try {
            fos = new FileInputStream(context.getFileStreamPath(filename));
            oos = new ObjectInputStream(fos);
            globalItem = (ArrayList<ShoppingListModel>) oos.readObject();
            oos.close();
            fos.close();
        } catch(Exception e) {
            Log.d("Model", "Exception on loading", e);
        }
        return globalItem;
    }

    @Override
    public void onDeleteClick(int position) {
        ShoppingListModel model = items.get(position);
        for(int i=0; i<globalItem.size(); i++){
            if(globalItem.get(i).equals(model))
                globalItem.remove(i);
        }
        items.remove(position);
        saveState(getContext(), filename, globalItem);
        adapter.notifyDataSetChanged();
    }
}

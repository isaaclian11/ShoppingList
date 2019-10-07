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

import edu.iastate.shoppinglist.Adapters.MainListReyclerViewAdapter;
import edu.iastate.shoppinglist.Models.MainListModel;
import edu.iastate.shoppinglist.Models.ShoppingListModel;
import edu.iastate.shoppinglist.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainList extends Fragment implements MainListReyclerViewAdapter.OnMainListListener {

    private final String key = "parent_id";
    private final String positionKey = "position";
    private final String titleKey = "title";
    private final String filename = "main_list";
    private final String globalItemList = "item_list";


    ImageView floatingActionButton;
    private ArrayList<MainListModel> titles = new ArrayList<>();
    private ArrayList<ShoppingListModel> globalItems = new ArrayList<>();

    RecyclerView recyclerView;
    MainListReyclerViewAdapter adapter;
    EditText editText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main_list, container, false);
        floatingActionButton = view.findViewById(R.id.add_title);
        editText = view.findViewById(R.id.createText);

        if(savedInstanceState==null){
            loadFromFile(view.getContext(), filename);
        }

        initializeRecyclerView(view);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String title = editText.getText().toString();
                    UUID id = UUID.randomUUID();
                    titles.add(new MainListModel(id, title));
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

    private void initializeRecyclerView(View view){
        Log.d(TAG, "initializeRecyclerView: initializeRecyclerView.");
        recyclerView = view.findViewById(R.id.main_list);
        adapter = new MainListReyclerViewAdapter(titles, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onMainListClick(int position, View view) {

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Bundle bundle = new Bundle();
        String id = titles.get(position).getId().toString();
        String listTitle = titles.get(position).getTitle();
        bundle.putString(key, id);
        bundle.putString(titleKey, listTitle);
        bundle.putInt(positionKey, position);
        Fragment shoppingList = new ShoppingList();
        shoppingList.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, shoppingList).addToBackStack(null).commit();

    }

    @Override
    public void onDeleteClick(int position) {
        ShoppingList shoppingList = new ShoppingList();
        globalItems = shoppingList.loadFromFile(getContext(), globalItemList);
        for(int i=0; i<globalItems.size(); i++){
            if(globalItems.get(i).getParentID().equals(titles.get(position).getId())){
                globalItems.remove(i);
            }
        }
        titles.remove(position);
        adapter.notifyDataSetChanged();
        shoppingList.saveState(getContext(), globalItemList, globalItems);
        saveState(getContext(), filename, titles);
    }

    @Override
    public void duplicateClick(int position) {
        ArrayList<ShoppingListModel> duplicateList = new ArrayList<>();
        UUID id = titles.get(position).getId();
        UUID newID = UUID.randomUUID();
        ShoppingList shoppingList = new ShoppingList();
        globalItems = shoppingList.loadFromFile(getContext(), globalItemList);
        for(int i=0; i<globalItems.size();i++){
            if(globalItems.get(i).getParentID().equals(id)){
                duplicateList.add(new ShoppingListModel(newID, globalItems.get(i).getItem()));
            }
        }
        globalItems.addAll(duplicateList);
        shoppingList.saveState(getContext(), globalItemList, globalItems);
        MainListModel duplicateModel = new MainListModel(newID, titles.get(position).getTitle());
        titles.add(duplicateModel);
        adapter.notifyDataSetChanged();
        saveState(getContext(), filename, titles);
    }

    public void saveState(Context context, String filename, ArrayList<MainListModel> titles) {
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

    public ArrayList<MainListModel> loadFromFile(Context context, String filename) {
        FileInputStream fos;
        ObjectInputStream oos;

        try {
            fos = new FileInputStream(context.getFileStreamPath(filename));
            oos = new ObjectInputStream(fos);
            titles = (ArrayList<MainListModel>) oos.readObject();
            oos.close();
            fos.close();
        } catch(Exception e) {
            Log.d("Model", "Exception on loading", e);
        }
        return titles;
    }
}

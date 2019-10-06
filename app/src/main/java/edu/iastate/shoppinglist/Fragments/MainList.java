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
                String title = editText.getText().toString();
                if(title.isEmpty())
                    return;
                UUID id = UUID.randomUUID();
                titles.add(new MainListModel(id, title));
                saveState(view.getContext(), filename);
                editText.getText().clear();
                adapter.notifyDataSetChanged();

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
        adapter = new MainListReyclerViewAdapter(titles, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onMainListClick(int position, UUID parentID, String title, View view) {

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Bundle bundle = new Bundle();
        bundle.putString(key, parentID.toString());
        bundle.putString(titleKey, title);
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
        saveState(getContext(), filename);
    }

    public void saveState(Context context, String filename) {
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

    public void loadFromFile(Context context, String filename) {
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
    }
}

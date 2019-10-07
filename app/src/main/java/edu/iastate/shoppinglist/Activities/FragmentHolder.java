package edu.iastate.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.iastate.shoppinglist.Fragments.ShoppingList;
import edu.iastate.shoppinglist.R;

public class FragmentHolder extends AppCompatActivity{
    ShoppingList shoppingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_holder);
        shoppingList = new ShoppingList();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, shoppingList).commit();
    }

}

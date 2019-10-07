package edu.iastate.shoppinglist.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import java.util.UUID;

import edu.iastate.shoppinglist.Adapters.FragmentAdapter;
import edu.iastate.shoppinglist.Fragments.MainList;
import edu.iastate.shoppinglist.Fragments.ShoppingList;
import edu.iastate.shoppinglist.R;

public class FragmentHolder extends AppCompatActivity{
    MainList mainList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_holder);
        mainList = new MainList();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,mainList).commit();
    }

}

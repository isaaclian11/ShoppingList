package edu.iastate.shoppinglist.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>(); //List of fragments

    /**
     * Constructor for fragment adapter
     * @param fm Fragment manager
     * @param behavior Indicates whether this fragment will be the only one to be in Resumed state
     */
    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    /**
     * Gets a fragment from the array list
     * @param position position of the fragment in the array list
     * @return returns a fragment
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    /**
     * Gets the size of the array list of fragments
     * @return the size of the array list of fragments
     */
    @Override
    public int getCount() {
        return mFragments.size();
    }
}

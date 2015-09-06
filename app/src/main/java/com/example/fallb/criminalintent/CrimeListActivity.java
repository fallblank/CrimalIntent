package com.example.fallb.criminalintent;

import android.support.v4.app.Fragment;

import fragments.CrimeListFragment;

/**
 * Created by fallb on 2015/8/28.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}

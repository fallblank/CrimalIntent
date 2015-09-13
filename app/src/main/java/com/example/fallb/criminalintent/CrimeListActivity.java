package com.example.fallb.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import fragments.CrimeFragment;
import fragments.CrimeListFragment;
import models.Crime;

/**
 * Created by fallb on 2015/8/28.
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks,CrimeFragment.Callbacks {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSeleted(Crime crime) {
        if(findViewById(R.id.detailFragmentContainer)==null){
            Intent i = new Intent(this,CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID,crime.getId());
            startActivity(i);
        }else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldFragment = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newFragment = CrimeFragment.newInstance(crime.getId());

            if(oldFragment!=null){
                ft.remove(oldFragment);
            }
            ft.add(R.id.detailFragmentContainer,newFragment);
            ft.commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}

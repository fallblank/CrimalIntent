package com.example.fallb.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import fragments.CrimeFragment;
import models.Crime;
import models.CrimeLab;

/**
 * Created by fallb on 2015/8/29.
 */
public class CrimePagerActivity extends FragmentActivity implements CrimeFragment.Callbacks {

    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create layout by code
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        Iterator iterator = mCrimes.iterator();
        int i=0;
        while(iterator.hasNext()){
           Crime crime = (Crime) iterator.next();
            if(crime.getId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }else {
                i++;
            }
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Crime crime = mCrimes.get(position);
                if(crime.getTitle() != null){
                    setTitle(crime.getTitle());
                    Toast.makeText(getApplicationContext(),crime.getTitle(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return;

    }

    @Override
    public void onCrimeUpdated(Crime crime) {
    }
}

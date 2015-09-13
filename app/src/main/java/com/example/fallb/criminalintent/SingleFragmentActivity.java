package com.example.fallb.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by fallb on 2015/8/28.
 * Desription:this is a super activity class,it can hold a fragment
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    //sub class must override this method,so the activity can knoew which fragment it wants hold
    protected abstract Fragment createFragment();

    //To provide own layout,sub-class must override this method.
    protected int getLayoutResId(){
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment==null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }
}

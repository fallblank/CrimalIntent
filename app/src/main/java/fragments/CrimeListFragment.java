package fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fallb.criminalintent.CrimePagerActivity;
import com.example.fallb.criminalintent.R;

import java.util.ArrayList;

import javax.security.auth.callback.Callback;

import models.Crime;
import models.CrimeLab;

/**
 * Created by fallb on 2015/8/28.
 */
public class CrimeListFragment extends Fragment {
    //this is a request code,which can unique distinguish others
    private static final int REQUEST_CODE = 0;
    private boolean mSubtitleVisible;
    private ListView mCrimeList;
    private ArrayList<Crime> mCrimes;
    private Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeSeleted(Crime crime);
    }

    public void updateUI(){
        ((CrimeAdapter)(mCrimeList.getAdapter())).notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //notify fragment manager call fragment.createOptionMenu()
        setHasOptionsMenu(true);
        getActivity().setTitle(getString(R.string.crime_title));
        mCrimes = CrimeLab.get(getActivity()).getCrimes();
        //remain fragment instance
        setRetainInstance(true);
        mSubtitleVisible = false;
    }

    @TargetApi(11)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeList = (ListView) v.findViewById(R.id.list);
        CrimeAdapter adapter = new CrimeAdapter(mCrimes);
        mCrimeList.setAdapter(adapter);
        View emptyView = v.findViewById(R.id.empty);
        mCrimeList.setEmptyView(emptyView);
        mCrimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Crime crime = (Crime) adapterView.getAdapter().getItem(i);

                //start activity from fragment
                mCallbacks.onCrimeSeleted(crime);
                return;
            }
        });
        Button addCrime = (Button) v.findViewById(R.id.add_record_buttun);
        addCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
                startActivityForResult(i, 0);
                return;
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mSubtitleVisible) {
            getActivity().getActionBar().setSubtitle(R.string.subtitle);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //use floating context menu on lower sdk version
            registerForContextMenu(mCrimeList);
        } else {
            //use contextual action bar to show context menu on higher sdk version
            mCrimeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mCrimeList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                //this method will ba called back on item selected state changed
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                }

                //the following four methods is defined on ActionMode.Callbace
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater menuInflater = actionMode.getMenuInflater();
                    menuInflater.inflate(R.menu.crime_list_item_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_delete_crime:
                            CrimeAdapter crimeAdapter = (CrimeAdapter) mCrimeList.getAdapter();
                            CrimeLab crimeLab = CrimeLab.get(getActivity());
                            for (int i = crimeAdapter.getCount() - 1; i >= 0; i--){
                                if(mCrimeList.isItemChecked(i)){
                                    crimeLab.removeCrime(crimeAdapter.getItem(i).getId());
                                }
                            }
                            actionMode.finish();
                            crimeAdapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {

                }
            });
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        //update list
        ((CrimeAdapter) mCrimeList.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).savaCrimes();
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            }
            //there is a command error:
            //after each loop,the value of mTitleTextView possess different values,so you can't made
            //mTitleTextView a global variable to reduce consumption of memory
            Crime c = getItem(position);
            TextView mTitleTextView = (TextView) convertView.findViewById(R.id.crime_list_item_title_text_view);
            mTitleTextView.setText(c.getTitle());
            TextView mDateTextView = (TextView) convertView.findViewById(R.id.crime_list_item_date_text_view);
            mDateTextView.setText(CrimeFragment.formatDate(c.getDate(), "EEEE,LLL d,yyyy"));
            CheckBox mSolvedCheckBox = (CheckBox) convertView.findViewById(R.id.crime_list_item_solved_check_box);
            mSolvedCheckBox.setChecked(c.isSolved());
            return convertView;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "data changed!", Toast.LENGTH_SHORT).show();
        }
        return;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem showSubtitle = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible && showSubtitle != null) {
            showSubtitle.setTitle(R.string.hide_subtitle);
        }
        return;
    }

    @TargetApi(11)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                ((CrimeAdapter)mCrimeList.getAdapter()).notifyDataSetChanged();
                mCallbacks.onCrimeSeleted(crime);
                return true;
            case R.id.menu_item_show_subtitle:
                if (getActivity().getActionBar().getSubtitle() == null) {
                    getActivity().getActionBar().setSubtitle(R.string.subtitle);
                    item.setTitle(R.string.hide_subtitle);
                    mSubtitleVisible = true;
                } else {
                    getActivity().getActionBar().setSubtitle(null);
                    item.setTitle(R.string.show_subtitle);
                    mSubtitleVisible = false;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Create context menu
    //By checking view's ID,we can decide which context menu will be inflated
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    //response context menu item selected
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        CrimeAdapter crimeAdapter = (CrimeAdapter) mCrimeList.getAdapter();
        Crime crime = crimeAdapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).removeCrime(crime.getId());
                crimeAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }
}

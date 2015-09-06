package fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fallb.criminalintent.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import models.Crime;
import models.CrimeLab;

/**
 * Created by fallb on 2015/8/27.
 */
public class CrimeFragment extends Fragment {

    private final static String TAG = "CrimeFragment";
    public final static String EXTRA_CRIME_ID = "fragments.crime_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mDeleteButton;

    public final static CrimeFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, uuid);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "f-oncreate");
        UUID crimeId = (UUID) getArguments().getSerializable(CrimeFragment.EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        //set back icon
        setHasOptionsMenu(true);
        return;
    }

    //Build view through this method,then return the view to activity which manager it.
    @TargetApi(11)
    @Nullable //param can be null
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //show navigation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        View v = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());

                //notify data changed
                getActivity().setResult(Activity.RESULT_OK, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
                return;
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
                //notify data changed
                getActivity().setResult(Activity.RESULT_OK, null);
                return;
            }
        });
        mDeleteButton = (Button)v.findViewById(R.id.crime_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID crimeId = (UUID) getArguments().getSerializable(CrimeFragment.EXTRA_CRIME_ID);
                if(CrimeLab.get(getActivity()).removeCrime(crimeId)){
                    Toast.makeText(getActivity(),"Delete successfully",Toast.LENGTH_SHORT).show();
                    if (NavUtils.getParentActivityName(getActivity()) != null) {
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }
                }
                return;
            }
        });

        return v;
    }

    public static String formatDate(Date date, String fromatString) {
        DateFormat format = new SimpleDateFormat(fromatString);
        return format.format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        return;
    }

    private void updateDate() {
        mDateButton.setText(formatDate(mCrime.getDate(), "EEEE,LLL d,yyyy"));
        return;
    }

    //respose user click back icon


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //method one,bue it will not reserve data to list fragment
               /* Intent i = new Intent(getActivity(),CrimeListFragment.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                getActivity().finish();
                */
                //method two
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).savaCrimes();
    }
}
package fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fallb.criminalintent.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import models.Crime;
import models.CrimeLab;
import models.Photo;
import utils.PhotoUtils;

/**
 * Created by fallb on 2015/8/27.
 */
public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "fragments.crime_id";
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_DATE = "date";
    public static final String DIALOG_IMAGE = "image";

    private static final int REQUEST_DATE = 100;
    private static final int REQUEST_PHOTO = 200;
    private static final int REQUEST_CONTACT = 300;


    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mDeleteButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mImageFile;
    private Button mSuspectChooser;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                mCallbacks.onCrimeUpdated(mCrime);
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
                dialog.show(fm, DIALOG_IMAGE);
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
                mCallbacks.onCrimeUpdated(mCrime);
                getActivity().setResult(Activity.RESULT_OK, null);
                return;
            }
        });
        mDeleteButton = (Button) v.findViewById(R.id.crime_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID crimeId = (UUID) getArguments().getSerializable(CrimeFragment.EXTRA_CRIME_ID);
                if (CrimeLab.get(getActivity()).removeCrime(crimeId)) {
                    Toast.makeText(getActivity(), "Delete successfully", Toast.LENGTH_SHORT).show();
                    if (NavUtils.getParentActivityName(getActivity()) != null) {
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }
                    mCallbacks.onCrimeUpdated(mCrime);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    CrimeFragment fragment = (CrimeFragment) fm.findFragmentById(R.id.detailFragmentContainer);
                    fm.beginTransaction().remove(fragment).commit();
                }
                return;
            }
        });
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //capture picture through a camera application
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    if (mImageFile == null) {
                        try {
                            mImageFile = PhotoUtils.getImageFile(getActivity(), mCrime.getId().toString());
                        } catch (IOException e) {
                            Log.e(TAG, "error in getting image file", e);
                        }
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageFile));
                    startActivityForResult(takePictureIntent, REQUEST_PHOTO);
                }
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_image_view);
        if (mCrime.getPhoto() != null) {
            try {
                mImageFile = PhotoUtils.getImageFile(getActivity(), mCrime.getPhoto().getFileName());
            } catch (IOException e) {
                Log.e(TAG, "Error in getting photo file path", e);
            }
            if (mImageFile != null) {
                showPhoto();
            }
        }
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Photo photo = mCrime.getPhoto();
                if (photo == null) {
                    Toast.makeText(getActivity(), "please take a photo first!", Toast.LENGTH_SHORT).show();
                    return;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PhotoFragment.newInstance(mImageFile.getAbsolutePath()).show(fm, DIALOG_IMAGE);
            }
        });

        Button sendReport = (Button) v.findViewById(R.id.crime_report_button);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_suspect));
                i = Intent.createChooser(i, getString(R.string.send_report));
                if (canStartIntent(i)) {
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "System don't support", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSuspectChooser = (Button) v.findViewById(R.id.crime_suspect_button);
        mSuspectChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                if (canStartIntent(i)) {
                    startActivityForResult(i, REQUEST_CONTACT);
                } else {
                    Toast.makeText(getActivity(), "System don't support", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectChooser.setText(mCrime.getSuspect());
        }
        Button dialButton = (Button) v.findViewById(R.id.crime_dial_button);
        dialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri number = Uri.parse("tel:10086");
                Intent i = new Intent(Intent.ACTION_DIAL, number);
                startActivity(i);
            }
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        PhotoUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_DATE:
                if (resultCode == Activity.RESULT_OK) {
                    Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                    mCrime.setDate(date);
                    mCallbacks.onCrimeUpdated(mCrime);
                    updateDate();
                }
                break;
            case REQUEST_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Photo photo = new Photo(mCrime.getId().toString());
                    mCrime.setPhoto(photo);
                    mCallbacks.onCrimeUpdated(mCrime);
                    showPhoto();
                }
                break;
            case REQUEST_CONTACT:
                Uri uri = data.getData();
                String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
                Cursor c = getActivity().getContentResolver().query(uri, queryFields, null, null, null);
                if (c.getCount() == 0) {
                    c.close();
                    return;
                }
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mCallbacks.onCrimeUpdated(mCrime);
                mSuspectChooser.setText(suspect);
                c.close();
                break;
            default:
                break;
        }
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

    public static String formatDate(Date date, String fromatString) {
        DateFormat format = new SimpleDateFormat(fromatString);
        return format.format(date);
    }

    private void updateDate() {
        mDateButton.setText(formatDate(mCrime.getDate(), "EEEE,LLL d,yyyy"));
        return;
    }

    private void showPhoto() {
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            b = PhotoUtils.getScaleDrawable(getActivity(), mImageFile.getAbsolutePath());
            mPhotoView.setImageDrawable(b);
        }
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_resolved);
        } else {
            solvedString = getString(R.string.crime_report_unresolved);
        }
        String dateString = formatDate(mCrime.getDate(), "EEE,MMM dd");
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        Log.d(TAG, "report:" + report);
        return report;
    }

    private boolean canStartIntent(Intent intent) {
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        boolean isIntentSafa = activities.size() > 0;
        return isIntentSafa;

    }

}

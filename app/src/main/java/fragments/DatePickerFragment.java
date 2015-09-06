package fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.fallb.criminalintent.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import utils.UtilsHelper;

/**
 * Through this fragment,we can set date
 * Created by fallb on 2015/8/30.
 */
public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "fragments.date";
    private DatePicker datePicker;
    private Date mDate;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(DatePickerFragment.EXTRA_DATE);

        //Set the dataPicker view's beginning day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null);

        datePicker = (DatePicker) v.findViewById(R.id.dialog_date_datePicker);

        //there is a bug on miui system,it will not execute onDateChanged()
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                //translate user selected date
                mDate = new GregorianCalendar(year, month, day).getTime();
                //update args
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    //Because miui can't execute ondateChanged(),so I implement that by this way.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (UtilsHelper.isMiui()) {
                            int month = datePicker.getMonth();
                            int year = datePicker.getYear();
                            int day = datePicker.getDayOfMonth();
                            mDate = new GregorianCalendar(year, month, day).getTime();
                            getArguments().putSerializable(EXTRA_DATE, mDate);
                        }
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .setView(v)
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        return;
    }
}

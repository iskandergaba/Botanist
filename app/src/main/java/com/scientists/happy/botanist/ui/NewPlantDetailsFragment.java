package com.scientists.happy.botanist.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.scientists.happy.botanist.R;
import com.scientists.happy.botanist.data.DatabaseManager;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class NewPlantDetailsFragment extends Fragment {
    private GregorianCalendar mBirthday;
    private Bitmap mBitmap;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_new_plant_details, container, false);
        final String species = getArguments().getString("species");
        // Inflate the layout for this fragment
        final TextView speciesTextView = rootView.findViewById(R.id.species_text_view);
        speciesTextView.setText(species);
        final EditText nameEditText = rootView.findViewById(R.id.name_edit_text);
        final EditText heightEditText = rootView.findViewById(R.id.height_edit_text);
        final EditText birthdayEditText = rootView.findViewById(R.id.birthday_edit_text);
        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                showDatePicker(birthdayEditText);
            }
        });
        birthdayEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            /**
             * Handle focus change between views
             * @param v - current view
             * @param hasFocus - whether the current view has focus
             */
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker(birthdayEditText);
                }
            }
        });

        final ImageView picture = rootView.findViewById(R.id.picture);
        picture.setOnClickListener(new View.OnClickListener() {
            /**
             * Handle click event in the picture
             * @param v - the current view
             */
            @Override
            public void onClick(View v) {
                final PickSetup setup = new PickSetup().setSystemDialog(true);
                PickImageDialog.build(setup).setOnPickResult(new IPickResult() {
                    /**
                     * Handle the selected result
                     * @param r - the selection result
                     */
                    @Override
                    public void onPickResult(PickResult r) {
                        mBitmap = r.getBitmap();
                        picture.setImageBitmap(mBitmap);
                    }
                }).show(getFragmentManager());
            }
        });
        mBirthday = new GregorianCalendar();

        final Button addPlantButton = rootView.findViewById(R.id.add_plant_button);
        addPlantButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatabaseManager databaseManager = DatabaseManager.getInstance();
                String height = heightEditText.getText().toString();
                boolean isAddSuccessful = databaseManager.addPlant(getActivity(), nameEditText.getText().toString(),
                        species, mBirthday.getTimeInMillis(),
                        height.isEmpty() ? 0 : Double.parseDouble(height), mBitmap);
                if (isAddSuccessful) {
                    getActivity().finish();
                }
            }
        });
        return rootView;
    }

    /**
     * Show the date picker
     */
    private void showDatePicker(final EditText birthdayEditText) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            /**
             * Handle selected time for birthday
             * @param view - the current view
             * @param year - the selected year
             * @param monthOfYear - the selected month
             * @param dayOfMonth - the selected day
             */
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                mBirthday.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                birthdayEditText.setText(dateFormat.format(mBirthday.getTime()));
            }
        };
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(listener,
                mBirthday.get(Calendar.YEAR), mBirthday.get(Calendar.MONTH),
                mBirthday.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setTitle(getActivity().getString(R.string.set_birthday));
        datePickerDialog.vibrate(false);
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setOnDateSetListener(listener);
        datePickerDialog.show(getActivity().getFragmentManager(), "date_picker");
    }
}
package com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorActivity;
import com.example.daniel.jobster.R;

public class ProfileCreatorStepTwoFragment extends Fragment
{
    private EditText phoneNumberField, cityField;
    private Spinner stateSpinner;
    private DatePicker birthdayPicker;

    public ProfileCreatorStepTwoFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.profile_creator_step_two_fragment, container, false);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        initResources(view);
        loadSavedData();
    }

    // save data on fragment destroy
    @Override
    public void onDestroy()
    {
        saveData();
        super.onDestroy();
    }

    // init resources
    private void initResources(View view)
    {
        // init birthday picker
        birthdayPicker = view.findViewById(R.id.profile_creator__birthday_picker);

        // init state spinner
        stateSpinner = view.findViewById(R.id.profile_creator__state_spinner);

        // init input fields
        cityField = view.findViewById(R.id.profile_creator__city_field);
        phoneNumberField = view.findViewById(R.id.profile_creator__phone_number_field);
    }

    // save input data into Profile object
    private void saveData()
    {
        ProfileCreatorActivity.profile.setBirthday(getDate(birthdayPicker));
        ProfileCreatorActivity.profile.setState(stateSpinner.getSelectedItem().toString().trim());
        ProfileCreatorActivity.profile.setCity(cityField.getText().toString().trim());
        ProfileCreatorActivity.profile.setPhoneNumber(phoneNumberField.getText().toString().trim());
    }

    // load input data from Profile object
    private void loadSavedData()
    {
        // set birthday
        if(ProfileCreatorActivity.profile.getBirthday() != null)
        {
            int year = Integer.valueOf(ProfileCreatorActivity.profile.getBirthday().substring(0, 4));
            int month = Integer.valueOf(ProfileCreatorActivity.profile.getBirthday().substring(5, 7)) - 1;
            int day = Integer.valueOf(ProfileCreatorActivity.profile.getBirthday().substring(8, 10));
            birthdayPicker.updateDate(year, month, day);
        }

        // set state
        stateSpinner.setSelection(getIndexOfState(ProfileCreatorActivity.profile.getState()));

        // set city and phone number
        cityField.setText(ProfileCreatorActivity.profile.getCity());
        phoneNumberField.setText(ProfileCreatorActivity.profile.getPhoneNumber());
    }

    // returns index of specified state in spinner
    private int getIndexOfState(String state)
    {
        for(int i = 0; i < stateSpinner.getCount(); i++)
            if(stateSpinner.getItemAtPosition(i).toString().equals(state))
                return i;

        return 0;
    }

    // returns date from DatePicker in 0000-00-00 format as a string
    private String getDate(DatePicker date)
    {
        StringBuilder formattedDate = new StringBuilder();

        // append year
        formattedDate.append(String.valueOf(date.getYear()) + "-");

        // append month
        if(String.valueOf(date.getMonth()).length() == 1)
            formattedDate.append("0" + String.valueOf(date.getMonth() + 1) + "-");
        else
            formattedDate.append(String.valueOf(date.getMonth()+ 1) + "-");

        // append day
        if(String.valueOf(date.getDayOfMonth()).length() == 1)
            formattedDate.append("0" + String.valueOf(date.getDayOfMonth()));
        else
            formattedDate.append(String.valueOf(date.getDayOfMonth()));

        return formattedDate.toString();
    }

}

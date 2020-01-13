package com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorActivity;
import com.example.daniel.jobster.R;

public class ProfileCreatorStepOneFragment extends Fragment
{
    private EditText nameField, surnameField;
    private ImageButton maleButton, femaleButton;

    public ProfileCreatorStepOneFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.profile_creator_step_one_fragment, container, false);
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
        // init male button
        maleButton = view.findViewById(R.id.profile_creator__male_button);
        maleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                maleButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                femaleButton.setBackgroundColor(android.R.drawable.btn_default);

                ProfileCreatorActivity.profile.setGender(1);
            }
        });

        // init female button
        femaleButton = view.findViewById(R.id.profile_creator__female_button);
        femaleButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                femaleButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                maleButton.setBackgroundColor(android.R.drawable.btn_default);

                ProfileCreatorActivity.profile.setGender(2);
            }
        });

        // init input fields
        nameField = view.findViewById(R.id.profile_creator__name_field);
        surnameField = view.findViewById(R.id.profile_creator__surname_field);
    }

    // save input data into Profile object
    private void saveData()
    {
        ProfileCreatorActivity.profile.setName(nameField.getText().toString().trim());
        ProfileCreatorActivity.profile.setSurname(surnameField.getText().toString().trim());
    }

    // load input data from Profile object
    private void loadSavedData()
    {
        // select gender if it was previously chosen
        if(ProfileCreatorActivity.profile.getGender() != null)
        {
            if (ProfileCreatorActivity.profile.getGender() == 1)
                maleButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
            else if (ProfileCreatorActivity.profile.getGender() == 2)
                femaleButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
        }

        // set name and surname
        nameField.setText(ProfileCreatorActivity.profile.getName());
        surnameField.setText(ProfileCreatorActivity.profile.getSurname());
    }
}

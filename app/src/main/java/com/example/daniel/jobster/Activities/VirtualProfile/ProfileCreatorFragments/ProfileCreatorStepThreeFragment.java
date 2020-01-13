package com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorActivity;
import com.example.daniel.jobster.R;

public class ProfileCreatorStepThreeFragment extends Fragment
{
    private EditText hobbyField, descriptionField;

    public ProfileCreatorStepThreeFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View fragmentView = inflater.inflate(R.layout.profile_creator_step_three_fragment, container, false);

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
        // init input fields
        hobbyField = view.findViewById(R.id.profile_creator__hobby_field);
        descriptionField = view.findViewById(R.id.profile_creator__description_field);
    }

    // save input data into Profile object
    public void saveData()
    {
        ProfileCreatorActivity.profile.setHobby(hobbyField.getText().toString().trim());
        ProfileCreatorActivity.profile.setDescription(descriptionField.getText().toString().trim());
    }

    // load input data from Profile object
    private void loadSavedData()
    {
        // set hobby and description
        hobbyField.setText(ProfileCreatorActivity.profile.getHobby());
        descriptionField.setText(ProfileCreatorActivity.profile.getDescription());
    }
}

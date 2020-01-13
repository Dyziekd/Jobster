package com.example.daniel.jobster.Activities.VirtualProfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Activities.Menu.EmployeeMenuActivity;
import com.example.daniel.jobster.Activities.Menu.EmployerMenuActivity;
import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorFragments.ProfileCreatorStepOneFragment;
import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorFragments.ProfileCreatorStepThreeFragment;
import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorFragments.ProfileCreatorStepTwoFragment;
import com.example.daniel.jobster.DataModels.Profile;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileCreatorActivity extends AppCompatActivity
{
    private int currentStep;
    public static Profile profile;

    private ImageButton previousButton, nextButton;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creator);

        initResources();

        // create new profile object to save data
        profile = new Profile();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue != null)
            requestQueue.cancelAll(RestStrings.PROFILE_CREATOR_REQUEST_TAG);
    }

    // init resources
    private void initResources()
    {
        // hide action bar
        getSupportActionBar().hide();

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // init buttons
        previousButton = findViewById(R.id.profile_creator__back_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousStep();
            }
        });
        previousButton.setVisibility(View.GONE);

        nextButton = findViewById(R.id.profile_creator__next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                nextStep();
            }
        });

        // show first step fragment
        currentStep = 1;
        switchFragment();

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    // back to previous step
    private void previousStep()
    {
        currentStep--;

        if(currentStep < 3 && nextButton.getVisibility() == View.GONE)
            nextButton.setVisibility(View.VISIBLE);

        if(currentStep == 1 && previousButton.getVisibility() == View.VISIBLE)
            previousButton.setVisibility(View.GONE);

        switchFragment();
    }

    // go to next step
    private void nextStep()
    {
        currentStep++;

        if(currentStep > 1 && previousButton.getVisibility() == View.GONE)
            previousButton.setVisibility(View.VISIBLE);

        if(currentStep == 3 && nextButton.getVisibility() == View.VISIBLE)
            nextButton.setVisibility(View.GONE);

        switchFragment();
    }

    // switch fragment adequately to current step
    private void switchFragment()
    {
        // create proper fragment
        Fragment fragment;
        switch(currentStep)
        {
            case 1:
                fragment = new ProfileCreatorStepOneFragment();
                break;

            case 2:
                fragment = new ProfileCreatorStepTwoFragment();
                break;

            case 3:
                fragment = new ProfileCreatorStepThreeFragment();
                break;

            default:
                fragment = new ProfileCreatorStepOneFragment();
        }

        // replace current fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.profile_creator__fragment, fragment);
        fragmentTransaction.commit();
    }

    // validate fields from all 3 steps
    private boolean validateForm()
    {
        boolean valid = true;
        StringBuilder invalidData = new StringBuilder();

        // validate gender
        if(profile.getGender() == null)
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_gender_error));
        }

        // validate name
        if(profile.getName().isEmpty())
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_name_error));
        }

        // validate surname
        if(profile.getSurname().isEmpty())
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_surname_error));
        }

        // validate phone number
        if(profile.getPhoneNumber().isEmpty())
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_phone_error));
        }
        else if(profile.getPhoneNumber().length() < 7)
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__too_short_phone_error));
        }
        else if(profile.getPhoneNumber().length() > 9)
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__too_long_phone_error));
        }

        // validate state
        if(profile.getState().isEmpty())
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_state_error));
        }

        // validate city
        if(profile.getCity().isEmpty())
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_city_error));
        }

        // validate birthday
        if(profile.getBirthday().isEmpty())
        {
            valid = false;
            invalidData.append(getString(R.string.profile_creator__empty_birthday_error));
        }

        // show dialog if data is invalid
        if(!valid)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(ProfileCreatorActivity.this).create();
            alertDialog.setTitle(getString(R.string.profile_creator__invalid_data_dialog_title));
            alertDialog.setMessage(invalidData.toString());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }

        return valid;
    }

                                                        // ON BUTTONS CLICK

    // try to create profile
    public void createProfile(View view)
    {
        if(validateForm())
        {
            // show create profile process dialog
            final ProgressDialog profileCreatorDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            profileCreatorDialog.setMessage(getString(R.string.create_profile_dialog_message));
            profileCreatorDialog.show();

            // create request
            StringRequest request = new StringRequest(Request.Method.POST, RestStrings.createProfileURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    try
                    {
                        // get json response
                        JSONObject jsonResponse = new JSONObject(response);

                        // show message
                        String message = jsonResponse.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                        // open main activity if profile was created successful
                        int success = jsonResponse.getInt("success");
                        if (success == 1)
                        {
                            // save id profile
                            long idProfile = jsonResponse.getLong("id_profile");
                            SharedPreferences.Editor spEditor = userDetails.edit();
                            spEditor.putLong("idProfile", idProfile);
                            spEditor.apply();

                            // create proper activity
                            Intent intent = new Intent();
                            int accountType = userDetails.getInt("accountType", 0);
                            if(accountType == 1)
                                intent = new Intent(getApplicationContext(), EmployeeMenuActivity.class);
                            else if(accountType == 2)
                                intent = new Intent(getApplicationContext(), EmployerMenuActivity.class);

                            startActivity(intent);
                            finish();
                        }

                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.create_profile_json_extract_error), Toast.LENGTH_LONG).show();
                    }

                    profileCreatorDialog.dismiss();
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    profileCreatorDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
                })
            {
                @Override   // add params to the request
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<>();

                    // get inputs
                    String gender = profile.getGender().toString();
                    String name = profile.getName();
                    String surname = profile.getSurname();
                    String birthday = profile.getBirthday();
                    String state = profile.getState();
                    String city = profile.getCity();
                    String phoneNumber = profile.getPhoneNumber();
                    String hobby = profile.getHobby();
                    String description = profile.getDescription();
                    String idUser = String.valueOf(userDetails.getLong("idUser", 0));

                    // put params
                    params.put("gender", gender);
                    params.put("name", name);
                    params.put("surname", surname);
                    params.put("birthday", birthday);
                    params.put("state", state);
                    params.put("city", city);
                    params.put("phoneNumber", phoneNumber);
                    params.put("hobby", hobby);
                    params.put("description", description);
                    params.put("idUser", idUser);

                    return params;
                }
            };
            request.setTag(RestStrings.PROFILE_CREATOR_REQUEST_TAG);

            // send request to the server
            requestQueue.add(request);
        }
    }
}

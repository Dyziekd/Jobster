package com.example.daniel.jobster.Activities.VirtualProfile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Activities.Opinions.AddOpinionActivity;
import com.example.daniel.jobster.Activities.Opinions.OpinionsActivity;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class VirtualProfileActivity extends AppCompatActivity
{
    private String name, address, phone, email, hobby, description, rating;
    private int age;
    private long idUser, idProfile;

    private TextView nameField, addressField, ageField, phoneField, emailField, hobbyField, descriptionField, ratingField;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_profile);

        initResources();
        getProfileData();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue != null)
            requestQueue.cancelAll(RestStrings.VIRTUAL_PROFILE_REQUEST_TAG);
    }

    // init resources
    private void initResources()
    {
        // hide action bar;
        getSupportActionBar().hide();

        // init textviews
        nameField = findViewById(R.id.virtual_profile__name);
        addressField = findViewById(R.id.virtual_profile__address);
        ageField = findViewById(R.id.virtual_profile__age);
        phoneField = findViewById(R.id.virtual_profile__phone);
        emailField = findViewById(R.id.virtual_profile__email);
        hobbyField = findViewById(R.id.virtual_profile__hobby);
        descriptionField = findViewById(R.id.virtual_profile__description);
        ratingField = findViewById(R.id.virtual_profile__rating);

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // get user id
        idUser = getIntent().getExtras().getLong("idUser");

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    // get profile data from database
    private void getProfileData()
    {
        // show dialog
        final ProgressDialog getProfileDataDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        getProfileDataDialog.setMessage(getString(R.string.open_virtual_profile_dialog_message));
        getProfileDataDialog.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.getProfileURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // get json response
                    JSONObject jsonResponse = new JSONObject(response);

                    // get profile data
                    idProfile = jsonResponse.getLong("id_profile");
                    name = jsonResponse.getString("name") + " " + jsonResponse.getString("surname");
                    address = jsonResponse.getString("state") + ", " + jsonResponse.getString("city");
                    age = calculateAge(jsonResponse.getString("birthday"));
                    phone = jsonResponse.getString("phone_number");
                    email = jsonResponse.getString("email");
                    hobby = jsonResponse.getString("hobby");
                    description = jsonResponse.getString("description");
                    rating = jsonResponse.getString("average_rating");

                    // fill profile data
                    fillProfile();
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.get_profile_json_extract_error), Toast.LENGTH_LONG).show();
                }

                getProfileDataDialog.dismiss();
            }
        },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    getProfileDataDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // put params
                params.put("idUser", String.valueOf(idUser));

                return params;
            }
        };
        request.setTag(RestStrings.VIRTUAL_PROFILE_REQUEST_TAG);

        // send request to the server
        requestQueue.add(request);
    }

    // fill profile with data
    private void fillProfile()
    {
        nameField.setText(name);
        addressField.setText(address);
        ageField.setText(age + " lat");
        phoneField.setText(phone);
        emailField.setText(email);
        hobbyField.setText(hobby);
        if(TextUtils.isEmpty(hobby) || hobby.equals("null"))
            findViewById(R.id.virtual_profile__hobby_label).setVisibility(View.INVISIBLE);
        else
            hobbyField.setText(hobby);
        if(TextUtils.isEmpty(description) || description.equals("null"))
            findViewById(R.id.virtual_profile__description_label).setVisibility(View.INVISIBLE);
        else
            descriptionField.setText(description);
        if(TextUtils.isEmpty(rating) || rating.equals("null"))
        {
            //????????????????
        }
        else
            ratingField.setText(rating);
    }

    // returns elapsed years from passed date
    private int calculateAge(String birthday)
    {
        // calculate year difference
        int birthdayYear = Integer.valueOf(birthday.substring(0, 4));
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int age = currentYear - birthdayYear;

        // get birthday date (dd.MM)
        int birthdayMonth = Integer.valueOf(birthday.substring(5, 7));
        int birthdayDay = Integer.valueOf(birthday.substring(8, 10));
        double birthdayDate = Double.valueOf(birthdayMonth + "." + birthdayDay);

        // get current date (dd.MM)
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        double currentDate = Double.valueOf(currentMonth + "." + currentDay);

        // correct age
        if(birthdayDate > currentDate)
            age--;

        return age;
    }

                                                    // ON BUTTONS CLICK

    public void makeCall(View view)
    {
        // check permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        else
        {
            // get number
            String phoneNumber = phoneField.getText().toString();

            // make call
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    public void writeEmail(View view)
    {

        String emailAddress = emailField.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email_dialog_title)));
    }

    public void openOpinionsActivity(View view)
    {
        Intent opinionsActivityIntent = new Intent(getApplicationContext(), OpinionsActivity.class);
        opinionsActivityIntent.putExtra("idProfile", idProfile);
        startActivity(opinionsActivityIntent);
    }

    public void openAddOpinionActivity(View view)
    {
        Intent addOpinionIntent = new Intent(getApplicationContext(), AddOpinionActivity.class);
        addOpinionIntent.putExtra("idProfileFrom", userDetails.getLong("idProfile", 0));
        addOpinionIntent.putExtra("idProfileTo", idProfile);
        addOpinionIntent.putExtra("name", name);
        addOpinionIntent.putExtra("address", address);
        addOpinionIntent.putExtra("age", age);
        startActivity(addOpinionIntent);
    }
}

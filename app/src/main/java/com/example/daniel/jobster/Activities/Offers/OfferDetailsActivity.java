package com.example.daniel.jobster.Activities.Offers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Activities.Applications.OfferApplicationsExplorerActivity;
import com.example.daniel.jobster.Activities.VirtualProfile.VirtualProfileActivity;
import com.example.daniel.jobster.DataModels.Offer;
import com.example.daniel.jobster.DataModels.Profile;
import com.example.daniel.jobster.Functions;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OfferDetailsActivity extends AppCompatActivity
{
    private String providerName, publicationTime, offerName, place, address, startTime, description;
    private int salaryType, applicationsCount, status;
    private double salary;
    private long idOffer, idProvider;
    private boolean alreadyApplied;

    private TextView providerNameLabel, publicationTimeLabel, offerNameLabel, placeLabel, addressLabel, startTimeLabel, salaryLabel, salaryTypeLabel, descriptionLabel, applicationsCountLabel;
    private EditText messageField;
    private Button makeApplicationButton, cancelApplicationButton, showApplicationsButton, endOfferButton;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        initResources();
        getOfferDetailsData();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue != null)
            requestQueue.cancelAll(RestStrings.VIRTUAL_PROFILE_REQUEST_TAG);
    }

    private void initResources()
    {
        // hide action bar;
        getSupportActionBar().hide();

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // init textviews
        providerNameLabel = findViewById(R.id.offer_details__provider_name);
        publicationTimeLabel = findViewById(R.id.offer_details__publication_date);
        offerNameLabel = findViewById(R.id.offer_details__offer_name);
        placeLabel = findViewById(R.id.offer_details__place);
        addressLabel = findViewById(R.id.offer_details__address);
        startTimeLabel = findViewById(R.id.offer_details__start_time);
        salaryLabel = findViewById(R.id.offer_details__salary);
        salaryTypeLabel = findViewById(R.id.offer_details__salary_type);
        descriptionLabel = findViewById(R.id.offer_details__description);
        applicationsCountLabel = findViewById(R.id.offer_details__applications_count);

        // init buttons
        makeApplicationButton = findViewById(R.id.offer_details__make_application_button);
        cancelApplicationButton = findViewById(R.id.offer_details__cancel_application_button);
        showApplicationsButton = findViewById(R.id.offer_details__show_applications_button);
        endOfferButton = findViewById(R.id.offer_details__end_offer_button);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    // hide buttons depending on account type
    private void setButtonsVisibility()
    {
        int accountType = userDetails.getInt("accountType", 1);

        // hide all buttons if offer is inactive
        if(status == Offer.STATUS_INACTIVE)
        {
            makeApplicationButton.setVisibility(View.GONE);
            cancelApplicationButton.setVisibility(View.GONE);
            showApplicationsButton.setVisibility(View.GONE);
            endOfferButton.setVisibility(View.GONE);
        }
        else if (accountType == Profile.EMPLOYER_ACCOUNT)
        {
            makeApplicationButton.setVisibility(View.GONE);
            cancelApplicationButton.setVisibility(View.GONE);

            if(idProvider != userDetails.getLong("idUser", 0))
                showApplicationsButton.setVisibility(View.GONE);
        }
        else if (accountType == Profile.EMPLOYEE_ACCOUNT)
        {
            showApplicationsButton.setVisibility(View.GONE);
            endOfferButton.setVisibility(View.GONE);
            if(alreadyApplied)
                makeApplicationButton.setVisibility(View.GONE);
            else
                cancelApplicationButton.setVisibility(View.GONE);
        }
    }

    // get offer details from database
    public void getOfferDetailsData()
    {
        // show get offer details process dialog
        final ProgressDialog getOfferDetailsDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        getOfferDetailsDialog.setMessage(getString(R.string.get_offer_details_message));
        getOfferDetailsDialog.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.getOfferDetailsURL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // get json response
                    JSONObject offerDetails = new JSONObject(response);

                    // get offer details data
                    providerName = offerDetails.getString("provider_name") + " " + offerDetails.getString("provider_surname");
                    publicationTime = Functions.changeTimeFormat(offerDetails.getString("publication_time"));
                    offerName = offerDetails.getString("offer_name");
                    place = "woj. " + offerDetails.getString("state") + ", " + offerDetails.getString("city");
                    address = offerDetails.getString("address");
                    startTime = offerDetails.getString("start_time");
                    salary = offerDetails.getDouble("salary");
                    salaryType = offerDetails.getInt("salary_type");
                    description = offerDetails.getString("description");
                    applicationsCount = offerDetails.getInt("applications_count");
                    status = offerDetails.getInt("status");
                    idProvider = offerDetails.getLong("id_provider");
                    idOffer = offerDetails.getLong("id_offer");
                    alreadyApplied = offerDetails.getBoolean("already_applied");

                    // hide some buttons depending on account type
                    setButtonsVisibility();

                    // fill offer details data
                    fillOfferDetails();
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.get_offer_details_json_extract_error), Toast.LENGTH_LONG).show();
                }

                getOfferDetailsDialog.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        getOfferDetailsDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // get inputs
                String idOffer = String.valueOf(getIntent().getLongExtra("idOffer", 0));
                String idUser = String.valueOf(userDetails.getLong("idUser", 1));

                // put params
                params.put("idOffer", idOffer);
                params.put("idUser", idUser);

                return params;
            }
        };
        request.setTag(RestStrings.OFFER_REQUEST_TAG);

        // send request to the server
        requestQueue.add(request);
    }

    // fill offer details with data
    private void fillOfferDetails()
    {
        providerNameLabel.setText(providerName);
        publicationTimeLabel.setText(publicationTime);
        offerNameLabel.setText(offerName);
        placeLabel.setText(place);
        addressLabel.setText(address);
        startTimeLabel.setText(Functions.changeTimeFormat(startTime));
        salaryLabel.setText(Functions.formatDouble(salary) + "zł");
        salaryTypeLabel.setText(getSalaryTypeByNumber(salaryType));
        applicationsCountLabel.setText(applicationsCount + " użytkowników aplikowało do tej oferty.");
        if(TextUtils.isEmpty(description) || description.equals("null"))
            descriptionLabel.setVisibility(View.GONE);
        else
            descriptionLabel.setText(description);

        // display inactive message for inactive offers
        if(status == Offer.STATUS_INACTIVE)
            findViewById(R.id.offer_details__inactive_offer_message).setVisibility(View.VISIBLE);
    }

    // returns salary type (in string)
    private String getSalaryTypeByNumber(int salaryTypeNumber)
    {
        if(salaryTypeNumber == 1)
            return getResources().getStringArray(R.array.salary_type_array)[0];
        else if(salaryTypeNumber == 2)
            return getResources().getStringArray(R.array.salary_type_array)[1];
        else if(salaryTypeNumber == 3)
            return getResources().getStringArray(R.array.salary_type_array)[2];
        else if(salaryTypeNumber == 4)
            return getResources().getStringArray(R.array.salary_type_array)[3];
        else
            return "error";
    }



                                                                // ON BUTTONS CLICK

    // make application to this offer
    public void makeApplication(View view)
    {
        // create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.make_application_dialog_title);

        // set layout
        LayoutInflater inflater = LayoutInflater.from(this);
        final View v = inflater.inflate(R.layout.create_application_dialog_layout, null);
        builder.setView(v);
        ((TextView)v.findViewById(R.id.application_dialog__offer_name)).setText(offerName);

        // set buttons
        builder.setPositiveButton(getString(R.string.ok_button_text), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // send request
                StringRequest request = new StringRequest(Request.Method.POST, RestStrings.createApplicationURL, new Response.Listener<String>()
                {
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

                            // set buttons visibility
                            if(jsonResponse.getInt("success") == 1)
                            {
                                makeApplicationButton.setVisibility(View.GONE);
                                cancelApplicationButton.setVisibility(View.VISIBLE);
                                alreadyApplied = true;
                                applicationsCount++;
                                applicationsCountLabel.setText(applicationsCount + " użytkowników zaaplikowało do tej oferty.");
                            }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.create_application_json_extract_error), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        })
                {
                    @Override   // add params to the request
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<>();

                        // get params
                        messageField = v.findViewById(R.id.application_dialog__message_field);
                        String message = messageField.getText().toString().trim();
                        String idUser = String.valueOf(userDetails.getLong("idUser", 0));

                        // put params
                        params.put("message", message);
                        params.put("idUser", idUser);
                        params.put("idOffer", String.valueOf(idOffer));

                        return params;
                    }
                };
                request.setTag(RestStrings.APPLICATION_REQUEST_TAG);
                requestQueue.add(request);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        // show dialog
        builder.create().show();
    }

    // cancel application to this offer
    public void cancelApplication(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.cancel_application_dialog_title);
        builder.setMessage(R.string.cancel_application_dialog_message);
        builder.setPositiveButton(getString(R.string.yes_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // create request
                StringRequest request = new StringRequest(Request.Method.POST, RestStrings.cancelApplicationURL, new Response.Listener<String>()
                {
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

                            // set buttons visibility
                            if(jsonResponse.getInt("success") == 1)
                            {
                                makeApplicationButton.setVisibility(View.VISIBLE);
                                cancelApplicationButton.setVisibility(View.GONE);
                                alreadyApplied = false;
                                applicationsCount--;
                                applicationsCountLabel.setText(applicationsCount + " użytkowników zaaplikowało do tej oferty.");
                            }
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.cancel_application_json_extract_error), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        })
                {
                    @Override   // add params to the request
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<>();

                        // get params
                        String idUser = String.valueOf(userDetails.getLong("idUser", 0));

                        // put params
                        params.put("idUser", idUser);
                        params.put("idOffer", String.valueOf(idOffer));

                        return params;
                    }
                };

                request.setTag(RestStrings.APPLICATION_REQUEST_TAG);
                requestQueue.add(request);
            }
        });
        builder.setNegativeButton(getString(R.string.no_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    // show applications to this offer
    public void showApplications(View view)
    {
        Intent showApplicationsActivityIntent = new Intent(getApplicationContext(), OfferApplicationsExplorerActivity.class);
        showApplicationsActivityIntent.putExtra("idOffer", idOffer);
        startActivity(showApplicationsActivityIntent);
    }

    // end the possibility of applying for this offer (sets status offer to inactive)
    public void endApplications(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_offer_dialog_title);
        builder.setMessage(R.string.end_offer_dialog_message);
        builder.setPositiveButton(getString(R.string.yes_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                // create request
                StringRequest request = new StringRequest(Request.Method.POST, RestStrings.endOfferURL, new Response.Listener<String>()
                {
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

                            // set buttons visibility
                            if(jsonResponse.getInt("success") == 1)
                                endOfferButton.setVisibility(View.GONE);
                        }
                        catch (JSONException e)
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.end_offer_json_extract_error), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        })
                {
                    @Override   // add params to the request
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<>();

                        // put params
                        params.put("idOffer", String.valueOf(idOffer));

                        return params;
                    }
                };

                request.setTag(RestStrings.OFFER_REQUEST_TAG);
                requestQueue.add(request);
            }
        });
        builder.setNegativeButton(getString(R.string.no_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        builder.create().show();
    }

    // show offer provider virtual profile
    public void showProviderVirtualProfile(View view)
    {
        // start virtual profile activity
        Intent virtualProfileActivityIntent = new Intent(getApplicationContext(), VirtualProfileActivity.class);
        virtualProfileActivityIntent.putExtra("idUser", idProvider);
        startActivity(virtualProfileActivityIntent);
    }

    // open google maps showing job place
    public void openGoogleMaps(View view)
    {
        String map = "http://maps.google.co.in/maps?q=" + placeLabel.getText() + ", " + addressLabel.getText();
        Intent googleMapsIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(map));
        startActivity(googleMapsIntent);
    }


}

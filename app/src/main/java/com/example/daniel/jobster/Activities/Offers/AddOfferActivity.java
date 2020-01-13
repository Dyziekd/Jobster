package com.example.daniel.jobster.Activities.Offers;

import android.app.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddOfferActivity extends AppCompatActivity
{
    private static String startTime;    // keeps time in proper format (yyyy-MM-dd HH:mm:ss to insert to db)

    private static Button startTimeButton, endTimeButton;
    private EditText nameField, salaryField, cityField, addressField, descriptionField;
    private Spinner salarySpinner, stateSpinner;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        initResources();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue != null)
            requestQueue.cancelAll(RestStrings.OFFER_REQUEST_TAG);
    }

    // init resources
    private void initResources()
    {
        startTime = "";

        // set action bar title
        getSupportActionBar().setTitle(R.string.add_offer_toolbar_title);

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // init buttons
        startTimeButton = (Button)findViewById(R.id.add_offer__start_time_button);

        // init input fields
        nameField = (EditText)findViewById(R.id.add_offer__name_field);
        salaryField = (EditText)findViewById(R.id.add_offer__salary_field);
        cityField = (EditText)findViewById(R.id.add_offer__city_field);
        addressField = (EditText)findViewById(R.id.add_offer__address_field);
        descriptionField = (EditText)findViewById(R.id.add_offer__description_field);

        // init salary spinner
        salarySpinner = (Spinner)findViewById(R.id.add_offer__salary_type_spinner);

        // init state spinner
        stateSpinner = (Spinner)findViewById(R.id.add_offer__state_spinner);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    // validates form
    private boolean validateForm()
    {
        boolean isCorrect = true;

        // get inputs
        String name = nameField.getText().toString();
        String salary = salaryField.getText().toString();
        String city = cityField.getText().toString();
        String address = addressField.getText().toString();

        // validate offer name
        if(TextUtils.isEmpty(name))
        {
            nameField.setError(getString(R.string.empty_offer_name_error));
            isCorrect = false;
        }

        // validate salary
        if(TextUtils.isEmpty(salary))
        {
            salaryField.setError(getString(R.string.empty_salary_error));
            isCorrect = false;
        }
        else if(Double.valueOf(salary) <= 0)
        {
            salaryField.setError(getString(R.string.incorrect_salary_error));
            isCorrect = false;
        }

        // validate city
        if(TextUtils.isEmpty(city))
        {
            cityField.setError(getString(R.string.empty_city_error));
            isCorrect = false;
        }

        // validate address
        if(TextUtils.isEmpty(address))
        {
            addressField.setError(getString(R.string.empty_address_error));
            isCorrect = false;
        }

        // validate start time
        if(startTimeButton.getText().toString() == getString(R.string.choose_start_time_button_text))
        {
            Toast.makeText(getApplicationContext(), getString(R.string.empty_start_time_error), Toast.LENGTH_LONG).show();
            isCorrect = false;
        }

        return isCorrect;
    }
                                                    // ON BUTTONS CLICK

    // attempts add offer on button click
    public void addOffer(View view)
    {
        if (validateForm())
        {
            // show add offer process dialog
            final ProgressDialog addOfferDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            addOfferDialog.setMessage(getString(R.string.add_offer_dialog_message));
            addOfferDialog.show();

            // create request
            StringRequest request = new StringRequest(Request.Method.POST, RestStrings.createOfferURL, new Response.Listener<String>()
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

                        // finish this activity if password was added successful
                        int success = jsonResponse.getInt("success");
                        if(success == 1)
                            finish();
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.add_offer_json_extract_error), Toast.LENGTH_LONG).show();
                    }

                    addOfferDialog.dismiss();
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    addOfferDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override   // add params to the request
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<>();

                    // get inputs
                    String name = nameField.getText().toString().trim();
                    String salaryType = getSalaryTypeNumber(salarySpinner.getSelectedItem().toString());
                    String salary = salaryField.getText().toString().trim();
                    String state = stateSpinner.getSelectedItem().toString().trim();
                    String city = cityField.getText().toString().trim();
                    String address = addressField.getText().toString().trim();
                    String description = descriptionField.getText().toString().trim();
                    String idUser = String.valueOf(userDetails.getLong("idUser", 0));

                    // put params
                    params.put("name", name);
                    params.put("salaryType", salaryType);
                    params.put("salary", salary);
                    params.put("state", state);
                    params.put("city", city);
                    params.put("address", address);
                    params.put("startTime", startTime);
                    params.put("description", description);
                    params.put("idUser", idUser);

                    return params;
                }
            };
            request.setTag(RestStrings.OFFER_REQUEST_TAG);

            // send request to the server
            requestQueue.add(request);
        }
    }

    // opens dialog to select start time
    public void selectStartTime(View view)
    {
        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.show(getFragmentManager(), getString(R.string.date_picker_title));
    }

    // returns number of specified salary type (used to insert it into database)
    private String getSalaryTypeNumber(String salaryType)
    {
        String piecework = getResources().getStringArray(R.array.salary_type_array)[0];
        String perHour = getResources().getStringArray(R.array.salary_type_array)[1];
        String perDay = getResources().getStringArray(R.array.salary_type_array)[2];
        String perWork = getResources().getStringArray(R.array.salary_type_array)[3];

        if(salaryType.equals(piecework))
            return "1";
        else if(salaryType.equals(perHour))
            return "2";
        else if(salaryType.equals(perDay))
            return "3";
        else if(salaryType.equals(perWork))
            return "4";
        else
            return "0";
    }

                                                    // SUBCLASSES

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // get current date
            Calendar calender = Calendar.getInstance();
            int year = calender.get(Calendar.YEAR);
            int month = calender.get(Calendar.MONTH);
            int day = calender.get(Calendar.DAY_OF_MONTH);

            // create dialog
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
        {
            // change date format
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            String dateAppFormat = new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime());
            String dateDbFormat = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            // get time
            startTimeButton.setText(dateAppFormat);
            startTime = dateDbFormat;

            // open time picker
            TimePickerFragment timePicker = new TimePickerFragment();
            timePicker.show(getFragmentManager(), getString(R.string.time_picker_title));
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // get current time
            Calendar calender = Calendar.getInstance();
            int hour = calender.get(Calendar.HOUR_OF_DAY);
            int minute = calender.get(Calendar.MINUTE);

            // create dialog
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute)
        {
            // change time format
            Calendar calendar = Calendar.getInstance();
            calendar.set(0, 0, 0,  hourOfDay, minute);
            String timeAppFormat = new SimpleDateFormat("HH:mm").format(calendar.getTime());
            String timeDbFormat = new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());

            // append time to date
            startTimeButton.setText(startTimeButton.getText().toString() + " - " + timeAppFormat);
            startTime = startTime + " " + timeDbFormat;
        }
    }
}

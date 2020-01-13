package com.example.daniel.jobster.Activities.Other;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Functions;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingsActivity extends AppCompatActivity
{
    private String newValue, password;

    private EditText newValueField, passwordField;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initResources();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue != null)
            requestQueue.cancelAll(RestStrings.LOGIN_REQUEST_TAG);
    }

    // init resources
    private void initResources()
    {
        // set action bar title
        getSupportActionBar().setTitle(R.string.settings_toolbar_title);

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    public void changePassword(View view)
    {
        // show dialog window
        AlertDialog.Builder builder = createDialog(getString(R.string.change_password_dialog_title), getString(R.string.new_password_field_hint), InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validate passwords
                String newPassword = newValueField.getText().toString();
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(newPassword))
                    newValueField.setError(getString(R.string.empty_password_error));
                else if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params
                    newValue = Functions.sha512(newPassword, userDetails.getString("login", ""));
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changePasswordFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    public void changeEmail(View view)
    {
        // show dialog window
        AlertDialog.Builder builder = createDialog(getString(R.string.change_email_dialog_title), getString(R.string.email_field_hint), InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validate email and password
                String email = newValueField.getText().toString();
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(email))
                    newValueField.setError(getString(R.string.empty_email_error));
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    newValueField.setError(getString(R.string.incorrect_email_error));
                else if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params
                    newValue = email;
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changeEmailFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    public void changePhoneNumber(View view)
    {
        // show dialog window
        AlertDialog.Builder builder = createDialog(getString(R.string.change_phone_dialog_title), getString(R.string.phone_field_hint), InputType.TYPE_CLASS_PHONE);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validate phone number and password
                String phoneNumber = newValueField.getText().toString();
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(phoneNumber))
                    newValueField.setError(getString(R.string.empty_phone_error));
                else if(phoneNumber.length() < 7)
                    newValueField.setError(getString(R.string.too_short_phone_error));
                else if(phoneNumber.length() > 9)
                    newValueField.setError(getString(R.string.too_long_phone_error));
                else if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params
                    newValue = phoneNumber;
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changePhoneFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    public void changeState(View view)
    {
        // create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.change_state_dialog_title));

        // set layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.state_settings_dialog_layout, null);
        builder.setView(layout);

        // init input fields
        final Spinner statesSpinner = (Spinner)layout.findViewById(R.id.state_settings_dialog__state_spinner);
        final EditText passwordField = (EditText)layout.findViewById(R.id.state_settings_dialog__password_field);

        // set buttons
        builder.setPositiveButton(getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // show dialog
        final AlertDialog dialog = builder.show();

        // override buttons
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // validate password
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params;
                    newValue = statesSpinner.getSelectedItem().toString().trim();
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changeStateFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    public void changeCity(View view)
    {
        // show dialog window
        AlertDialog.Builder builder = createDialog(getString(R.string.change_city_dialog_title), getString(R.string.city_field_hint), InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validate city and password
                String city = newValueField.getText().toString();
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(city))
                    newValueField.setError(getString(R.string.empty_city_error));
                else if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params
                    newValue = city;
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changeCityFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    public void changeHobby(View view)
    {
        // show dialog window
        AlertDialog.Builder builder = createDialog(getString(R.string.change_hobby_dialog_title), getString(R.string.hobby_field_hint), InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validate password
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params
                    newValue = newValueField.getText().toString();
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changeHobbyFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    public void changeDescription(View view)
    {
        // show dialog window
        AlertDialog.Builder builder = createDialog(getString(R.string.change_description_dialog_title), getString(R.string.description_field_hint), InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // validate password
                String pass = passwordField.getText().toString();
                if(TextUtils.isEmpty(pass))
                    passwordField.setError(getString(R.string.empty_password_error));
                else
                {
                    // set request params
                    newValue = newValueField.getText().toString();
                    password = pass;

                    // send request
                    Request request = createRequest(RestStrings.changeDescriptionFunction);
                    request.setTag(RestStrings.SETTINGS_REQUEST_TAG);
                    requestQueue.add(request);

                    dialog.dismiss();
                }
            }
        });
    }

    // builds alert dialog
    private AlertDialog.Builder createDialog(String dialogTitle, String inputFieldLabel, int inputFieldType)
    {
        // create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(dialogTitle);

        // set layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.settings_dialog_layout, null);
        builder.setView(view);
        ((TextView)view.findViewById(R.id.settings_dialog__new_value_label)).setText(inputFieldLabel);

        // init input fields
        newValueField = view.findViewById(R.id.settings_dialog__new_value_field);
        newValueField.setInputType(InputType.TYPE_CLASS_TEXT | inputFieldType);
        passwordField = view.findViewById(R.id.settings_dialog__password_field);

        // set buttons
        builder.setPositiveButton(getString(R.string.ok_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.setNegativeButton(getString(R.string.cancel_button_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        return builder;
    }

    // builds request to server
    private StringRequest createRequest(final String function)
    {
        // show changing settings process dialog
        final ProgressDialog updateDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        updateDialog.setMessage(getString(R.string.change_settings_dialog_message));
        updateDialog.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.changeSettingsURL, new Response.Listener<String>() {
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
                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.change_settings_json_extract_error), Toast.LENGTH_LONG).show();
                }

                updateDialog.dismiss();
            }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    updateDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // get params
                String login = userDetails.getString("login", "");
                String idUser = String.valueOf(userDetails.getLong("idUser", 0));

                // put params
                params.put("newValue", newValue);
                params.put("password", Functions.sha512(password, login));
                params.put("idUser", idUser);
                params.put("function", function);

                return params;
            }
        };

        return request;
    }
}

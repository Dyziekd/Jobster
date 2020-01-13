package com.example.daniel.jobster.Activities.Other;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Activities.Menu.EmployeeMenuActivity;
import com.example.daniel.jobster.Activities.Menu.EmployerMenuActivity;
import com.example.daniel.jobster.Activities.VirtualProfile.ProfileCreatorActivity;
import com.example.daniel.jobster.Functions;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
{
    private EditText loginField, passwordField;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        // hide action bar;
        getSupportActionBar().hide();

        // init input fields
        loginField = findViewById(R.id.login__login_field);
        passwordField = findViewById(R.id.login__password_field);

        // checks if internet connection is active
        Functions.checkInternetConnection(LoginActivity.this);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    // validate form
    private boolean validateForm()
    {
        boolean isCorrect = true;

        // validate login
        if(TextUtils.isEmpty(loginField.getText().toString()))
        {
            loginField.setError(getString(R.string.empty_login_email_error));
            isCorrect = false;
        }

        // validate password
        if(TextUtils.isEmpty(passwordField.getText().toString()))
        {
            passwordField.setError(getString(R.string.empty_password_error));
            isCorrect = false;
        }

        return isCorrect;
    }


                                                            // ON BUTTONS CLICK

    // try to sign in
    public void login(View view)
    {
        if(validateForm())
        {
            // show login process dialog
            final ProgressDialog loginDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            loginDialog.setMessage(getString(R.string.login_dialog_message));
            loginDialog.show();

            // create request
            StringRequest request = new StringRequest(Request.Method.POST, RestStrings.loginURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    try
                    {
                        // get json response
                        JSONObject jsonResponse = new JSONObject(response);

                        // open main activity or profile creator if user logs in for the first time, in other case show suitable message
                        int success = jsonResponse.getInt("success");
                        if (success == 1)
                        {
                            // save user data (id and account type)
                            long idUser = jsonResponse.getLong("id_user");
                            long idProfile = jsonResponse.getLong("id_profile");
                            int accountType = jsonResponse.getInt("account_type");
                            SharedPreferences userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);
                            SharedPreferences.Editor spEditor = userDetails.edit();
                            spEditor.putLong("idUser", idUser);
                            spEditor.putLong("idProfile", idProfile);
                            spEditor.putString("login", loginField.getText().toString());
                            spEditor.putInt("accountType", accountType);
                            spEditor.apply();

                            // create proper activity
                            Intent intent;
                            boolean hasProfile = jsonResponse.getBoolean("has_profile");
                            if(hasProfile && accountType == 1)
                                intent = new Intent(getApplicationContext(), EmployeeMenuActivity.class);
                            else if(hasProfile && accountType == 2)
                                intent = new Intent(getApplicationContext(), EmployerMenuActivity.class);
                            else
                                intent = new Intent(getApplicationContext(), ProfileCreatorActivity.class);

                            // start new activity
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            // show message
                            String message = jsonResponse.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_json_extract_error), Toast.LENGTH_LONG).show();
                    }

                    loginDialog.dismiss();
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    loginDialog.dismiss();
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override   // add params to the request
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<>();

                    // get inputs
                    String login = loginField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();

                    // put params
                    params.put("login", login);
                    params.put("password", Functions.sha512(password, login));

                    return params;
                }
            };
            request.setTag(RestStrings.LOGIN_REQUEST_TAG);

            // send request to the server
            requestQueue.add(request);
        }
    }

    // register hyperlink method
    public void startRegisterActivity(View view)
    {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }
}

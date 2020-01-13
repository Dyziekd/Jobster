package com.example.daniel.jobster.Activities.Other;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity
{
    private int accountType = 1;    // 1 - employee;    2 - employer

    private EditText loginField, emailField, passwordField, confirmPasswordField;
    private Button employeeButton, employerButton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initResources();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if(requestQueue != null)
            requestQueue.cancelAll(RestStrings.REGISTER_REQUEST_TAG);
    }

    // init resources
    private void initResources()
    {
        // hide action bar
        getSupportActionBar().hide();

        // init input fields
        loginField = findViewById(R.id.register__login_field);
        emailField = findViewById(R.id.register__email_field);
        passwordField = findViewById(R.id.register__password_field);
        confirmPasswordField = findViewById(R.id.register__confirm_password_field);

        // init account type buttons
        employeeButton = findViewById(R.id.register__account_type_employee_button);
        employerButton = findViewById(R.id.register__account_type_employer_button);
        employeeButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
        employerButton.setBackgroundColor(android.R.drawable.btn_default);
        employeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employeeButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                employerButton.setBackgroundColor(android.R.drawable.btn_default);

                accountType = 1;
            }
        });
        employerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employerButton.setBackgroundColor(getResources().getColor(R.color.colorOrange));
                employeeButton.setBackgroundColor(android.R.drawable.btn_default);

                accountType = 2;
            }
        });

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
            loginField.setError(getString(R.string.empty_login_error));
            isCorrect = false;
        }

        // validate email
        if(TextUtils.isEmpty(emailField.getText().toString()))
        {
            emailField.setError(getString(R.string.empty_email_error));
            isCorrect = false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString()).matches())
        {
            emailField.setError(getString(R.string.incorrect_email_error));
            isCorrect = false;
        }

        // validate password
        if(TextUtils.isEmpty(passwordField.getText().toString()))
        {
            passwordField.setError(getString(R.string.empty_password_error));
            isCorrect = false;
        }

        // validate confirm password
        if(TextUtils.isEmpty(confirmPasswordField.getText().toString()))
        {
            confirmPasswordField.setError(getString(R.string.empty_password_error));
            isCorrect = false;
        }
        else if(!passwordField.getText().toString().equals(confirmPasswordField.getText().toString()))
        {
            confirmPasswordField.setError(getString(R.string.incorrect_confirm_password_error));
            isCorrect = false;
        }

        return isCorrect;
    }

                                                        // ON BUTTONS CLICK

    // try to sign up
    public void register(View view)
    {
        if(validateForm())
        {
            // show login process dialog
            final ProgressDialog registerDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            registerDialog.setMessage(getString(R.string.register_dialog_message));
            registerDialog.show();

            // create request
            StringRequest request = new StringRequest(Request.Method.POST, RestStrings.registerURL, new Response.Listener<String>() {
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

                        // back to login activity if registration was successful
                        int success = jsonResponse.getInt("success");
                        if (success == 1)
                            startLoginActivity(null);
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), getString(R.string.register_json_extract_error), Toast.LENGTH_LONG).show();
                    }

                    registerDialog.dismiss();
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    registerDialog.dismiss();
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
                    String email = emailField.getText().toString().trim();
                    String password = passwordField.getText().toString().trim();

                    // put params
                    params.put("login", login);
                    params.put("email", email);
                    params.put("password", Functions.sha512(password, login));
                    params.put("accountType", String.valueOf(accountType));

                    return params;
                }
            };
            request.setTag(RestStrings.REGISTER_REQUEST_TAG);

            // send request to the server
            requestQueue.add(request);
        }
    }

    // login hyperlink method
    public void startLoginActivity(View view)
    {
        finish();
    }
}

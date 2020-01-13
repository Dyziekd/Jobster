package com.example.daniel.jobster.Activities.Opinions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddOpinionActivity extends AppCompatActivity
{
    private EditText commentField;
    private TextView nameField, addressField, ageField;
    private RatingBar ratingBar;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opinion);

        initResources();
        fillData();
    }

    private void initResources()
    {
        // init textviews
        nameField = (TextView) findViewById(R.id.add_opinion__name_field);
        addressField = (TextView) findViewById(R.id.add_opinion__address_field);
        ageField = (TextView) findViewById(R.id.add_opinion__age_field);

        // init edittexts
        commentField = (EditText)findViewById(R.id.add_opinion__comment_field);

        // init rating bar
        ratingBar = (RatingBar)findViewById(R.id.add_opinion__rating_bar);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);
    }

    private void fillData()
    {
        nameField.setText(getIntent().getStringExtra("name"));
        addressField.setText(getIntent().getStringExtra("address"));
        ageField.setText(getIntent().getIntExtra("age", 0) + " lat");
    }

                                                                // ON BUTTONS CLICK

    public void addOpinion(View view)
    {
        // show dialog
        final ProgressDialog addOpinionData = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        addOpinionData.setMessage(getString(R.string.add_opinion_dialog_message));
        addOpinionData.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.createOpinionURL, new Response.Listener<String>() {
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
                    Toast.makeText(getApplicationContext(), getString(R.string.add_opinion_json_extract_error), Toast.LENGTH_LONG).show();
                }

                addOpinionData.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        addOpinionData.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // put params
                params.put("rating", String.valueOf(ratingBar.getRating()));
                params.put("comment", commentField.getText().toString().trim());
                params.put("id_profile_from", String.valueOf(getIntent().getLongExtra("idProfileFrom", 0)));
                params.put("id_profile_to", String.valueOf(getIntent().getLongExtra("idProfileTo", 0)));

                return params;
            }
        };
        request.setTag(RestStrings.OPINIONS_REQUEST_TAG);

        // send request to the server
        requestQueue.add(request);
    }
}

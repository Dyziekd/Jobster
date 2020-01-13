package com.example.daniel.jobster.Activities.Opinions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Adapters.OpinionsAdapter;
import com.example.daniel.jobster.DataModels.Rating;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpinionsActivity extends AppCompatActivity
{
    private ListView opinionsList;
    private OpinionsAdapter opinionsAdapter;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opinions);

        initResources();
    }

    // reload list on resume
    @Override
    protected void onResume()
    {
        super.onResume();

        // clear current opinions list
        opinionsAdapter.clear();

        // get opinions
        getOpinions();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if (requestQueue != null)
            requestQueue.cancelAll(RestStrings.OFFER_REQUEST_TAG);
    }

    private void initResources()
    {
        // set action bar title
        getSupportActionBar().setTitle(R.string.opinions_toolbar_title);

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);

        initOpinionsList();
    }

    // init adapter and listview for opinions
    private void initOpinionsList()
    {
        // init opinions adapter
        opinionsAdapter = new OpinionsAdapter(this, R.layout.opinions_list_row, new ArrayList<Rating>());

        // init opinions list
        opinionsList = (ListView)findViewById(R.id.opinions__list);
        opinionsList.setAdapter(opinionsAdapter);
    }

    // get specified user opinions from database
    private void getOpinions()
    {
        // show get opinons process dialog
        final ProgressDialog getOpinionsDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        getOpinionsDialog.setMessage(getString(R.string.get_opinions_dialog_message));
        getOpinionsDialog.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.getOpinionsURL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // get opinions array from json response
                    JSONArray opinionsArray = new JSONArray(response);

                    // add opinions to the list
                    for(int i = 0; i < opinionsArray.length(); i++)
                    {
                        // get opinion data object
                        JSONObject opinionData = opinionsArray.getJSONObject(i);

                        // get opinion data
                        long idOpinion = opinionData.getLong("id_rating");
                        String userFromName = opinionData.getString("name") + " " + opinionData.getString("surname");
                        String comment = opinionData.getString("comment");
                        double rating = opinionData.getDouble("rating");
                        long idProfileFrom = opinionData.getLong("id_profile_from");
                        long idProfileTo = opinionData.getLong("id_profile_to");

                        // create opinion object
                        Rating opinion = new Rating(idOpinion, userFromName, comment, rating, idProfileFrom, idProfileTo);

                        // add opinion to the list
                        opinionsAdapter.add(opinion);
                    }

                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.get_opinions_json_extract_error), Toast.LENGTH_LONG).show();
                }

                getOpinionsDialog.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        getOpinionsDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // get inputs
                String idProfile = String.valueOf(getIntent().getLongExtra("idProfile", 0));

                // put params
                params.put("idProfile", idProfile);

                return params;
            }
        };
        request.setTag(RestStrings.APPLICATION_REQUEST_TAG);

        // send request to the server
        requestQueue.add(request);
    }

}

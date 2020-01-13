package com.example.daniel.jobster.Activities.Offers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.jobster.Adapters.OffersAdapter;
import com.example.daniel.jobster.DataModels.Offer;
import com.example.daniel.jobster.DataModels.Profile;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OffersExplorerActivity extends AppCompatActivity
{
    private int explorerMode;// 0 - all offers; 1 - my offers; 2 - my offers history
    private final static int ALL_OFFERS_MODE = 0;
    private final static int MY_OFFERS_MODE = 1;
    private final static int HISTORY_OFFERS_MODE = 2;

    private ListView offersList;
    private OffersAdapter offersAdapter;
    private RequestQueue requestQueue;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_explorer);

        initResources();
    }

    // reload list on resume
    @Override
    protected void onResume()
    {
        super.onResume();

        // clear current offers list
        offersAdapter.clear();

        // get offers
        fillOffersList();
    }

    // cancel all requests on stop
    @Override
    protected void onStop()
    {
        super.onStop();
        if (requestQueue != null)
            requestQueue.cancelAll(RestStrings.OFFER_REQUEST_TAG);
    }

    // inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // inflate menu layout
        getMenuInflater().inflate(R.menu.offers_explorer_toolbar, menu);

        // set activity name
        setActivityLabel();

        // hide add button on toolbar if its employee account in explorer mode
        hideAddButtonForEmployee(menu);

        // set buttons listeners
        MenuItem menuItem = menu.findItem(R.id.offers_toolbar__search_button);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                offersAdapter.getFilter().filter(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    // set suitable activity label
    private void setActivityLabel()
    {
        ActionBar actionBar = getSupportActionBar();

        switch (explorerMode) {
            case ALL_OFFERS_MODE:
                actionBar.setTitle(getString(R.string.offers_toolbar_title));
                break;

            case MY_OFFERS_MODE:
                actionBar.setTitle(getString(R.string.my_offers_toolbar_title));
                break;

            case HISTORY_OFFERS_MODE:
                actionBar.setTitle(getString(R.string.my_offers_history_toolbar_title));
                break;

            default:
                actionBar.setTitle(getString(R.string.offers_toolbar_title));
        }
    }

    // hide add button on toolbar if its employee account or activity works as offers history
    private void hideAddButtonForEmployee(Menu menu)
    {
        int accountType = userDetails.getInt("accountType", 1);

        if (accountType == Profile.EMPLOYEE_ACCOUNT || explorerMode == HISTORY_OFFERS_MODE)
        {
            MenuItem addButton = menu.findItem(R.id.offers_toolbar__add_button);
            addButton.setVisible(false);
        }
    }

    private void initResources()
    {
        // get explorer mode
        explorerMode = getIntent().getIntExtra("explorerMode", 0);

        // init shared preferences
        userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);

        // init request queue
        requestQueue = Volley.newRequestQueue(this);

        initOffersList();
    }

    // init adapter and listview for offers
    private void initOffersList()
    {
        // init offers adapter
        offersAdapter = new OffersAdapter(this, R.layout.applications_list_row, new ArrayList<Offer>());

        // init offers listview
        offersList = (ListView) findViewById(R.id.offers__list);
        offersList.setAdapter(offersAdapter);
        offersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // get offer id
                long idOffer = offersAdapter.getItem(position).getId();

                // open offer details activity
                Intent offerDetailsIntent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
                offerDetailsIntent.putExtra("idOffer", idOffer);
                startActivity(offerDetailsIntent);
            }
        });
    }

    // fill list with suitable offers
    private void fillOffersList()
    {
        if (explorerMode == ALL_OFFERS_MODE)
            getAllOffers();
        else
            getMyOffers();
    }

    // get offers from database
    private void getAllOffers()
    {
        // show get offers process dialog
        final ProgressDialog getOffersDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        getOffersDialog.setMessage(getString(R.string.get_offers_dialog_message));
        getOffersDialog.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.getOffersURL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // get offers array from json response
                    JSONArray offersArray = new JSONArray(response);

                    // add offers to the list
                    for(int i = 0; i < offersArray.length(); i++)
                    {
                        // get offer data object
                        JSONObject offerData = offersArray.getJSONObject(i);

                        // get offer data
                        long idOffer = offerData.getInt("id_offer");
                        String offerName = offerData.getString("offer_name");
                        String city = offerData.getString("city");
                        String startTime = offerData.getString("start_time");
                        double salary = offerData.getDouble("salary");
                        int salaryType = offerData.getInt("salary_type");
                        String providerName = offerData.getString("provider_name") + " " + offerData.getString("provider_surname");

                        // create offer object
                        Offer offer = new Offer(idOffer, offerName, city, startTime, salary, salaryType, providerName);

                        // add offer to the list
                        offersAdapter.add(offer);
                    }

                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.get_offers_json_extract_error), Toast.LENGTH_LONG).show();
                }

                getOffersDialog.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        getOffersDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // put params
                params.put("status", String.valueOf(1)); // get only active offers

                return params;
            }
        };
        request.setTag(RestStrings.OFFER_REQUEST_TAG);

        // send request to the server
        requestQueue.add(request);
    }

    // get specified user offers from database
    public void getMyOffers()
    {
        // show get offers process dialog
        final ProgressDialog getOffersDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        getOffersDialog.setMessage(getString(R.string.get_offers_dialog_message));
        getOffersDialog.show();

        // create request
        StringRequest request = new StringRequest(Request.Method.POST, RestStrings.getOffersURL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    // get offers array from json response
                    JSONArray offersArray = new JSONArray(response);

                    // add offers to the list
                    for(int i = 0; i < offersArray.length(); i++)
                    {
                        // get offer data object
                        JSONObject offerData = offersArray.getJSONObject(i);

                        // get offer data
                        long idOffer = offerData.getLong("id_offer");
                        String offerName = offerData.getString("offer_name");
                        String city = offerData.getString("city");
                        String startTime = offerData.getString("start_time");
                        double salary = offerData.getDouble("salary");
                        int salaryType = offerData.getInt("salary_type");

                        // create offer object
                        Offer offer = new Offer(idOffer, offerName, city, startTime, salary, salaryType);

                        // add offer to the list
                        offersAdapter.add(offer);
                    }

                }
                catch (JSONException e)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.get_offers_json_extract_error), Toast.LENGTH_LONG).show();
                }

                getOffersDialog.dismiss();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        getOffersDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override   // add params to the request
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // get inputs
                String idUser = String.valueOf(userDetails.getLong("idUser", 0));
                String status = "0";    // inactive offers (My Offers History)
                if(explorerMode == MY_OFFERS_MODE)
                    status = "1";   // active offers (My Offers)

                // put params
                params.put("idUser", idUser);
                params.put("status", status);

                return params;
            }
        };
        request.setTag(RestStrings.OFFER_REQUEST_TAG);

        // send request to the server
        requestQueue.add(request);
    }


                                                  // ON BUTTONS CLICK

    public void openAddOfferActivity(MenuItem item)
    {
        Intent addOfferActivity = new Intent(getApplicationContext(), AddOfferActivity.class);
        startActivity(addOfferActivity);
    }
}

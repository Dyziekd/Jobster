package com.example.daniel.jobster.Activities.Menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.example.daniel.jobster.Activities.Offers.OffersExplorerActivity;
import com.example.daniel.jobster.Activities.Other.LoginActivity;
import com.example.daniel.jobster.Activities.Other.SettingsActivity;
import com.example.daniel.jobster.Activities.VirtualProfile.VirtualProfileActivity;
import com.example.daniel.jobster.R;
import com.example.daniel.jobster.RestStrings;

public class EmployerMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_menu);

        // hide action bar;
        getSupportActionBar().hide();
    }

    // open CatalogActivity
    public void openCatalogActivity(View view)
    {
        Intent offersActivityIntent = new Intent(getApplicationContext(), OffersExplorerActivity.class);
        offersActivityIntent.putExtra("explorerMode", 0);
        startActivity(offersActivityIntent);
    }

    // open MyOffersActivity (only active offers)
    public void openMyOffersActivity(View view)
    {
        Intent myOffersActivityIntent = new Intent(getApplicationContext(), OffersExplorerActivity.class);
        myOffersActivityIntent.putExtra("explorerMode", 1);
        startActivity(myOffersActivityIntent);
    }

    // open MyOffersActivity (only inactive offers)
    public void openMyOffersHistoryActivity(View view)
    {
        Intent myOffersHistoryActivityIntent = new Intent(getApplicationContext(), OffersExplorerActivity.class);
        myOffersHistoryActivityIntent.putExtra("explorerMode", 2);
        startActivity(myOffersHistoryActivityIntent);
    }

    // open VirtualProfileActivity
    public void openMyVirtualProfileActivity(View view)
    {
        // get user id
        SharedPreferences userDetails = getApplicationContext().getSharedPreferences(RestStrings.preferencesFile, Context.MODE_PRIVATE);
        long idUser = userDetails.getLong("idUser", 0);

        // start virtual profile activity
        Intent virtualProfileActivity = new Intent(getApplicationContext(), VirtualProfileActivity.class);
        virtualProfileActivity.putExtra("idUser", idUser);
        startActivity(virtualProfileActivity);
    }

    // open SettingsActivity
    public void openSettingsActivity(View view)
    {
        // start settings activity
        Intent settingsActivityIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(settingsActivityIntent);
    }

    // sign out
    public void logout(View view)
    {
        // start login activity and close other activities
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginActivity);
    }
}

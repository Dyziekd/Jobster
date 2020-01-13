package com.example.daniel.jobster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Functions
{
    // cuts unnecessary zeros (e.g. 1.0 -> 1;  0.970 -> 0.97)
    public static String formatDouble(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s", d);
    }

    // changes time format (FROM yyyy-MM-dd kk:mm:ss [database format] TO dd.MM.yyyy kk:mm [application format])
    public static String changeTimeFormat(String time)
    {
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        SimpleDateFormat appFormat = new SimpleDateFormat("dd.MM.yyyy kk:mm");

        try
        {
            Date timeDate = dbFormat.parse(time);
            return appFormat.format(timeDate);
        }
        catch (ParseException e)
        {
            return "error";
        }
    }

    // returns hashed text using SHA512 algorithm (used to encrypt password)
    public static String sha512(String text, String salt)
    {
        String result;

        try
        {
            // get bytes to encryption
            byte textBytes[] = text.getBytes();
            byte saltBytes[] = salt.getBytes();
            byte bytesToEncrypt[] = new byte[textBytes.length + saltBytes.length];
            System.arraycopy(textBytes, 0, bytesToEncrypt, 0, textBytes.length);
            System.arraycopy(saltBytes, 0, bytesToEncrypt, textBytes.length, saltBytes.length);

            // create digest object with SHA-512 algorithm
            MessageDigest myDigest = MessageDigest.getInstance("SHA-512");
            myDigest.update(bytesToEncrypt);

            // encrypt bytes
            byte encryptedBytes[] = myDigest.digest();

            // convert hex to string
            StringBuilder hexString = new StringBuilder();
            for(int i = 0; i < encryptedBytes.length; i++)
                hexString.append(String.format("%02X", encryptedBytes[i]));

            // return hash in lower case
            result = hexString.toString().toLowerCase();
        }
        catch(Exception e)
        {
            result = "error";
        }

        return result;
    }


    // checks internet connection
    public static void checkInternetConnection(final Activity activity)
    {
        // get information about network
        ConnectivityManager myConnectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo myNetworkInfo = myConnectivityManager.getActiveNetworkInfo();

        // check connection
        if(myNetworkInfo!= null && myNetworkInfo.isConnectedOrConnecting()) // internet connection is active
            return;
        else   // show dialog window if internet connection is not active
        {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.connection_error_dialog_title)
                    .setMessage(R.string.connection_error_dialog_message)
                    .setPositiveButton(R.string.try_again_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {    // try to connect again
                            checkInternetConnection(activity);
                        }
                    })
                    .setNegativeButton(R.string.exit_button_text, new DialogInterface.OnClickListener() {   // exit
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finish();
                        }
                    }).show();
        }
    }
}

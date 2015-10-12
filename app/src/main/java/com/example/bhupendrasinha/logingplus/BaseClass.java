package com.example.bhupendrasinha.logingplus;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bhupendra on 12-10-2015.
 */
public class BaseClass extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener
{


    //GPlus
    GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private static final int RC_SIGN_IN = 1;

    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    private static final int RC_PERM_GET_ACCOUNTS = 2;


    final static String TAG = "Bhupi";

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Signing in", Toast.LENGTH_SHORT).show();
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }


        // Show a message to the user that we are signing in.
        Toast.makeText(this, "Sign out", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;


        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String name = currentPerson.getDisplayName();
        String imageURL = currentPerson.getImage().getUrl();

        imageURL = imageURL.substring(0,
                imageURL.length() - 2)
                + 80;

        String emailID =  Plus.AccountApi.getAccountName(mGoogleApiClient);

        JSONObject object = new JSONObject();
        try
        {

            object.put("name", name);
            object.put("imageURL",imageURL);
            object.put("emailID",emailID);

            Log.d(TAG, "Bhupi:" + name);
            Log.d(TAG, "Bhupi:" +imageURL);
        }
        catch (JSONException ex)
        {
            Log.d(TAG, "Error:" + ex.getMessage());
        }



        // Show a message to the user that we are signing in.
        //Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(this, connectionResult.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
            Toast.makeText(this, "Signout", Toast.LENGTH_SHORT).show();
        }

    }


}

package com.unipi.cs.p15013p15120.kastropoliteies;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

//loading splash screen activity
/*
we check if user has connection to the internet and if user has an account for the app
if user does not have an account we prompt him to the login and sign up page
if user has an account we check if he has answered his initial questionnaire
if user has answered the questionnaire we prompt him to home page activity
else we prompt him to questionnaire activity to answer his questionnaire
*/
public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";

    //variable to save whether user uses google account
    private SharedPreferences sp;

    Intent i;

    //variables to use in checkQ method
    String cuid; //current user id
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseAuth auth;
    ValueEventListener listener;
    boolean found;

    //constructor for Dialog class to show a dialog
    //Dialog dialog;


    ConnectivityManager cm;
    static boolean resumed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //set the shared preferences variable to save if user is logged in with gmail
        sp = getSharedPreferences("account_google", MODE_PRIVATE);

        //logo splash screen
        ImageView logo = findViewById(R.id.logo);
        makeRound(R.drawable.logo,logo);

        //check if there is internet connection
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        checkForConnection(cm);
    }

    //on restart activity we check again for internet connection
    @Override
    public void onRestart() {
        super.onRestart();

        //variable to save if user restarted activity
        resumed = true;
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        checkForConnection(cm);
    }

    //show toast message
    private void makeToast(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }

    private void checkForConnection(ConnectivityManager cm)
    {
        //if there is connection to a network
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {

            auth = FirebaseAuth.getInstance();
            //if there is a connected user, check i user has answered the questionnaire
            if (auth.getCurrentUser() != null)
                checkQ(auth.getCurrentUser());
            //if user is not connected with an account prompt user to login and sign up activity
            else {
                i = new Intent(this, LoginSignUpPage.class);
                sp.edit().putBoolean("account_google",false).apply();
                startActivity(i);
                finish();
            }
        }//if there is no network
        else {
            Log.w(TAG,"No Network");
            //if we are not in activity from restart show alert dialog using the Dialog constructor
            if (!resumed) {
                dialog = new Dialog("No Internet", "splash", "Οι Καστροπολιτείες χρειάζονται σύνδεση στο διαδίκτυο για να λειτουργήσουν. Παρακαλώ συνδέσου και ξαναπροσπάθησε!");
                dialog.show(getSupportFragmentManager(), "splash");
            }//else show a simple otast t oavoid exceptions
            else
                makeToast(getApplicationContext(), "Οι Καστροπολιτείες χρειάζονται σύνδεση στο διαδίκτυο για να λειτουργήσουν. Παρακαλώ συνδέσου και ξαναπροσπάθησε!");
        }
    }

    //round image
    private void makeRound(int img, ImageView logo)
    {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),img);
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(),icon);
        dr.setCornerRadius(Math.max(icon.getWidth(), icon.getHeight()) / 2.0f);
        logo.setBackground(dr);

    }

    //method that check if connected user has answered questionnaire
    private void checkQ(FirebaseUser currentUser){

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        cuid = currentUser.getUid();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("questionnaire").hasChild(cuid)) {
                    Log.v(TAG,"User has answered questionnaire!");
                    found = true;
                }
                else {
                    Log.v(TAG,"User has not answered questionnaire!");
                    found = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Activity: " + TAG , "The read failed: " + databaseError.getMessage());
                makeToast(getApplicationContext(), "Παρακαλώ επανεκκινήστε την εφαρμογή");
            }
        });


        /*listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot ds : dataSnapshot.child("questionnaire").getChildren()){
                    if (String.valueOf(ds.getKey()).equals(cuid)){
                        found = true;
                        //den theloume na elegxoume pia an ehei apadisei o xristis sto questionnaire giati vrehtike
                        myRef.removeEventListener(listener);
                        break;
                    }
                }

                //an den vrethei, phgaine ton stin selida tou erwthmatologiou
                if(!found){
                    Log.v(TAG,"User has not answered questionnaire!");
                    Intent intent = new Intent(getApplicationContext(), Questionnaire.class);
                    startActivity(intent);
                    finish();

                }
                //alliws phgaine ton sthn arxiki selida
                else{
                    Log.v(TAG,"User has answered questionnaire!");
                    //den theloume na elegxoume pia an ehei apadisei o xristis sto questionnaire giati ehei vrethei
                    myRef.removeEventListener(listener);
                    Intent intent = new Intent(getApplicationContext(), HomePage.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("The read failed: " ,databaseError.getMessage());
                makeToast(getApplicationContext(), "Παρακαλώ κάντε επανεκκίνηση την εφαρμογή");
            }
        });*/
    }

}

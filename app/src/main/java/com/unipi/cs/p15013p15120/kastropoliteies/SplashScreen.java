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
    //variable to save whether user uses google account
    private SharedPreferences sp;

    Intent i;

    //variables to use in checkQ method
    String cuid;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference myRef;
    FirebaseAuth auth;
    ValueEventListener listener;

    //constructor for Dialog class to show a dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
    }
}

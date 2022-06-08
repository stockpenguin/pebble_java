package com.stockpenguin.pebble.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stockpenguin.pebble.R;
import com.stockpenguin.pebble.models.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {
    enum AuthMode {
        LOGIN, SIGNUP
    }

    /* switch between Login or Sign-Up */
    private AuthMode authMode = AuthMode.SIGNUP;

    /* instance of FirebaseAuth to create a User */
    private FirebaseAuth auth;

    /* instance of FirebaseDatabase */
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    /* Firebase Storage */
    private FirebaseStorage firebaseStorage;
    private StorageReference rootRef;
    private StorageReference pfpRef;

    /* UI views in activity_auth.xml */
    private EditText emailEditText;
    private EditText passwordEditText;
    private AppCompatButton authButton;
    private TextView authTextView;

    /*
     * onStart() is the visible lifetime of an activity,
     * it is called after onCreate() which is the entire lifetime of an activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        /*
         * on the start of this activity, we want to see if a user is already logged in. If so, we
           can go ahead and navigate directly to the PebbleActivity.
         * auth.getCurrentUser() will return null if one is not already logged in, so we can just
           use an if statement to check if one exists
         */
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(this, PebbleActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    /*
    onCreate() is called when the Activity is created and represents the entire lifetime of an
    Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        /* set color of Status Bar and Navigation Bar to Pastel Blue */
        getWindow().setStatusBarColor(getColor(R.color.pastel_blue));
        getWindow().setNavigationBarColor(getColor(R.color.pastel_blue));

        /* initialize the singleton instance of FirebaseAuth */
        auth = FirebaseAuth.getInstance();

        /* initialize the singleton instance of FirebaseDatabase */
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        /* Firebase Storage */
        firebaseStorage = FirebaseStorage.getInstance("gs://pebble-c46e0.appspot.com/");
        rootRef = firebaseStorage.getReference();
        pfpRef = rootRef.child("pfp");

        /* initialize the views by linking them to the ones in activity_auth.xml */
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        authTextView = findViewById(R.id.authTextView);

        /* change to opposite AuthMode when the authTextView is tapped */
        authTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authMode == AuthMode.LOGIN) {
                    authMode = AuthMode.SIGNUP;
                    authButton.setText(getString(R.string.sign_up));
                    authTextView.setText(getString(R.string.or_login));
                } else {
                    authMode = AuthMode.LOGIN;
                    authButton.setText(getString(R.string.login));
                    authTextView.setText(getString(R.string.or_sign_up));
                }
            }
        });

        /*
        when the authButton is clicked, it will either login or signup a user based on whether
        authMode is set to Login or Signup
         */
        authButton = findViewById(R.id.authButton);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authMode == AuthMode.LOGIN) {
                    login();
                } else { signUp(); }
            }
        });
    }

    private void login() {
        /* set the String values email and password to the values within the EditTexts */
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        /* use auth to signIn to firebase, encrypt the password to store in the database */
        auth.signInWithEmailAndPassword(email, encryptPassword(password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            /* go to PebbleActivity */
                            /* create Intent i which will allow us to go to the next activity if
                            login is successful */
                            Intent i = new Intent(
                                    getApplicationContext(),
                                    PebbleActivity.class
                            );
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            /* start the next activity with Intent i */
                            startActivity(i);
                        } else {
                            /* display a message to the user of why there was an error */
                            Toast.makeText(
                                    getApplicationContext(),
                                    Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        /* store email and password input */
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        /* validate email and password */
        if (!validate(email, password)) return;

        auth.createUserWithEmailAndPassword(email, encryptPassword(password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /* go to PebbleActivity */
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;

                            /* set user email */
                            user.updateEmail(email);

                            /* automatically create a username when signing up */
                            String username;
                            try {
                                /* get text before the '@' in their inputted email address */
                                username = Objects.requireNonNull(
                                        user.getEmail()).split("@")[0];
                            } catch (NullPointerException e) {
                                /* otherwise just set it to the Uid if it is null */
                                username = user.getUid();
                            }

                            /* set username */
                            user.updateProfile(
                                    new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build()
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    /* add authenticated user to users database */
                                    createNewUser(user);
                                }
                            });

                            /* add authenticated user to users database */
//                            createNewUser(user);

                            /* create Intent i which will allow us to go to the next activity if
                            signUp is successful */
                            Intent i = new Intent(
                                    getApplicationContext(),
                                    PebbleActivity.class
                            );
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            /* go to next activity using Intent i */
                            startActivity(i);
                        } else {
                            /* show message to user on why the login failed */
                            Toast.makeText(getApplicationContext(),
                                    Objects.requireNonNull(task.getException()).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createNewUser(FirebaseUser user) {
        pfpRef.child("default_pfp.png").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        User newUser = new User(
                                user.getUid(),
                                user.getEmail(),
                                user.getDisplayName(),
                                uri.toString(),
                                System.currentTimeMillis()
                        );
                        usersRef.child(user.getUid()).setValue(newUser);

                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri).build());
                    }
                });
    }

    private boolean validate(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "please do not leave any empty fields",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(
                    this,
                    "password must be at least 6 characters",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.contains("@")) {
            Toast.makeText(
                    this,
                    "please enter a valid email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /* encrypt password into a byte[] using SHA-256 */
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] encryptedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHexString(encryptedPassword);
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    /* convert the encrypted byte[] into a hex string */
    private static String toHexString(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            hexString.append(hex.length() == 1 ? '0' : hex);
        }
        return hexString.toString();
    }
}
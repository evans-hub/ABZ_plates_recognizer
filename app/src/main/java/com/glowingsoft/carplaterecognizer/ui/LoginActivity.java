package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    CheckBox checkBox;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    private TextView forgot;
    ProgressDialog loading;
    private Button login;
    /* access modifiers changed from: private */
    public FirebaseAuth mAuth;
    private EditText passwords;
    private TextView signup;
    private EditText usernames;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.cirLoginButton);
        usernames = (EditText) findViewById(R.id.ktda_number_layout);
        passwords = (EditText) findViewById(R.id.editTextPassword);
       forgot = (TextView) findViewById(R.id.forgotPassword);
        loading = new ProgressDialog(this);
        checkBox = (CheckBox) findViewById(R.id.remember_me);
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, DashBoard.class));
            finish();
        }
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginUser();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               forgotPassword();
            }
        });
    }

    /* access modifiers changed from: private */
    public void forgotPassword() {
    }

    /* access modifiers changed from: private */
    public void loginUser() {
       loading.setTitle("Signing in");
       loading.setMessage("Please wait...");
        loading.setCanceledOnTouchOutside(false);
        final String username = usernames.getText().toString();
        final String password = passwords.getText().toString();
        if (password.isEmpty() || password.length() < 6) {
            passwords.setError("6 Characters and More Required");
        }
        if (username.length() < 3 || username.isEmpty()) {
            usernames.setError("invalid username");
            return;
        }
        loading.show();
       firebaseDatabase = FirebaseDatabase.getInstance();
       databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(username).child("email_address").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot != null) {
                    String email = (String) snapshot.getValue(String.class);
                    if (email != null) {
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loading.dismiss();
                                    SharedPreferences.Editor myEdit = getSharedPreferences("Pref", 0).edit();
                                    myEdit.putString("username", username);
                                    myEdit.commit();
                                    startActivity(new Intent(LoginActivity.this, DashBoard.class));
                                    finish();
                                    return;
                                }
                                String message = task.getException().toString();
                                loading.dismiss();

                                Toast.makeText(getApplicationContext(), "Login failed!! Please try again later" + message, Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }loading.dismiss();
                    Toast.makeText(LoginActivity.this, "No such user available", Toast.LENGTH_SHORT).show();
                    return;
                }loading.dismiss();
                Toast.makeText(LoginActivity.this, "No such user available", Toast.LENGTH_SHORT).show();
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Invalid Number", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

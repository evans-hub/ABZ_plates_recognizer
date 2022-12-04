package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.Entity.staff;
import com.glowingsoft.carplaterecognizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Registration extends AppCompatActivity {
    ImageView bac;
    EditText count;
    EditText email;

    /* renamed from: id  reason: collision with root package name */
    EditText f13id;
    ProgressDialog loading;
    EditText mode;
    EditText nam;
    EditText phon;
    EditText plate;
    DatabaseReference reference;
    EditText reside;
    Button signup;
    String status;

    @Override
    protected void onStart() {
        super.onStart();
        this.plate.setText(getSharedPreferences("Prof", 0).getString("plate", ""));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_registration);
        bac = (ImageView) findViewById(R.id.back);
       f13id = (EditText) findViewById(R.id.layout_id_number);
       nam = (EditText) findViewById(R.id.layout_name);
        phon = (EditText) findViewById(R.id.layout_mobile_Number);
        email = (EditText) findViewById(R.id.layout_email);
        this.count = (EditText) findViewById(R.id.layout_county);
        this.reside = (EditText) findViewById(R.id.layout_distance);
        this.mode = (EditText) findViewById(R.id.layout_model);
        this.plate = (EditText) findViewById(R.id.layout_plate);
        this.signup = (Button) findViewById(R.id.register);
        this.loading = new ProgressDialog(this);
        String staff_path = getIntent().getExtras().getString("database_path");

        if (staff_path.equalsIgnoreCase("staff")) {
            this.status = "temporary";
        }else if (staff_path.equalsIgnoreCase("visitors")) {
            this.status = "visitor";
        } else {
            this.status = "enabled";
        }
        this.reference = FirebaseDatabase.getInstance().getReference(staff_path);

        this.signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Registration.this.register_user();
            }
        });
        this.bac.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Registration.this.startActivity(new Intent(Registration.this.getApplicationContext(), DashBoard.class));
                Registration.this.finish();
            }
        });
    }

    /* access modifiers changed from: private */
    public void register_user() {
        SimpleDateFormat dateF = new SimpleDateFormat("dd-MM-yyyy");
        String format = dateF.format(new Date());
        this.loading.setTitle("Signing Up");
        this.loading.setMessage("Please wait...");
        this.loading.setCanceledOnTouchOutside(false);
        String idNumber = this.f13id.getText().toString().trim();
        String phone_number = this.phon.getText().toString().trim();
        String county = this.count.getText().toString().trim();
        String name = this.nam.getText().toString().trim();
        String distance = this.reside.getText().toString().trim();
        String model = this.mode.getText().toString().trim();
        String carPlate = this.plate.getText().toString().trim();
        String emailAddress = this.email.getText().toString().trim();
        String amount="0";
        String pay="Unpaid";
        String state="disabled";
       int totalTimes=0;
        if (idNumber.isEmpty() || idNumber.length() < 7) {
            this.nam.setError("invalid id number");
        }
        if (phone_number.isEmpty() || phone_number.length() < 2) {
            this.phon.setError("invalid phone number");
        }
        if (county.isEmpty() || county.length() < 2) {
            this.count.setError("invalid county name");
        }
        if (name.isEmpty() || name.length() < 2) {
            this.nam.setError("invalid name");
        }
        if (distance.isEmpty() || distance.length() < 2) {
            this.reside.setError("invalid distance");
        }
        if (model.isEmpty() || model.length() < 2) {
            this.mode.setError("invalid model name");
        }
        if (carPlate.isEmpty() || carPlate.length() < 7) {
            this.plate.setError("invalid plate number");
        }
        if (emailAddress.isEmpty()) {
            String str = carPlate;
        } else if (emailAddress.length() < 2) {
            SimpleDateFormat simpleDateFormat = dateF;
            String str2 = carPlate;
        } else {
            this.loading.show();
            SimpleDateFormat simpleDateFormat2 = dateF;
            staff staffModel = new staff(idNumber, name, phone_number, county, emailAddress, carPlate, model, distance, this.status,amount,pay,totalTimes,state);
            this.reference.child(carPlate).setValue(staffModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        Registration.this.loading.dismiss();
                        Registration.this.startActivity(new Intent(Registration.this.getApplicationContext(), DashBoard.class));
                        Registration.this.finish();
                        return;
                    }
                    Registration.this.loading.dismiss();
                    String message = task.getException().toString();
                    Registration registration = Registration.this;
                    Toast.makeText(registration, "Failed to Add Person" + message, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        this.email.setError("invalid email");
    }
}

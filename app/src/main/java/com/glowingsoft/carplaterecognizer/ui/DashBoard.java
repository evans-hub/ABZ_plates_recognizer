package com.glowingsoft.carplaterecognizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.glowingsoft.carplaterecognizer.R;
import com.google.firebase.auth.FirebaseAuth;

public class DashBoard extends AppCompatActivity {

    CardView admin;
    AlertDialog.Builder builder;
    CardView bus;
    CardView logout;
    TextView profile;
    CardView settings;
    CardView staff;
    CardView visitors;

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.profile.setText(getSharedPreferences("Pref", 0).getString("username", ""));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_dash_board);
        this.staff = (CardView) findViewById(R.id.staff);
        this.bus = (CardView) findViewById(R.id.school_bus);
        this.visitors = (CardView) findViewById(R.id.visitor);
        this.logout = (CardView) findViewById(R.id.logout);
        this.settings = (CardView) findViewById(R.id.setting);
        this.admin = (CardView) findViewById(R.id.admin);
        this.profile = (TextView) findViewById(R.id.profilename);
        this.staff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this.getApplicationContext(), MainActivity.class);
                intent.putExtra("database_path", "staff");
                DashBoard.this.startActivity(intent);
            }
        });
        this.visitors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this.getApplicationContext(), MainActivity.class);
                intent.putExtra("database_path", "visitors");
                DashBoard.this.startActivity(intent);
            }
        });
        this.bus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this.getApplicationContext(), MainActivity.class);
                intent.putExtra("database_path", "buses");
                DashBoard.this.startActivity(intent);
            }
        });
        this.admin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!DashBoard.this.getSharedPreferences("Pref", 0).getString("username", "").contains("admin")) {
                    Toast.makeText(DashBoard.this, "User not authorized", Toast.LENGTH_SHORT).show();

                } else {
                    DashBoard.this.startActivity(new Intent(DashBoard.this.getApplicationContext(), AdminActivity.class));
                    DashBoard.this.finish();

                }
            }
        });
        this.settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DashBoard.this.startActivity(new Intent(DashBoard.this.getApplicationContext(), SettingActivity.class));
            }
        });
        this.logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                DashBoard dashBoard = DashBoard.this;
                dashBoard.builder = new AlertDialog.Builder(dashBoard);
                DashBoard.this.builder.setMessage("Confirm").setTitle("Log Out");
                DashBoard.this.builder.setMessage("Do you want to log out ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id2) {
                        auth.signOut();
                        DashBoard.this.startActivity(new Intent(DashBoard.this.getApplicationContext(), LoginActivity.class));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id2) {
                        dialog.dismiss();
                    }
                });
                DashBoard.this.builder.create().show();
            }
        });
    }
}
